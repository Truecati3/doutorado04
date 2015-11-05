/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.filter;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.bean.Rule;
import java.util.Set;

/**
 *
 * @author edimar
 */
public abstract class RulesFilter {

    private Site site;

    public RulesFilter(Site site) {
        this.site = site;
    }
    
    

    public Set<Rule> filter(Set<Rule> rules) {
        Set<Rule> rulesFiltered = execute(rules);
        return rulesFiltered;
    }

    public abstract Set<Rule> execute(Set<Rule> rules);

    public Site getSite() {
        return site;
    }
    
    
}
