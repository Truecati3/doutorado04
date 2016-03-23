/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.contentsimilarity.TypeAwareSimilarity;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import java.util.HashSet;
import java.util.Set;

/**
 * Acho que um Jaro ou Qgrams seria mais proveitoso
 *
 * @author edimar
 */
public class DomainSimilarity extends RuleSimilarity {

    public DomainSimilarity(Rule masterRule, Rule candidateComplementaryRule) {
        super(masterRule, candidateComplementaryRule);
    }

    @Override
    public double score() {
        //a regra mestre deve ser a segunda pq é a que serve como KB do Ondux
        return TypeAwareSimilarity.typeSimilarity(candidateComplementaryRule.getUrlValues().values(), masterRule.getUrlValues().values());
    }
}
