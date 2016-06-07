/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.check;

import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.algorithms.filter.NullValuesFilter;
import br.edimarmanica.weir.algorithms.filter.RulesFilter;
import br.edimarmanica.weir.algorithms.filter.SameValuesFilter;
import br.edimarmanica.weir.algorithms.filter.SubSetFilter;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.load.LoadRules;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edimar
 */
public class NrRulesPerSitePosFilter {

    public static void main(String[] args) {

        Domain domain = br.edimarmanica.dataset.weir.Domain.FINANCE;

        for (Site site : domain.getSites()) {

            System.out.println("Site: " + site);
            LoadRules lr = new LoadRules(site);
            /**
             * adicionando filtros *
             */
            RulesFilter filter01 = new NullValuesFilter(site);
            RulesFilter filter02 = new SameValuesFilter(site);
            RulesFilter filter03 = new SubSetFilter(site);

            List<Rule> rules = new ArrayList<>();
            rules.addAll(filter03.filter(filter02.filter(filter01.filter(lr.getRules())))); //só add as regras que satisfazem os filtros
            System.out.println("\t" + rules.size());
            
            //eu tenho que indexar informações delas, por exemplo, o tipo. Para não fazer de novo todas as vezes
        }
    }
}
