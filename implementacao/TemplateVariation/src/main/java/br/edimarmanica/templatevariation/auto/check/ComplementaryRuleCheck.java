/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.check;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.templatevariation.auto.ComplementaryRule;
import br.edimarmanica.templatevariation.auto.UnionRules;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import br.edimarmanica.templatevariation.auto.load.LoadRule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class ComplementaryRuleCheck {
    public static void main(String[] args) {
        General.DEBUG=true;
        
        Site site = br.edimarmanica.dataset.swde.movie.Site.BOXOFFICEMOJO;
        Attribute attribute = br.edimarmanica.dataset.swde.movie.Attribute.DIRECTOR;

        /**
         * * all rules in site **
         */
        Map<Integer, Rule> allRules = LoadRule.loadAllRules(site);
        /**
         * * master rule in site **
         */
        List<Integer> masterRulesInSite = new ArrayList<>();
        masterRulesInSite.add(6708);

        /**
         * * master rules in other sites **
         */
        Set<Rule> masterRulesInOtherSites = UnionRules.getMasterRulesInOtherSitesManual(site, attribute);
        /**
         * * Execução **
         */
        ComplementaryRule rum = new ComplementaryRule(masterRulesInSite, allRules, masterRulesInOtherSites, UnionRules.getNrPages(site));
        System.out.println("Complemented Rule: " + rum.getComplementaryRule().getRuleID()); //1584 ou 741

    }
}
