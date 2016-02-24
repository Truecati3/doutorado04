/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.entitysimilarity.InsufficientOverlap;
import br.edimarmanica.entitysimilarity.TypeAwareSimilarity;
import br.edimarmanica.templatevariation.auto.bean.Rule;
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
     * @return max entity score between candidate complementary rule and the master rules for the same attribute in other sites
     */
    @Override
    public double score() {

        double maxSim = 0;
        for (Rule masterRuleInOtherSite : masterRulesInOtherSites) {

            try {
                double sim = TypeAwareSimilarity.typeSimilarity(masterRuleInOtherSite.getEntityValues(), candidateComplementaryRule.getEntityValues());
                
                if (sim > maxSim){
                    maxSim = sim;
                }
            } catch (InsufficientOverlap ex) {
                //não tinha entidades sobrepostas
            }
        }
        return maxSim;
    }
}
