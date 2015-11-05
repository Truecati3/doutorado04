/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.filter;

import br.edimarmanica.configuration.IntrasiteExtraction;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.Value;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class NullValuesFilter extends RulesFilter {

    public NullValuesFilter(Site site) {
        super(site);
    }

    /**
     * We discard rules whose a large majority (more than 80%) of null values.
     *
     * @param rules
     * @return
     */
    @Override
    public Set<Rule> execute(Set<Rule> rules) {

        Set<Rule> rulesFiltered = new HashSet<>();

        for (Rule rule : rules) {
            
            int nrPages = 0;
            int nrNullValues = 0;
            for(Value v: rule.getValues()){
                nrPages++;
                if (v.getValue() == null){
                    nrNullValues++;
                }
            }

            if ((nrNullValues * 100 / nrPages) <= IntrasiteExtraction.PR_NULL_VALUES) {
                rulesFiltered.add(rule);
            }
        }
        return rulesFiltered;
    }

}
