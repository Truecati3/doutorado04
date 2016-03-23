/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.contentsimilarity;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author edimar
 */
public abstract class TypeAwareSimilarity {

    /**
     *
     * @param r1 Set<Value>
     * @param r2 Set<Value> -- essa deve ser a master rule pq serve como KB
     * @return the content similarity between r1 and r2 -- Baseado na type aware
     * similarity usada por Ondux (R2 assume o papel de KB)
     */
    public static double typeSimilarity(Collection<String> r1, Collection<String> r2) {

        DataType typeR1 = DataTypeController.getMostFrequentType(r1);
        DataType typeR2 = DataTypeController.getMostFrequentType(r2);
        
        if (typeR1 != typeR2){
            return 0; //como são do mesmo site deve ser do mesmo tipo, senão é zero
        }
        TypeAwareSimilarity similarity;

        switch (typeR1) {
            case DATE:
                similarity = new DateSimilarity();
                break;
            case ISBN:
                similarity = new ISBNSimilarity();
                break;
            case PHONE:
                similarity = new PhoneSimilarity();
                break;
            case CURRENCY:
                similarity = new CurrencySimilarity();
                break;
            case LENGHT:
                similarity = new LenghtSimilarity();
                break;
            case WEIGHT:
                similarity = new WeightSimilarity();
                break;
            case NUMBER:
                similarity = new NumberSimilarity();
                break;
            case STRING:
                similarity = new StringSimilarity();
                break;
            default:
                similarity = null;
        }

        double similarityValue = similarity.similarity(r1, r2);
        return similarityValue;
    }

    /**
     * Calcula a similarity entre cada elemento de r1 com o conjunto de valores
     * de r2 pq r2 corresponde a KB do Ondux. Ai faz a média
     *
     * @param r1
     * @param r2
     * @return
     */
    public double similarity(Collection<String> r1, Collection<String> r2) {

        double similarity = 0;
        for (String valueR1 : r1) {
            similarity += similaritySpecific(valueR1, r2);
        }

        return similarity / r1.size(); //é a média da similaridade dos elementos de r1 com o conjunto r2
    }

    public abstract double similaritySpecific(String valueR1, Collection<String> valueR2);
}
