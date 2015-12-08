/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.check;

import br.edimarmanica.dataset.weir.Domain;
import br.edimarmanica.dataset.weir.finance.Site;
import br.edimarmanica.weir.algorithms.distance.TypeAwareDistance;
import br.edimarmanica.weir.algorithms.filter.NullValuesFilter;
import br.edimarmanica.weir.algorithms.filter.RulesFilter;
import br.edimarmanica.weir.algorithms.integration.ScoredPairs;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.ScoredPair;
import br.edimarmanica.weir.load.LoadRules;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class ScoreCheck {
    
    public static void main(String[] args) {

        List<Rule> rules = new ArrayList<>();
        rules.add(loadRules(Site.BIGCHARTS, 13));
        //rules.add(loadRules(Site.BLACKWELL, 18));
        rules.add(loadRules(Site.NASDAQ, 51)); //128
        //rules.add(loadRules(Site.BARNESANDNOBLE, 56));
        //rules.add(loadRules(Site.BOOKDEPOSITORY, 16));
        //rules.add(loadRules(Site.BOOKRENTER, 2));
       // rules.add(loadRules(Site.BOOKSANDEBOOKS, 1)); //1       
        //rules.add(loadRules(Site.ECAMPUS, 9));
        //rules.add(loadRules(Site.GOODREADS, 271));

        
        System.out.println("Score: "+TypeAwareDistance.typeDistance(rules.get(0), rules.get(1)));
        
        
        /*List<ScoredPair> pairs = ScoredPairs.getScoredPairs(Domain.BOOK, rules);
        for (ScoredPair pair : pairs) {
            System.out.println(pair.getR1().getSite().getFolderName() + ";" + pair.getR1().getRuleID() + ";"
                    + pair.getS1().getSite().getFolderName() + ";" + pair.getS1().getRuleID() + ";"+pair.getDistance());
        }*/
        
    }

    public static Rule loadRules(Site site, Integer ruleID) {
        
        List<Rule> rules = new ArrayList<>();
        
        LoadRules lr = new LoadRules(site);
        Set<Rule> rulesSite = lr.getRules();
        for (Rule rule : rulesSite) {
            if (ruleID == rule.getRuleID()) {
                return rule;
            }
        }
        return null;
    }
}
