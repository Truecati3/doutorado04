/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.swde.movie.Site;
import br.edimarmanica.entitysimilarity.InsufficientOverlapException;
import br.edimarmanica.entitysimilarity.TypeAwareSimilarity;
import br.edimarmanica.templatevariation.auto.UnionRules;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import br.edimarmanica.templatevariation.auto.load.LoadRule;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class EntitySimilarity extends RuleSimilarity {

    private Set<Rule> masterRulesInOtherSites;

    public EntitySimilarity(Set<Rule> masterRulesInOtherSites, Rule masterRule, Rule candidateComplementaryRule) {
        super(masterRule, candidateComplementaryRule);
        this.masterRulesInOtherSites = masterRulesInOtherSites;
    }

    /**
     *
     * @return max entity score between candidate complementary rule and the
     * master rules for the same attribute in other sites or 0 se não há um
     * número mínimo de entidades sobrepostas com nenhum site e a referida regra
     */
    @Override
    public double score() {

        double maxSim = 0;
        for (Rule masterRuleInOtherSite : masterRulesInOtherSites) {
           // System.out.println("Master: " + masterRuleInOtherSite.getRuleID() + " - Comp: " + candidateComplementaryRule.getRuleID());
            try {
                //System.out.println("Master: "+masterRuleInOtherSite.getEntityValues());
                //System.out.println("Complementar: "+candidateComplementaryRule.getEntityValues());
                /*if (masterRuleInOtherSite.getRuleID() == 528) {
                    for (String key : candidateComplementaryRule.getEntityValues().keySet()) {
                        if (candidateComplementaryRule.getEntityValues().get(key).equals(masterRuleInOtherSite.getEntityValues().get(key))) {
                            System.out.println(candidateComplementaryRule.getEntityValues().get(key) + "X" + masterRuleInOtherSite.getEntityValues().get(key));
                        }

                    }
                }*/
                double sim = TypeAwareSimilarity.typeSimilarity(masterRuleInOtherSite.getEntityValues(), candidateComplementaryRule.getEntityValues());
               // System.out.println("sim: " + sim);
                if (sim > maxSim) {
                    maxSim = sim;
                }
            } catch (InsufficientOverlapException ex) {
                //não tinha entidades sobrepostas suficientes
            }
        }
        return maxSim;
    }

    public static void main(String[] args) {
        General.DEBUG = true;

        Site site = Site.BOXOFFICEMOJO;
        Attribute attribute = br.edimarmanica.dataset.swde.movie.Attribute.DIRECTOR;

        int compRuleID = 2626;
        LoadRule lrComp = new LoadRule(site, compRuleID);
        Rule compRule = lrComp.loadRule();

        Set<Rule> masterRulesInOtherSites = UnionRules.getMasterRulesInOtherSitesManual(site, attribute);

        EntitySimilarity sim = new EntitySimilarity(masterRulesInOtherSites, null, compRule);

        System.out.println("Score: " + sim.score());
    }
}
