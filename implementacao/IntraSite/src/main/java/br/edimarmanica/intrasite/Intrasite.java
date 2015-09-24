/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class Intrasite {
    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.weir.book.Site.AMAZON;
        Neo4jHandlerType type = Neo4jHandlerType.LOCAL;
        /* Define template nodes and candidate value nodes
         * SetTemplates st = new SetTemplates();
        st.execute(); */
        
        /** Generate candidate rules **/
        GenerateRules gr = new GenerateRules(site, type);
        gr.execute();
        Set<String> rules = gr.getRules();
        
        /** NullValuesFilter **/
        NullValuesFilter nvfilter = new NullValuesFilter(site, type);
        rules = nvfilter.filter(rules);
        
        /** TEmplateNodesFilter **/
        TemplateNodesFilter tnfilter = new TemplateNodesFilter(site, type);
        rules = tnfilter.filter(rules);
        
        for(String rule: rules){
            System.out.println(rule);
            System.out.println("");
        }
    }
}
