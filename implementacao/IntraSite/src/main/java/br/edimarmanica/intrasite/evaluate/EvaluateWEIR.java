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
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.weir.GroundTruthWeir;
import br.edimarmanica.metrics.weir.PrinterWeir;
import br.edimarmanica.metrics.weir.ResultsWeir;
import br.edimarmanica.metrics.weir.RuleMetricsWeir;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class EvaluateWEIR {

    private Site site;
    private Labels labels;
    private Map<String, Set<String>> myResults; //<RuleName,Set<ExtractedValues>>
    private PrinterWeir printer;

    public EvaluateWEIR(Site site) {
        this.site = site;
    }

    private void printMetrics(Attribute attribute) throws SiteWithoutThisAttribute {
        GroundTruthWeir groundTruth = new GroundTruthWeir(site, attribute);
        groundTruth.load();

        double maxRecall = 0;
        double maxPrecision = 0;
        double maxF1 = 0;
        int maxRelevantsRetrieved = 0;
        String maxRule = null;
        Set<String> maxExtractValues = new HashSet<>();

        for (String rule : myResults.keySet()) {

            RuleMetrics metrics = new RuleMetricsWeir(myResults.get(rule), groundTruth.getGroundTruth());
            metrics.computeMetrics();

            if (metrics.getF1() == 0) {
                continue;
            }

            if (metrics.getF1() > maxF1) {
                maxF1 = metrics.getF1();
                maxRecall = metrics.getRecall();
                maxPrecision = metrics.getPrecision();
                maxRule = rule;

                maxRelevantsRetrieved = metrics.getRelevantRetrieved();
                maxExtractValues = myResults.get(rule);
            }
        }

        if (maxRecall == 0) {
            maxRule = "Attribute not found";
        }

        printer.print(attribute, maxRule, labels.getLabels().get(maxRule), groundTruth.getGroundTruth(), maxExtractValues, maxRecall, maxPrecision, maxRelevantsRetrieved);
    }

    public void printMetrics() {

        labels = new Labels(site);
        labels.load();

        ResultsWeir results = new ResultsWeir(site);
        myResults = results.loadAllRules();

        printer = new PrinterWeir(site, Paths.PATH_INTRASITE);

        for (Attribute attr : site.getDomain().getAttributes()) {
            try {
                printMetrics(attr);
            } catch (SiteWithoutThisAttribute ex) {
                //Logger.getLogger(EvaluateWEIR.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public static void main(String[] args) {
        //edition não pega pq identifica como label (template)
        Domain domain = br.edimarmanica.dataset.weir.Domain.BOOK;
        for (Site site : domain.getSites()) {
            if (site != br.edimarmanica.dataset.weir.book.Site.AMAZON) {
                continue;
            }
            EvaluateWEIR eval = new EvaluateWEIR(site);
            eval.printMetrics();
        }
    }
}