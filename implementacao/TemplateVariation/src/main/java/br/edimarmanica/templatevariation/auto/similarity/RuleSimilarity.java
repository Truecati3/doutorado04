/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.templatevariation.auto.bean.Rule;

/**
 *
 * @author edimar
 */
public abstract class RuleSimilarity {
    protected Rule masterRule;
    protected Rule candidateComplementaryRule; 

    public RuleSimilarity(Rule masterRule, Rule candidateComplementaryRule) {
        this.masterRule = masterRule;
        this.candidateComplementaryRule = candidateComplementaryRule;
    }
    
    public abstract double score();
}
