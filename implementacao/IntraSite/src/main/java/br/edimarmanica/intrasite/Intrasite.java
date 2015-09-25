/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.intrasite.rules.SetTemplates;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;
import br.edimarmanica.intrasite.extract.ExtractValues;
import br.edimarmanica.intrasite.filter.NullValuesFilter;
import br.edimarmanica.intrasite.filter.TemplateNodesFilter;
import br.edimarmanica.intrasite.rules.GenerateRules;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class Intrasite {
    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.weir.book.Site.BOOKSANDEBOOKS;
        Neo4jHandlerType type = Neo4jHandlerType.LOCAL;
       
        //Define template nodes and candidate value nodes
//        System.out.println("Set templates");
//        SetTemplates st = new SetTemplates(site, type);
//        st.execute(); 
        
        /** Generate candidate rules **/
        System.out.println("Generate rules");
        GenerateRules gr = new GenerateRules(site, type);
        gr.execute();
        Set<CypherRule> rules = gr.getRules();
        
        /** NullValuesFilter **/
        System.out.println("Nullvalues filter");
        NullValuesFilter nvfilter = new NullValuesFilter(site, type);
        rules = nvfilter.filter(rules);
        
        /** TEmplateNodesFilter **/
        System.out.println("Templatenodes filter");
        TemplateNodesFilter tnfilter = new TemplateNodesFilter(site, type);
        rules = tnfilter.filter(rules);
        
        /*** ExtractValues **/
        System.out.println("Extract Values");
        ExtractValues extractor = new ExtractValues(site, type, rules);
        extractor.printExtractedValues();
    }
}
