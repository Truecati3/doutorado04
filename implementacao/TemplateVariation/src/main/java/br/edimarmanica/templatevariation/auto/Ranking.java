/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto;

import br.edimarmanica.templatevariation.auto.bean.Rule;
import br.edimarmanica.templatevariation.auto.similarity.DomainSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.EntitySimilarity;
import br.edimarmanica.templatevariation.auto.similarity.LabelSimilarity;
import br.edimarmanica.templatevariation.auto.similarity.RuleSimilarity;
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
    
    public static double score(Set<Rule> masterRulesInOtherSites, Rule masterRule, Rule candidateComplementaryRule){
        RuleSimilarity xpathSim = new XPathSimilarity(masterRule, candidateComplementaryRule);
        RuleSimilarity labelSim = new LabelSimilarity(masterRule, candidateComplementaryRule);
        RuleSimilarity domainSim = new DomainSimilarity(masterRule, candidateComplementaryRule);
        RuleSimilarity entitySim = new EntitySimilarity(masterRulesInOtherSites, masterRule, candidateComplementaryRule);
        
        return T_xpath*xpathSim.score() + T_label * labelSim.score() + T_domain * domainSim.score() + T_entity * entitySim.score() + T_size * candidateComplementaryRule.getUrlValues().size();
    }
}
