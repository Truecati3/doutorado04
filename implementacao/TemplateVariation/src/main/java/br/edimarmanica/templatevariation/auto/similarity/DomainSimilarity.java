/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.templatevariation.auto.bean.Rule;
import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.UnsmoothedJS;
import com.wcohen.ss.api.StringWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Acho que um Jaro ou Qgrams seria mais proveitoso
 * @author edimar
 */
public class DomainSimilarity extends RuleSimilarity {

    private UnsmoothedJS similarity;

    public DomainSimilarity(Rule masterRule, Rule candidateComplementaryRule) {
        super(masterRule, candidateComplementaryRule);
    }

    @Override
    public double score() {
        train();
        return overallSimilarity();
    }

    private void train() {
        similarity = new UnsmoothedJS();

        /**
         * add the values of masterRule and candidateComplementaryRule to the
         * corpus
         */
        List<StringWrapper> list = new ArrayList<>();
        for (String value : masterRule.getUrlValues().values()) {
            /**
             * Na linha 32 do JensenShannonDistance tem um bug que ele chama o
             * next duas vezes. Desta forma ele sempre perde os valores da
             * posição par. Lembrando que índice começa em zero
             */
            list.add(similarity.prepare("nulo"));
            list.add(similarity.prepare(value));
        }

        for (String value : candidateComplementaryRule.getUrlValues().values()) {
            /**
             * Na linha 32 do JensenShannonDistance tem um bug que ele chama o
             * next duas vezes. Desta forma ele sempre perde os valores da
             * posição par. Lembrando que índice começa em zero
             */
            list.add(similarity.prepare("nulo"));
            list.add(similarity.prepare(value));
        }

        similarity.train(new BasicStringWrapperIterator(list.iterator()));
    }
    
    /**
     * 
     * @param complementarRuleValue
     * @return o maior valor de similaridade entre o valor da regra complementar e os valores da regra mestre
     */
    private double individualSimilarity(String complementarRuleValue){
        double max = 0;
        for(String value: masterRule.getUrlValues().values()){
            double current = similarity.score(value, complementarRuleValue);
            
            if (current > max){
                max = current;
            }
        }
        
        return max;
    }
    
    /**
     * 
     * @return média das similaridades individuais de cada valor da regra complementar
     */
    private double overallSimilarity(){
        int count = 0;
        double acumulador = 0;
        for(String value: candidateComplementaryRule.getUrlValues().values()){
            count++;
            acumulador += individualSimilarity(value);
        }
        return acumulador / count;
    }
}

