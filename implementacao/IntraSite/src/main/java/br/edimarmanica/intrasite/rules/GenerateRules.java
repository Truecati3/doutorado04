/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.rules;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.CypherNotation;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandler;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class GenerateRules {

    private static final int MAX_LENGHT = 7;//max lenght between the value and the label
    private Neo4jHandler neo4j;
    private Set<CypherRule> rules;
    private Site site;
    private Neo4jHandlerType type;

    public GenerateRules(Site site, Neo4jHandlerType type) {
        this.site = site;
        this.type = type;
    }

    public void execute() {
        neo4j = Neo4jHandler.getInstance(type, site);
        generate();
        neo4j.shutdown();
    }

    private void generate() {
        rules = new HashSet<>();

        //selecionar o nodo template mais próximo para cada CandValue
        // O problema dessa regra é que pega mais de um nodo template para o mesmo nodo valor
        //String cypherQuery = "MATCH p=shortestpath((l:Template)-[*.." + MAX_LENGHT + "]-(v:CandValue)) RETURN DISTINCT length(p) as lenght, l.VALUE as label, l.UNIQUE_PATH as UP_label, v.UNIQUE_PATH as UP_value LIMIT 3";
        
        //O problema dessa regra é que para um unique_path de valor só retorna um label (aquele mais próximo e com mais URLs). Isso pode dar problema na variação de template
        String cypherQuery = "MATCH p=shortestpath((l:Template)-[*..7]-(v:CandValue)) WITH v.UNIQUE_PATH as UP_value, l.VALUE as label, l.UNIQUE_PATH as UP_label, length(p) as len, count(DISTINCT v.URL) AS qtd ORDER BY len, qtd DESC  RETURN UP_value, head(collect(label)[0..1]) as label, head(collect(UP_label)[0..1]) as UP_label LIMIT 3";
        Iterator<Map<String, Object>> iterator = neo4j.executeCypher(cypherQuery);
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            CypherNotation cypherNotation = new CypherNotation(map.get("label").toString(), map.get("UP_label").toString(), map.get("UP_value").toString());
            CypherRule notacao = cypherNotation.getNotation();
            rules.add(notacao);
        }
    }

    public Set<CypherRule> getRules() {
        return rules;
    }

    public static void main(String[] args) {
        GenerateRules gr = new GenerateRules(br.edimarmanica.dataset.weir.book.Site.BOOKSANDEBOOKS, Neo4jHandlerType.LOCAL);
        gr.execute();
        Set<CypherRule> rules = gr.getRules();

        int i = 0;
        for (CypherRule notacao : rules) {
            System.out.println(i + " : " + notacao.getQueryWithoutParameters());
        }
    }
}
