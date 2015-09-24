/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.CypherNotation;
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
    private Set<String> rules;
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
        rules = new HashSet<String>();

        //selecionar o nodo template mais próximo para cada CandValue
        String cypherQuery = "MATCH p=shortestpath((l:Template)-[*.." + MAX_LENGHT + "]-(v:CandValue)) RETURN length(p) as lenght, l.VALUE as label, l.UNIQUE_PATH as UP_label, v.UNIQUE_PATH as UP_value";
        Iterator<Map<String, Object>> iterator = neo4j.executeCypher(cypherQuery);
        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();
            String notacao = CypherNotation.getNotation(map.get("label").toString(), map.get("UP_label").toString(), map.get("UP_value").toString());
            rules.add(notacao);
        }
    }

    public Set<String> getRules() {
        return rules;
    }

    public static void main(String[] args) {
        GenerateRules gr = new GenerateRules(br.edimarmanica.dataset.weir.book.Site.AMAZON, Neo4jHandlerType.LOCAL);
        gr.execute();
        Set<String> rules = gr.getRules();
        for (String notacao : rules) {
            System.out.println(notacao);
        }
    }
}
