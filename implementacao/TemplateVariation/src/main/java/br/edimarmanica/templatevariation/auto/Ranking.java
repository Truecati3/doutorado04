/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import br.edimarmanica.templatevariation.auto.load.LoadRule;
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

    public static final double T_xpath = 5.0 / 100;
    public static final double T_label = 5.0 / 100;
    public static final double T_domain = 40.0 / 100;
    public static final double T_entity = 45.0 / 100;
    public static final double T_size = 5.0 / 100;

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



        double xpathScore = xpathSim.score();
        double labelScore = labelSim.score();
        double domainScore = domainSim.score();
        double entityScore = entitySim.score();
        double sizeScore = sizeSim.score();
        double finalScore = T_xpath * xpathScore + T_label * labelScore + T_domain * domainScore + T_entity * entityScore + T_size * sizeScore;

        if (domainScore < 0.5 && entityScore < 0.5) {//Threshold
            return 0;
        }

        if (General.DEBUG) {
            System.out.println("rule,xpathScore,labelScore,domainScore,entityScore,sizeScore,total");
            System.out.println(candidateComplementaryRule.getRuleID() + "," + xpathScore + "," + labelScore + "," + domainScore + "," + entityScore + "," + sizeScore + "," + finalScore);
        }

        return finalScore;
    }

    public static void main(String[] args) {
        General.DEBUG = true;

        Site site = br.edimarmanica.dataset.weir.book.Site.BOOKMOOCH;
        Attribute attribute = br.edimarmanica.dataset.weir.book.Attribute.EDITION;

        int masterRuleID = 179;
        LoadRule lrMaster = new LoadRule(site, masterRuleID);
        Rule masterRule = lrMaster.loadRule();

        int compRuleID = 706;
        LoadRule lrComp = new LoadRule(site, compRuleID);
        Rule compRule = lrComp.loadRule();

        Set<Rule> masterRulesInOtherSites = UnionRules.getMasterRulesInOtherSitesManual(site, attribute);

        System.out.println("Score: " + score(masterRule, compRule, masterRulesInOtherSites, UnionRules.getNrPages(site)));
    }
}
