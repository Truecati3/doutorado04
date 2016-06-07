/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manualflex;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.GroundTruth;
import br.edimarmanica.metrics.Results;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ComplementaryRule {

    private Site site;
    private Attribute attribute;
    private Map<String, Map<String, String>> allRules;
    private List<String> masterRuleIDs;
    private Map<String, String> masterRuleValues;

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
    public ComplementaryRule(Site site, Attribute attribute, Map<String, Map<String, String>> allRules, List<String> masterRuleIDs, Map<String, String> masterRuleValues) {
        this.site = site;
        this.attribute = attribute;
        this.allRules = allRules;
        this.masterRuleIDs = masterRuleIDs;
        this.masterRuleValues = masterRuleValues;

    }

    /**
     * Duas regras são consideradas disjuntas mesmo se extraem valores para as
     * mesmas páginas desde que extraiam o mesmo valor nas páginas
     * compartilhadas
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


            Set<String> intersection = new HashSet<>();
            intersection.addAll(masterRuleValues.keySet());
            intersection.retainAll(allRules.get(rule).keySet());

            /*
             * ignorando se as duas regras (master e complementar) extraem valores para as
             mesmas páginas desde que extraiam o mesmo valor nas páginas
             compartilhadas
             */
            int pagesWithEqualValue = 0;
            for (String pageID : intersection) {
                if (masterRuleValues.get(pageID).equals(allRules.get(rule).get(pageID))) {
                    pagesWithEqualValue++;
                }
            }
            
            if (intersection.size() == pagesWithEqualValue) {    
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
        GroundTruth groundTruth = GroundTruth.getInstance(site, attribute);
        groundTruth.load();

        double maxF1 = 0;
        String maxRule = null;

        for (String rule : disjointedRules) {

            RuleMetrics metrics = RuleMetrics.getInstance(site, getDiff(allRules.get(rule)), groundTruth.getGroundTruth());
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

    /**
     *
     * @return os valores extraídos pela regra complementar avaliada exceto os
     * valores já extraídos pela regra mestre
     */
    private Map<String, String> getDiff(Map<String, String> ruleValues) {
        Map<String, String> diff = new HashMap<>();

        for (String pageID : ruleValues.keySet()) {
            if (!masterRuleValues.containsKey(pageID)) {
                diff.put(pageID, ruleValues.get(pageID));
            }
        }
        return diff;
    }

    public static void main(String[] args) {

        
        Site site = br.edimarmanica.dataset.swde.camera.Site.BUY;
        Attribute attribute = br.edimarmanica.dataset.swde.camera.Attribute.PRICE;
        Results results = new Results(site);
        Map<String, Map<String, String>> allRules = results.loadAllRules(Paths.PATH_INTRASITE);

        List<String> masterRuleIDs = new ArrayList<>();
        masterRuleIDs.add("rule_11122.csv");
        Map<String, String> masterRuleValues = allRules.get(masterRuleIDs.get(0));
        ComplementaryRule rum = new ComplementaryRule(site, attribute, allRules, masterRuleIDs, masterRuleValues);
        try {
            System.out.println("Complemented Rule: " + rum.getComplementaryRule());//Esperado rule_303.csv
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(ComplementaryRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
