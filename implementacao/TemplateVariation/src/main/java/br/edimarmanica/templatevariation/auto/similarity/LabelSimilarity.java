/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.templatevariation.auto.bean.Rule;
import com.wcohen.ss.JaroWinkler;

/**
 *
 * @author edimar
 */
public class LabelSimilarity extends RuleSimilarity {

    public LabelSimilarity(Rule masterRule, Rule candidateComplementaryRule) {
        super(masterRule, candidateComplementaryRule);
    }

    @Override
    public double score() {
        JaroWinkler jaro = new JaroWinkler();
        //No Futuro aqui eu posso usar aquela similaridade semântica do Berkeley
        return jaro.score(masterRule.getLabel(), candidateComplementaryRule.getLabel());
    }
}
