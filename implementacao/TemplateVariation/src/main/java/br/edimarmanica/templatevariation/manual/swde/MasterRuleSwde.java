/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.swde;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.swde.GroundTruthSwde;
import br.edimarmanica.metrics.swde.ResultsSWDE;
import br.edimarmanica.metrics.swde.RuleMetricsSwde;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class MasterRuleSwde {

    private Site site;
    private Attribute attribute;
    private Map<String, Map<Integer, String>> allRules;

    public MasterRuleSwde(Site site, Attribute attribute, Map<String, Map<Integer, String>> allRules) {
        this.site = site;
        this.attribute = attribute;
        this.allRules = allRules;
    }

    /**
     *
     * @return ID of the master rule (rule with the largest F1)
     */
    public String getMasterRule() throws SiteWithoutThisAttribute {
        GroundTruthSwde groundTruth = new GroundTruthSwde(site, attribute);
        groundTruth.load();

        double maxF1 = 0;
        String maxRule = null;

        for (String rule : allRules.keySet()) {

            RuleMetrics metrics = new RuleMetricsSwde(allRules.get(rule), groundTruth.getGroundTruth());
            metrics.computeMetrics();

            if (metrics.getF1() == 0) {
                continue;
            }

            if (metrics.getF1() > maxF1) {
                maxF1 = metrics.getF1();
                maxRule = rule;
            }
        }
        return maxRule;
    }

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.swde.book.Site.BOOKDEPOSITORY;
        Attribute attribute = br.edimarmanica.dataset.swde.book.Attribute.TITLE;
        ResultsSWDE results = new ResultsSWDE(site);
        Map<String, Map<Integer, String>> allRules = results.loadAllRules();

        MasterRuleSwde master = new MasterRuleSwde(site, attribute, allRules);
        try {
            System.out.println(master.getMasterRule()); //rule_227.csv
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(MasterRuleSwde.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
