/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.GroundTruth;
import br.edimarmanica.metrics.Results;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import java.io.File;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class CheckSpecificRule {

    private Site site;
    private Attribute attribute;
    private String ruleID;

    public CheckSpecificRule(Site site, Attribute attribute, String ruleID) {
        this.site = site;
        this.attribute = attribute;
        this.ruleID = ruleID;
    }

    public void printInfo() throws SiteWithoutThisAttribute {
        Results results = new Results(site);
        Map<String, String> ruleValues = results.loadRule(new File(Paths.PATH_INTRASITE + site.getPath() + "/extracted_values/" + ruleID));

        GroundTruth groundTruth = GroundTruth.getInstance(site, attribute);
        groundTruth.load();

        RuleMetrics metrics = RuleMetrics.getInstance(site, ruleValues, groundTruth.getGroundTruth());
        metrics.computeMetrics();

        System.out.println("Recall: " + metrics.getRecall());
        System.out.println("Precision: " + metrics.getPrecision());
        System.out.println("F1: " + metrics.getF1());

        System.out.println("****** Relevants not retrieved (Problemas de Recall)");
        for (String rel : groundTruth.getGroundTruth().keySet()) {
            if (!metrics.getIntersection().contains(rel)) {
                System.out.println("Faltando [" + groundTruth.getGroundTruth().get(rel) + "] na página: " + rel);
            }
        }

        System.out.println("****** Irrelevants retrieved (Problemas de precision)");
        for (String ret : ruleValues.keySet()) {
            if (!metrics.getIntersection().contains(ret)) {
                System.out.println("Faltando [" + ruleValues.get(ret) + "] na página: " + ret);
            }
        }

    }

    public static void main(String[] args) throws SiteWithoutThisAttribute {
        /**
         * * Testando SWDE **
         */
//        Site site = br.edimarmanica.dataset.swde.book.Site.AMAZON;
//        Attribute attribute = br.edimarmanica.dataset.swde.book.Attribute.AUTHOR;
//        String ruleID = "rule_10636.csv";//recall = 0.7345 ---> precision = 1.0
//        CheckSpecificRule check = new CheckSpecificRule(site, attribute, ruleID);
//        check.printInfo();
        /**
         * * Testando WEIR **
         */
//        Site site = br.edimarmanica.dataset.weir.book.Site.BARNESANDNOBLE;
//        Attribute attribute = br.edimarmanica.dataset.weir.book.Attribute.EDITION;
//        String ruleID = "rule_94.csv";//recall = 0.793 -- precision = 0.489
//        CheckSpecificRule check = new CheckSpecificRule(site, attribute, ruleID);
//        check.printInfo();
        
        
        Site site = br.edimarmanica.dataset.weir.videogame.Site.TEAMBOX;
        Attribute attribute = br.edimarmanica.dataset.weir.videogame.Attribute.PUBLISHER;
        String ruleID = "rule_4686.csv";
        CheckSpecificRule check = new CheckSpecificRule(site, attribute, ruleID);
        check.printInfo();

    }
}
