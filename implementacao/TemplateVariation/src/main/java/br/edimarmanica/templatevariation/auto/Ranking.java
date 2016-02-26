/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto;

import br.edimarmanica.configuration.General;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import br.edimarmanica.templatevariation.auto.similarity.DomainSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.EntitySimilarity;
import br.edimarmanica.templatevariation.auto.similarity.LabelSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.RuleSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.SizeSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.XPathSimilarity;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class Ranking {

    public static final double T_xpath = 0.22;
    public static final double T_label = 0.22;
    public static final double T_domain = 0.22;
    public static final double T_entity = 0.24;
    public static final double T_size = 0.1;

    // tem aquele artigo que diz quando somar e quando multiplicar e o efeito disso
    /// features: XPath, Label, DomainSimilarity (nXn intrasite), EntitySimilarity (entity->entity intersite), nr de páginas extraídas   
    /**
     * 
     * @param masterRule
     * @param candidateComplementaryRule
     * @param masterRulesInOtherSites
     * @param nrPagesSite -- Número total de páginas do site
     * @return 
     */
    public static double score(Rule masterRule, Rule candidateComplementaryRule, Set<Rule> masterRulesInOtherSites, int nrPagesSite) {
        
        RuleSimilarity xpathSim = new XPathSimilarity(masterRule, candidateComplementaryRule);
        RuleSimilarity labelSim = new LabelSimilarity(masterRule, candidateComplementaryRule);
        RuleSimilarity domainSim = new DomainSimilarity(masterRule, candidateComplementaryRule);
        RuleSimilarity entitySim = new EntitySimilarity(masterRulesInOtherSites, masterRule, candidateComplementaryRule);
        RuleSimilarity sizeSim = new SizeSimilarity(nrPagesSite, masterRule, candidateComplementaryRule);

        double xpathScore = T_xpath * xpathSim.score();
        double labelScore = T_label * labelSim.score();
        double domainScore = T_domain * domainSim.score();
        double entityScore = T_entity * entitySim.score();
        double sizeScore = T_size * sizeSim.score();
        double finalScore = xpathScore + labelScore + domainScore + entityScore + sizeScore;
        
        if (General.DEBUG){
            System.out.println("rule,xpathScore,labelScore,domainScore,entityScore,sizeScore,total");
            System.out.println(candidateComplementaryRule.getRuleID()+","+xpathScore+","+labelScore+","+domainScore+","+entityScore+","+sizeScore+","+finalScore);
        }
        
        return finalScore;
    }
}
