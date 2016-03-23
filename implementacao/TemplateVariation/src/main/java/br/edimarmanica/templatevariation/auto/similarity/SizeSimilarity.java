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
public class SizeSimilarity extends RuleSimilarity{

    private int nrPages; //número de páginas do site

    public SizeSimilarity(int nrPages, Rule masterRule, Rule candidateComplementaryRule) {
        super(masterRule, candidateComplementaryRule);
        this.nrPages = nrPages;
    }
    
    @Override
    public double score() {
        return (double)candidateComplementaryRule.getUrlValues().size() / nrPages;
    }
    
}
