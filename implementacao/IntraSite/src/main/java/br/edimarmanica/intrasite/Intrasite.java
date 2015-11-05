/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.configuration.General;
import br.edimarmanica.intrasite.rules.SetTemplates;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
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

    public void execute(Site site) {

        if (General.DEBUG) {
            System.out.println("Start INTRASITE");
            System.out.println(">> SetTemplates");
        }
        //Define template nodes and candidate value nodes
        SetTemplates st = new SetTemplates(site);
        st.execute();

        if (General.DEBUG) {
            System.out.println(">> GenerateRules");
        }
        //Generate candidate rules
        GenerateRules gr = new GenerateRules(site);
        gr.execute();
        Set<CypherRule> rules = gr.getRules();

//        if (General.DEBUG) {
//            System.out.println(">>>> Generated rules: " + rules.size());
//            System.out.println(">> NullValuesFilter");
//        }
//        //NullValuesFilter
//        NullValuesFilter nvfilter = new NullValuesFilter(site);
//        rules = nvfilter.filter(rules);
//
//        if (General.DEBUG) {
//            System.out.println(">>>> Remaining rules: " + rules.size());
//            System.out.println(">> TemplateNodesFilter");
//        }
//
//        //TEmplateNodesFilter
//        TemplateNodesFilter tnfilter = new TemplateNodesFilter(site);
//        rules = tnfilter.filter(rules);

        if (General.DEBUG) {
            System.out.println(">>>> Remaining rules: " + rules.size());
            System.out.println(">> ExtractValues");
        }
        // ExtractValues 
        ExtractValues extractor = new ExtractValues(site, rules);
        extractor.printExtractedValues();

        if (General.DEBUG) {
            System.out.println("End INTRASITE");
        }
    }
}
