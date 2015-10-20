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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Quando a base é muito grande. Divide a consulta. Fazendo uma consulta para
 * cada Unique_path distinto
 *
 * @author edimar
 */
public class GenerateRulesPartitioned {

    private Neo4jHandler neo4j;
    private Set<CypherRule> rules;
    private Site site;

    public GenerateRulesPartitioned(Site site) {
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

        while (iteratorUP.hasNext()) { //para cada CandValue com o mesmo UNIQUE_PATH
            Map<String, Object> map = iteratorUP.next();
            List<Long> ids = (List) map.get("ids");
            String in = "[";
            for (Long id : ids) {
                in += id + ",";
            }
            in = in.substring(0, in.length() - 1) + "]"; //tirando última virgula e colocando o ]

            //O problema dessa regra é que para um unique_path de valor só retorna um label (aquele mais próximo e com mais URLs). Isso pode dar problema na variação de template
            cypherQuery = "MATCH p=shortestpath((l:Template)-[*.." + IntrasiteExtraction.MAX_DISTANCE + "]-(v:CandValue)) WHERE id(v) in " + in + " WITH v.UNIQUE_PATH as UP_value, l.VALUE as label, l.UNIQUE_PATH as UP_label, length(p) as len, count(DISTINCT v.URL) AS qtd ORDER BY len, qtd DESC  RETURN UP_value, head(collect(label)[0..1]) as label, head(collect(UP_label)[0..1]) as UP_label ";
            Iterator<Map<String, Object>> iteratorLABEL = neo4j.executeCypher(cypherQuery);
            while (iteratorLABEL.hasNext()) {
                Map<String, Object> mapLabel = iteratorLABEL.next();
                CypherNotation cypherNotation = new CypherNotation(mapLabel.get("label").toString(), mapLabel.get("UP_label").toString(), mapLabel.get("UP_value").toString());
                rules.add(cypherNotation.getNotation());
            }
        }
    }

    public Set<CypherRule> getRules() {
        return rules;
    }

    public static void main(String[] args) {
        GenerateRulesPartitioned gr = new GenerateRulesPartitioned(br.edimarmanica.dataset.weir.book.Site.BOOKSANDEBOOKS);
        gr.execute();
        Set<CypherRule> rules = gr.getRules();

        int i = 0;
        for (CypherRule notacao : rules) {
            System.out.println(i + " : " + notacao.getQueryWithoutParameters());
            i++;
        }
    }
}
