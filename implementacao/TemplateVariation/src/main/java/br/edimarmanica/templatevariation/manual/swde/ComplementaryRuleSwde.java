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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class ComplementaryRuleSwde {

    private Site site;
    private Attribute attribute;
    private Map<String, Map<Integer, String>> allRules;
    private List<String> masterRuleIDs;
    private Map<Integer,String> masterRuleValues;

    /**
     *
     * @param site
     * @param attribute
     * @param allRules todas as regras geradas para o site
     * @param masterRuleIDs id da masterRule - A masterRule é a regra com maior
     * F1 para um atributo em um site. É um set pois pode já ter sido adicionada
     * mais alguma regra complementar
     * @param masterRuleValues conjunto resultado da união do conjunto de
     * valores das regras contidas em masterRuleIds
     */
    public ComplementaryRuleSwde(Site site, Attribute attribute, Map<String, Map<Integer,String>> allRules, List<String> masterRuleIDs, Map<Integer, String> masterRuleValues) {
        this.site = site;
        this.attribute = attribute;
        this.allRules = allRules;
        this.masterRuleIDs = masterRuleIDs;
        this.masterRuleValues = masterRuleValues;
    }

    /**
     * encontre as regras com intersecção (conjunto de páginas que extraí valor
     * não nulo) vazia com a masterRule
     *
     * @param masterRule = melhor regra para um atributo em um site
     * @return Set<RuleID>
     */
    private Set<String> getDisjointRules() {
        Set<String> disjointRules = new HashSet<>();

        for (String rule : allRules.keySet()) {
            if (masterRuleIDs.contains(rule)) {
                continue;
            }

            Set<Integer> intersection = new HashSet<>();
            intersection.addAll(masterRuleValues.keySet());
            intersection.retainAll(allRules.get(rule).keySet());
            if (intersection.isEmpty()) {
                disjointRules.add(rule);
            }
        }
        return disjointRules;
    }

    /**
     * @return the disjoint rule with the highest eficcacy
     */
    public String getComplementaryRule() throws SiteWithoutThisAttribute {

        Set<String> disjointedRules = getDisjointRules();
        GroundTruthSwde groundTruth = new GroundTruthSwde(site, attribute);
        groundTruth.load();

        double maxF1 = 0;
        String maxRule = null;

        for (String rule : disjointedRules) {

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
        Site site = br.edimarmanica.dataset.swde.camera.Site.AMAZON;
        Attribute attribute = br.edimarmanica.dataset.swde.camera.Attribute.MANUFECTURER;
        ResultsSWDE results = new ResultsSWDE(site);
        Map<String, Map<Integer,String>> allRules = results.loadAllRules();

        List<String> masterRuleIDs = new ArrayList<>();
        masterRuleIDs.add("rule_1100.csv");
        Map<Integer,String> masterRuleValues = allRules.get("rule_1100.csv");

        ComplementaryRuleSwde rum = new ComplementaryRuleSwde(site, attribute, allRules, masterRuleIDs, masterRuleValues);
        try {
            System.out.println("Complemented Rule: " + rum.getComplementaryRule());
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(ComplementaryRuleSwde.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
