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
public class XPathSimilarity extends RuleSimilarity {

    public XPathSimilarity(Rule masterRule, Rule candidateComplementaryRule) {
        super(masterRule, candidateComplementaryRule);
    }

    @Override
    public double score() {
        JaroWinkler jaro = new JaroWinkler();
        return jaro.score(masterRule.getXPath(), candidateComplementaryRule.getXPath());
    }
}
