/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.filter;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.Value;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author edimar
 */
public class SameValuesFilter extends RulesFilter {

    public SameValuesFilter(Site site) {
        super(site);
    }

    @Override
    public Set<Rule> execute(Set<Rule> rules) {
        List<Rule> listRules = new ArrayList<>(rules);
        Set<Integer> blackList = new HashSet<>();

        for (int i = 0; i < listRules.size() - 1; i++) {
            if (blackList.contains(listRules.get(i).getRuleID())) { //esse já foi excluído por ter os mesmos valores que outro
                continue;
            }

            for (int j = i + 1; j < listRules.size(); j++) {
                if (sameValues(listRules.get(i), listRules.get(j))) {
                    blackList.add(listRules.get(j).getRuleID());
                }
            }
        }

        Set<Rule> filteredRules = new HashSet<>();
        for (Rule r : rules) {
            if (!blackList.contains(r.getRuleID())) {
                filteredRules.add(r);
            }
        }
        return filteredRules;
    }

    /**
     *
     * @param r
     * @param s
     * @return true se as regras extraem os mesmos valores nas mesmas páginas e na mesma quantidade de páginas
     */
    private boolean sameValues(Rule r, Rule s) {
        
        if (r.getNrNotNullValues() == s.getNrNotNullValues()) {
            for (Value v : r.getValues()) {
                if (!s.getValues().contains(v)) {
                    /*** inicio debug **/
//                    if (getSite() == br.edimarmanica.dataset.weir.book.Site.GOODREADS) {
//                        if ((r.getRuleID() == 467 || s.getRuleID() == 467) && (r.getRuleID() == 79 || s.getRuleID() == 79)) {
//                            System.out.println("Value: "+v.getValue());
//                        }
//                    }            
                    /*** fim debug **/
                    
                    return false; //encontrou um valor diferente
                }
            }
            return true;
        }
        return false; //sizes diferentes
    }
}
