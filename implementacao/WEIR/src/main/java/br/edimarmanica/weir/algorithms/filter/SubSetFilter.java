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
 * Se duas regras r e s extraem os mesmos valores, porém uma delas extrai em
 * menos páginas, fica só com a que extrai em mais
 *
 * @author edimar
 */
public class SubSetFilter extends RulesFilter {

    public SubSetFilter(Site site) {
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
                if (listRules.get(i).getNrNotNullValues() >= listRules.get(j).getNrNotNullValues()) {
                    if (sameValues(listRules.get(i), listRules.get(j))) {
                        blackList.add(listRules.get(j).getRuleID());
                    }
                } else {
                    if (sameValues(listRules.get(j), listRules.get(i))) {
                        blackList.add(listRules.get(i).getRuleID());
                    }
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
     * @param rMaior: regra com mais valores
     * @param rMenor: regra com menos valores ou mesma quantidade de valores
     * @return true se as regras extraem os mesmos valores nas mesmas páginas,
     * mesmo que o nr de páginas extraídas seja diferente
     */
    private boolean sameValues(Rule rMaior, Rule rMenor) {


        for (Value v : rMenor.getValues()) { //tem que começar do menor (com menos valores)
            if (v.getValue() == null){
                continue;
            }
            
            if (!rMaior.getValues().contains(v) && rMaior.getPagesWithNotNullValues().contains(v.getPageID())) {
                /**
                 * * inicio debug *
                 */
//                if (getSite() == br.edimarmanica.dataset.weir.book.Site.AMAZON) {
//                    if ((rMaior.getRuleID() == 267 || rMenor.getRuleID() == 267) && (rMaior.getRuleID() == 14 || rMenor.getRuleID() == 14)) {
//                        System.out.println("Value: " + v.getValue() + " - "+v.getPageID());
//                    }
//                }
                /**
                 * fim debug *
                 */
                return false; //encontrou um valor diferente
            }
        }
        return true;
    }
}
