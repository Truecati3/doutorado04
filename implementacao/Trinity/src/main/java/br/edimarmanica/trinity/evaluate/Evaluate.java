/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.evaluate;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.GroundTruth;
import br.edimarmanica.metrics.Labels;
import br.edimarmanica.metrics.Printer;
import br.edimarmanica.metrics.Results;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class Evaluate {

    private Site site;
    private Map<String, Map<String, String>> myResults = new HashMap<>();//<RuleName,Map<PageID,Value>>
    private Printer printer;

    public Evaluate(Site site) {
        this.site = site;
    }

    private void printMetrics(Attribute attribute) throws SiteWithoutThisAttribute {
        GroundTruth groundTruth = GroundTruth.getInstance(site, attribute);
        groundTruth.load();

        if (groundTruth.getGroundTruth().isEmpty()) {
            return;//não tem esse atributo no gabarito
        }

        double maxRecall = 0;
        double maxPrecision = 0;
        double maxF1 = 0;
        int maxRelevantsRetrieved = 0;
        String maxRule = null;
        Map<String, String> maxExtractValues = new HashMap<>();
        Set<String> maxIntersection = new HashSet<>();

        for (String rule : myResults.keySet()) {//para cada regra
            RuleMetrics metrics = RuleMetrics.getInstance(site, myResults.get(rule), groundTruth.getGroundTruth());
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

        printer.print(attribute, maxRule, "", groundTruth.getGroundTruth(), maxExtractValues, maxIntersection, maxRecall, maxPrecision, maxRelevantsRetrieved);
    }

    public void printMetrics() {

        Results results = new Results(site);
        myResults = results.loadAllRules(Paths.PATH_TRINITY);

        printer = new Printer(site, Paths.PATH_TRINITY);

        for (Attribute attr : site.getDomain().getAttributes()) {
            try {
                printMetrics(attr);
            } catch (SiteWithoutThisAttribute ex) {
                // Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {

        for (Dataset dataset : Dataset.values()) {
            System.out.println("\tDataset: " + dataset);
            for (Domain domain : dataset.getDomains()) {
                /*if (domain != br.edimarmanica.dataset.weir.Domain.VIDEOGAME) {
                    continue;
                }*/

                System.out.println("\tDomain: " + domain);
                for (Site site : domain.getSites()) {
                    if (site != br.edimarmanica.dataset.weir.book.Site.BOOKMOOCH) {
                        continue;
                    }

                    try {
                        System.out.println("\t\tSite: " + site);
                        Evaluate eval = new Evaluate(site);
                        eval.printMetrics();
                    } catch (Exception ex) {
                        System.out.println("\t\t\tIgnorando");
                    }
                }
            }
        }

        // Evaluate eval = new Evaluate(br.edimarmanica.dataset.swde.restaurant.Site.USDINNERS);
        // eval.printMetrics();
    }
}
