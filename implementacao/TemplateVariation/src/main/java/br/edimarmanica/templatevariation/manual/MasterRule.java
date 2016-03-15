/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.GroundTruth;
import br.edimarmanica.metrics.Results;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class MasterRule {

    private Site site;
    private Attribute attribute;
    private Map<String, Map<String, String>> allRules;

    public MasterRule(Site site, Attribute attribute, Map<String, Map<String, String>> allRules) {
        this.site = site;
        this.attribute = attribute;
        this.allRules = allRules;
    }

    /**
     *
     * @return ID of the master rule (rule with the largest F1)
     */
    public String getMasterRule() throws SiteWithoutThisAttribute {
        GroundTruth groundTruth = GroundTruth.getInstance(site, attribute);
        groundTruth.load();

        double maxF1 = 0;
        String maxRule = null;

        for (String rule : allRules.keySet()) {

            RuleMetrics metrics = RuleMetrics.getInstance(site, allRules.get(rule), groundTruth.getGroundTruth());
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
}
