/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.evaluate;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Labels;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.swde.GroundTruthSwde;
import br.edimarmanica.metrics.swde.PrinterSwde;
import br.edimarmanica.metrics.swde.ResultsSWDE;
import br.edimarmanica.metrics.swde.RuleMetricsSwde;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class EvaluateSWDE {

    private Site site;
    private Map<String, Map<Integer, String>> myResults = new HashMap<>();//<RuleName,Map<PageID,Value>>
    private Labels labels;
    private PrinterSwde printer;

    public EvaluateSWDE(Site site) {
        this.site = site;
    }

    private void printMetrics(Attribute attribute) throws SiteWithoutThisAttribute {
        GroundTruthSwde groundTruth = new GroundTruthSwde(site, attribute);
        groundTruth.load();
        
        if (groundTruth.getGroundTruth().isEmpty()){
            return;//não tem esse atributo no gabarito
        }

        double maxRecall = 0;
        double maxPrecision = 0;
        double maxF1 = 0;
        int maxRelevantsRetrieved = 0;
        String maxRule = null;
        Map<Integer, String> maxExtractValues = new HashMap<>();
        Set<Integer> maxIntersection = new HashSet<>();

        for (String rule : myResults.keySet()) {//para cada regra

            RuleMetricsSwde metrics = new RuleMetricsSwde(myResults.get(rule), groundTruth.getGroundTruth());
            metrics.computeMetrics();

            if (metrics.getF1() > maxF1) {
                maxF1 = metrics.getF1();
                maxRecall = metrics.getRecall();
                maxPrecision = metrics.getPrecision();
                maxRule = rule;

                maxRelevantsRetrieved = metrics.getRelevantRetrieved();
                maxExtractValues = myResults.get(rule);
                maxIntersection = metrics.getIntersection();
            }
        }

        if (maxRecall == 0) {
            maxRule = "Attribute not found";
        }

        printer.print(attribute, maxRule, labels.getLabels().get(maxRule), groundTruth.getGroundTruth(), maxExtractValues, maxIntersection, maxRecall, maxPrecision, maxRelevantsRetrieved);
    }

    public void printMetrics() {
        labels = new Labels(site);
        labels.load();

        ResultsSWDE results = new ResultsSWDE(site);
        myResults = results.loadAllRules();

        printer = new PrinterSwde(site, Paths.PATH_INTRASITE);

        for (Attribute attr : site.getDomain().getAttributes()) {
            try {
                printMetrics(attr);
            } catch (SiteWithoutThisAttribute ex) {
                Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {

        //edition não pega pq identifica como label (template)
        Domain domain = br.edimarmanica.dataset.swde.Domain.NBA_PLAYER;
        for (Site site : domain.getSites()) {
            if (site != br.edimarmanica.dataset.swde.nba.Site.YAHOO){
                continue;
            }
            
            System.out.println("Site: " + site);
            EvaluateSWDE eval = new EvaluateSWDE(site);
            eval.printMetrics();
        }

        // EvaluateSWDE eval = new EvaluateSWDE(br.edimarmanica.dataset.swde.restaurant.Site.USDINNERS);
        // eval.printMetrics();
    }
}
