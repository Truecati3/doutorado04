/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.GroundTruth;
import br.edimarmanica.metrics.Results;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
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
public class ComplementaryRule {

    private Site site;
    private Attribute attribute;
    private Map<String, Map<String, String>> allRules;
    private List<String> masterRuleIDs;
    private Map<String, String> masterRuleValues;
    private int nrPagesSite;

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
    public ComplementaryRule(Site site, Attribute attribute, Map<String, Map<String, String>> allRules, List<String> masterRuleIDs, Map<String, String> masterRuleValues, int nrPagesSite) {
        this.site = site;
        this.attribute = attribute;
        this.allRules = allRules;
        this.masterRuleIDs = masterRuleIDs;
        this.masterRuleValues = masterRuleValues;
        this.nrPagesSite = nrPagesSite;
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

            if (nrPagesSite < masterRuleValues.size() + allRules.get(rule).size()) {
                continue;
            }

            Set<String> intersection = new HashSet<>();
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
        GroundTruth groundTruth = GroundTruth.getInstance(site, attribute);
        groundTruth.load();

        double maxF1 = 0;
        String maxRule = null;

        for (String rule : disjointedRules) {

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

    public static void main(String[] args) {

        /**
         * Teste SWDE *
         */
//        
        /**
         * Teste WEIR *
         */
        Site site = br.edimarmanica.dataset.weir.book.Site.BLACKWELL;
        Attribute attribute = br.edimarmanica.dataset.weir.book.Attribute.TITLE;
        Results results = new Results(site);
        Map<String, Map<String, String>> allRules = results.loadAllRules(Paths.PATH_INTRASITE);

        List<String> masterRuleIDs = new ArrayList<>();
        masterRuleIDs.add("rule_263.csv");
        Map<String, String> masterRuleValues = allRules.get(masterRuleIDs.get(0));
        ComplementaryRule rum = new ComplementaryRule(site, attribute, allRules, masterRuleIDs, masterRuleValues, UnionRules.getNrPages(site));
        try {
            System.out.println("Complemented Rule: " + rum.getComplementaryRule());//Esperado rule_303.csv
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(ComplementaryRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
