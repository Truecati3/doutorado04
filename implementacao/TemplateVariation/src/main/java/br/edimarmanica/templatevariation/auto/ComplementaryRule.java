/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import br.edimarmanica.templatevariation.auto.load.LoadRule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class ComplementaryRule {

    private List<Integer> masterRulesInSite;
    private Map<Integer, Rule> allRulesInSite;
    private Set<Rule> masterRulesInOtherSites;
    private int nrPagesSite;

    /**
     *
     * @param masterRulesInSite -- contém os identificadores da master rule para
     * o atributo A no site S. É um conjunto pq é possível que complementary
     * rules tenham sido adicionadas. A master rule sempre será a primeira da
     * lista
     * @param allRulesInSite
     * @param masterRulesInOtherSites
     */
    public ComplementaryRule(List<Integer> masterRulesInSite, Map<Integer, Rule> allRulesInSite, Set<Rule> masterRulesInOtherSites, int nrPagesSite) {
        this.masterRulesInSite = masterRulesInSite;
        this.allRulesInSite = allRulesInSite;
        this.masterRulesInOtherSites = masterRulesInOtherSites;
        this.nrPagesSite = nrPagesSite;
    }

    /**
     * encontre as regras com intersecção (conjunto de páginas que extraí valor
     * não nulo) vazia com a masterRule
     *
     * @return Set<RuleID>
     */
    private Set<Rule> getDisjointRules() {
        Set<Rule> disjointRules = new HashSet<>();

        int nrWithIntersection = 0;
        int nrWithoutIntersection = 0;
        for (Rule candRule : allRulesInSite.values()) {
            if (masterRulesInSite.contains(candRule.getRuleID())) {
                continue;//regra avaliada já é master rule
            }

            Set<String> intersection = new HashSet<>();
            for (Integer mr : masterRulesInSite) {
                intersection.addAll(allRulesInSite.get(mr).getUrlValues().keySet());
            }
            intersection.retainAll(candRule.getUrlValues().keySet());
            if (intersection.isEmpty()) {
                nrWithoutIntersection++;
                disjointRules.add(candRule);
            }else{
                nrWithIntersection++;
            }
        }
        
        if (General.DEBUG){
            System.out.println("Disjoint filter - elimina as regras que são sobrepostas");
            System.out.println("\tEliminadas: "+nrWithIntersection);
            System.out.println("\tPermaneceram: "+nrWithoutIntersection);
        }
        
        return disjointRules;
    }

    /**
     * @return the disjoint rule with the highest eficcacy
     */
    public Rule getComplementaryRule() {

        Set<Rule> disjointedRules = getDisjointRules();
        double maxScore = 0;
        Rule maxRule = null;

        for (Rule candRule : disjointedRules) {
            double score = Ranking.score(allRulesInSite.get(masterRulesInSite.get(0)), candRule, masterRulesInOtherSites, nrPagesSite);
            if (score == 0) {
                continue;
            }

            if (score > maxScore) {
                maxScore = score;
                maxRule = candRule;
            }
        }
        return maxRule;
    }

    public static void main(String[] args) {
        General.DEBUG = true;
        
        Site site = br.edimarmanica.dataset.swde.camera.Site.AMAZON;
        //Attribute attribute = br.edimarmanica.dataset.swde.camera.Attribute.MANUFECTURER;

        /**
         * * all rules in site **
         */
        Map<Integer, Rule> allRules = LoadRule.loadAllRules(site);

        /**
         * * master rule in site **
         */
        List<Integer> masterRulesInSite = new ArrayList<>();
        masterRulesInSite.add(1100);


        /**
         * * master rules in other sites **
         */
        Map<Site, Integer> aux = new HashMap<>();
        aux.put(br.edimarmanica.dataset.swde.camera.Site.BEACHAUDIO, 3056);
        aux.put(br.edimarmanica.dataset.swde.camera.Site.BUY, 13455);
        aux.put(br.edimarmanica.dataset.swde.camera.Site.COMPSOURCE, 7991);
        aux.put(br.edimarmanica.dataset.swde.camera.Site.ECOST, 3736);
        aux.put(br.edimarmanica.dataset.swde.camera.Site.JR, 1219);
        aux.put(br.edimarmanica.dataset.swde.camera.Site.NEWEGG, 38);
        aux.put(br.edimarmanica.dataset.swde.camera.Site.ONSALE, 3462);
        aux.put(br.edimarmanica.dataset.swde.camera.Site.PCNATION, 614);
        aux.put(br.edimarmanica.dataset.swde.camera.Site.THENERDS, 917);

        Set<Rule> masterRulesInOtherSites = new HashSet<>();
        for (Site s : aux.keySet()) {
            LoadRule lr = new LoadRule(s, aux.get(s));
            masterRulesInOtherSites.add(lr.loadRule());
        }

        /**
         * * Execução **
         */
        ComplementaryRule rum = new ComplementaryRule(masterRulesInSite, allRules, masterRulesInOtherSites, 2000);
        System.out.println("Complemented Rule: " + rum.getComplementaryRule().getRuleID()); //1584 ou 741

    }
}
