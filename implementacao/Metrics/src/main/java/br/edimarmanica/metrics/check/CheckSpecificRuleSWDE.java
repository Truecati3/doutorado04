/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.swde.GroundTruthSwde;
import br.edimarmanica.metrics.swde.ResultsSWDE;
import br.edimarmanica.metrics.swde.RuleMetricsSwde;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class CheckSpecificRuleSWDE {

    private Site site;
    private Attribute attribute;
    private String ruleID;

    public CheckSpecificRuleSWDE(Site site, Attribute attribute, String ruleID) {
        this.site = site;
        this.attribute = attribute;
        this.ruleID = ruleID;
    }

    public void printInfo() throws SiteWithoutThisAttribute {
        ResultsSWDE results = new ResultsSWDE(site);
        Map<Integer, String> ruleValues = results.loadRule(new File(Paths.PATH_INTRASITE + site.getPath() + "/extracted_values/" + ruleID));

        GroundTruthSwde groundTruth = new GroundTruthSwde(site, attribute);
        groundTruth.load();

        RuleMetricsSwde metrics = new RuleMetricsSwde(ruleValues, groundTruth.getGroundTruth());
        metrics.computeMetrics();

        System.out.println("Recall: " + metrics.getRecall());
        System.out.println("Precision: " + metrics.getPrecision());

        System.out.println("****** Relevants not retrieved (Problemas de Recall)");
        for (Integer rel : groundTruth.getGroundTruth().keySet()) {
            if (!metrics.getIntersection().contains(rel)) {
                System.out.println("Faltando [" + groundTruth.getGroundTruth().get(rel) + "] na página: " + rel);
            }
        }

        System.out.println("****** Irrelevants retrieved (Problemas de precision)");
        for (Integer ret : ruleValues.keySet()) {
            if (!metrics.getIntersection().contains(ret)) {
                System.out.println("Faltando [" + ruleValues.get(ret) + "] na página: " + ret);
            }
        }

    }

    public static void main(String[] args) throws SiteWithoutThisAttribute {
        Site site = br.edimarmanica.dataset.swde.camera.Site.NEWEGG;
        Attribute attribute = br.edimarmanica.dataset.swde.camera.Attribute.PRICE;
        String ruleID = "rule_286.csv";
        CheckSpecificRuleSWDE check = new CheckSpecificRuleSWDE(site, attribute, ruleID);
        check.printInfo();
    }
}
