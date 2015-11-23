/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.rules;

import br.edimarmanica.configuration.IntrasiteExtraction;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.CypherNotation;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Quando a base é muito grande, divide a consulta. Fazendo uma consulta para
 * cada Unique_path distinto
 *
 * @author edimar
 */
public class GenerateRules {

    private Neo4jHandler neo4j;
    private Set<CypherRule> rules;
    private Site site;

    public GenerateRules(Site site) {
        this.site = site;
    }

    public void execute() {
        neo4j = Neo4jHandler.getInstance(site);
        generate();
        neo4j.shutdown();
    }

    private void generate() {
        rules = new HashSet<>();

        //Seleciona os CandValue com o mesmo UNIQUE_PATH
        String cypherQuery = "match (v:CandValue) return v.UNIQUE_PATH as UP_value, collect(id(v)) as ids";
        Iterator<Map<String, Object>> iteratorUP = neo4j.executeCypher(cypherQuery);
        int i = 0;
        while (iteratorUP.hasNext()) { //para cada CandValue com o mesmo UNIQUE_PATH
            System.out.println("AKi: " + i);
            i++;
            Map<String, Object> map = iteratorUP.next();
            List<Long> ids = (List) map.get("ids");

            for (Long id : ids) {//pq rodar com ids juntos pegava labels errados e ficava com aqueles
                cypherQuery = "MATCH (v:CandValue) WHERE id(v) = {id} WITH v MATCH p=shortestpath((l:Template{URL:v.URL})-[*.." + IntrasiteExtraction.MAX_DISTANCE + "]-(v)) WITH v.UNIQUE_PATH as UP_value, l.VALUE as label, l.UNIQUE_PATH as UP_label, length(p) as len, count(DISTINCT v.URL) AS qtd ORDER BY len, qtd DESC  RETURN UP_value, head(collect(label)[0..1]) as label, head(collect(UP_label)[0..1]) as UP_label ";
                Map<String, Object> params = new HashMap<>();
                params.put("id", id);
                Iterator<Map<String, Object>> iteratorLABEL = neo4j.executeCypher(cypherQuery, params);
                while (iteratorLABEL.hasNext()) {
                    Map<String, Object> mapLabel = iteratorLABEL.next();
                    CypherNotation cypherNotation = new CypherNotation(mapLabel.get("label").toString(), mapLabel.get("UP_label").toString(), mapLabel.get("UP_value").toString());
                    rules.add(cypherNotation.getNotation());
                    System.out.println("Testando: " + cypherNotation.getNotation().getQueryWithoutParameters());
                }
            }
        }
    }

    public Set<CypherRule> getRules() {
        return rules;
    }

    public static void main(String[] args) {
        GenerateRules gr = new GenerateRules(br.edimarmanica.dataset.weir.book.Site.BOOKSANDEBOOKS);
        gr.execute();
        Set<CypherRule> rules = gr.getRules();

        int i = 0;
        for (CypherRule notacao : rules) {
            System.out.println(i + " : " + notacao.getQueryWithoutParameters());
            i++;
        }
    }
}
