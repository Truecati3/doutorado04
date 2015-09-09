/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.evaluate.EvaluateWEIR;
import br.edimarmanica.expressiveness.extract.ExtractValues;
import static br.edimarmanica.expressiveness.generate.GenerateRules.printRules;

/**
 *
 * @author edimar
 */
public class Main {

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.weir.book.Site.ECAMPUS;
        //antes de tudo tem que carregar o site no neo4j e gerar os templates (nessa etapa ainda não precisa dos templates pois estou fazendo manualmente)
        /**
         * primeiro chama o generate rules*
         */
       // printRules(site);
        /**
         * depois o ExtractValues*
         */
       // ExtractValues extract = new ExtractValues(site);
       // extract.printExtractedValues();
        /**
         * Depois o Evaluate*
         */
        EvaluateWEIR eval = new EvaluateWEIR(site);
        eval.printMetrics();
    }
}
