/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.weakremoval;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.algorithms.filter.NullValuesFilter;
import br.edimarmanica.weir.algorithms.filter.RulesFilter;
import br.edimarmanica.weir.algorithms.integration.ScoredPairs;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.ScoredPair;
import br.edimarmanica.weir.load.LoadRules;
import br.edimarmanica.weir.util.Conjuntos;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class WeakRemoval {

    public static List<Rule> weakRemoval(Domain domain, List<Rule> rules) {
        List<ScoredPair> scoredPairs = ScoredPairs.getScoredPairs(domain, rules);
        Set<Rule> completeRules = new HashSet<>();

        Conjuntos<String> util = new Conjuntos<>();

        for (ScoredPair sp : scoredPairs) {
            if (General.DEBUG) {
                System.out.println(sp.getR1().getSite() + ";" + sp.getR1().getRuleID() + ";" + sp.getS1().getSite() + ";" + sp.getS1().getRuleID() + ";" + sp.getDistance());
            }

            //quando chegar no score 1 poderia parar

            //r e s devem ser do mesmo site
            if (sp.getR1().getSite() == sp.getS1().getSite()) {
                //r e s não devem ter valores sobrepostos, ou seja, a intersecção entre seus conjuntos de valores é vazio
                if (util.intersection(sp.getR1().getPairsPageValue(), sp.getS1().getPairsPageValue()).isEmpty()) {
                    //para cada regra do mesmo site que é correta (r*)
                    boolean exist = false;
                    for (Rule t : completeRules) {
                        //(se há intersecção entre os valores de r* e r) ou (se há intersecção entre os valores de r* e s)
                        if (!util.intersection(sp.getR1().getPairsPageValue(), t.getPairsPageValue()).isEmpty()
                                || !util.intersection(sp.getS1().getPairsPageValue(), t.getPairsPageValue()).isEmpty()) {
                            exist = true;
                            if (General.DEBUG) {
                                System.out.println("\tTem intersecção com "+t.getRuleID()+ " do site "+t.getSite());
                            }
                            break;
                        }
                    }

                    if (!exist) {
                        if (General.DEBUG) {
                            System.out.println("\tComplete");
                        }
                        completeRules.add(sp.getR1());
                        completeRules.add(sp.getS1());
                    }

                }
            }
        }

        return new ArrayList<>(completeRules);
    }

    public static void main(String[] args) {
        General.DEBUG = true;
        Domain domain = br.edimarmanica.dataset.weir.Domain.BOOK;
        List<Rule> rules = new ArrayList<>();

        for (Site site : domain.getSites()) {
            LoadRules lr = new LoadRules(site);
            /**
             * adicionando filtros *
             */
            RulesFilter filter = new NullValuesFilter(site);
            rules.addAll(filter.filter(lr.getRules())); //só add as regras que satisfazem o filtro
        }

        //aqui tem que ter um passo que calcula o score e armazena pq hj ela calcula duas vezes: uma para o WeakRemoval e outra para o Weir
        //  ScoredPairs scores = new ScoredPairs(domain, rules);
        //  scores.compute();
        //  scores.persists();

        rules = weakRemoval(domain, rules);

        for (Rule r : rules) {
            System.out.println("\t" + r.getSite().getFolderName() + " - " + r.getRuleID());
        }
    }
}
