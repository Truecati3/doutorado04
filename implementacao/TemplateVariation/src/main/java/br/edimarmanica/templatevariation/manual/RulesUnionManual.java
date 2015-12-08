/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.intrasite.evaluate.EvaluateWEIR;
import br.edimarmanica.intrasite.evaluate.SiteWithoutThisAttribute;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.load.LoadRules;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class RulesUnionManual {

    private Site site;
    private Set<Rule> siteRules;

    public RulesUnionManual(Site site) {
        this.site = site;

        LoadRules lr = new LoadRules(site);
        siteRules = lr.getRules();
    }

    public Rule getRule(int ruleID) {
        Rule rule = null;
        for (Rule r : siteRules) {
            if (r.getRuleID() == ruleID) {
                rule = r;
                break;
            }
        }
        return rule;
    }

    public Rule getComplementaryRule(int masterRuleID, Attribute attribute) throws SiteWithoutThisAttribute {
        return getComplementaryRule(getRule(masterRuleID), attribute);
    }

    //encontre as regras com intersecção (conjunto de páginas que extraí valor não nulo) vazia
    private Set<Rule> getDisjointRules(Rule masterRule) {
        Set<Rule> disjointRules = new HashSet<>();

        for (Rule rule : siteRules) {
            if (masterRule == rule) {
                continue;
            }

            if (masterRule.getNrNotNullValues() + rule.getNrNotNullValues() > masterRule.getValues().size()) {
                continue;
            }

            Set<String> intersection = new HashSet<>();
            intersection.addAll(masterRule.getPagesWithNotNullValues());
            intersection.retainAll(rule.getPagesWithNotNullValues());
            if (intersection.isEmpty()) {
                disjointRules.add(rule);
            }
        }
        return disjointRules;
    }

    /**
     *
     * @param masterRule
     * @param rules
     * @param attribute
     * @return the disjoint rule with the highest eficcacy
     */
    public Rule getComplementaryRule(Rule masterRule, Attribute attribute) throws SiteWithoutThisAttribute {

        if (masterRule.getValues().size() == masterRule.getNrNotNullValues()) {
            return null; //regra completa
        }

        Set<Rule> disjointedRules = getDisjointRules(masterRule);

        Set<String> groundTruth = EvaluateWEIR.loadGroundTruth(site, attribute);

        double maxRecall = 0;
        double maxPrecision = 0;
        double maxF1 = 0;
        int maxRetrieved = 0;
        int maxRelevantsRetrieved = 0;
        Rule maxRule = null;
        Set<String> maxExtractValues = new HashSet<>();

        for (Rule rule : disjointedRules) {

            Set<String> intersection = new HashSet<>();
            intersection.addAll(Controller.getMyResults(site, rule.getRuleID()));
            intersection.retainAll(groundTruth);

            double recall = (double) intersection.size() / groundTruth.size();
            double precision = (double) intersection.size() / rule.getNotNullValues().size();

            if (recall == 0 || precision == 0) {
                continue;
            }
            double F1 = (2 * (recall * precision)) / (recall + precision);

            if (F1 > maxF1) {
                maxF1 = F1;
                maxRecall = recall;
                maxPrecision = precision;
                maxRule = rule;
            }
        }


        return maxRule;
    }

    

    public static void main(String[] args) {
        RulesUnionManual rum = new RulesUnionManual(br.edimarmanica.dataset.weir.book.Site.BLACKWELL);
        try {
            System.out.println("Complemented Rule: " + rum.getComplementaryRule(120, br.edimarmanica.dataset.weir.book.Attribute.TITLE).getRuleID());
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(RulesUnionManual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
