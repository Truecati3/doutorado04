/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.weir;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.weir.GroundTruthWeir;
import br.edimarmanica.metrics.weir.ResultsWeir;
import br.edimarmanica.metrics.weir.RuleMetricsWeir;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class MasterRuleWeir {

    private Site site;
    private Attribute attribute;
    private Map<String, Set<String>> allRules;

    public MasterRuleWeir(Site site, Attribute attribute, Map<String, Set<String>> allRules) {
        this.site = site;
        this.attribute = attribute;
        this.allRules = allRules;
    }

    /**
     *
     * @return ID of the master rule (rule with the largest F1)
     */
    public String getMasterRule() throws SiteWithoutThisAttribute {
        GroundTruthWeir groundTruth = new GroundTruthWeir(site, attribute);
        groundTruth.load();

        double maxF1 = 0;
        String maxRule = null;

        for (String rule : allRules.keySet()) {

            RuleMetrics metrics = new RuleMetricsWeir(allRules.get(rule), groundTruth.getGroundTruth());
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
        Site site = br.edimarmanica.dataset.weir.book.Site.BLACKWELL;
        Attribute attribute = br.edimarmanica.dataset.weir.book.Attribute.TITLE;
        ResultsWeir results = new ResultsWeir(site);
        Map<String, Set<String>> allRules = results.loadAllRules();

        MasterRuleWeir master = new MasterRuleWeir(site, attribute, allRules);
        try {
            System.out.println(master.getMasterRule()); //rule_120.csv
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(MasterRuleWeir.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
