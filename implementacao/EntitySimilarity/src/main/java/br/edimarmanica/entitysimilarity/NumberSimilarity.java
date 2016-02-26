/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class NumberSimilarity extends TypeAwareSimilarity {

    private static final double TETA = 0.02;
    private Double p;

    @Override
    public double similaritySpecific(String valueR1, String valueR2) {

        if (p == null) {
            throw new UnsupportedOperationException("You should train first!");
        }

        double numericValueR1;
        try {
            numericValueR1 = normalize(valueR1);
        } catch (NoiseException ex) {
            //System.out.println(ex.getMessage());
            return 0; //é um lixo que a regra pegou. Por ex: London
        }
        double numericValueR2;
        try {
            numericValueR2 = normalize(valueR2);
        } catch (NoiseException ex) {
            //System.out.println(ex.getMessage());
            return 0; //é um lixo que a regra pegou. Por ex: London
        }

        if (Math.abs(numericValueR1 - numericValueR2) > p) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public double similarity(Map<String, String> r1, Map<String, String> r2) throws InsufficientOverlapException {
        train(r1, r2);

        return super.similarity(r1, r2);
    }

    /**
     *
     * @param numericValues : list, pois um set eliminaria valores iguais
     * @return
     */
    private double getAverageAbsoluteValues(List<Double> numericValues) {
        double sum = 0;

        for (Double v : numericValues) {
            sum += Math.abs(v); //absolute values
        }

        return sum / numericValues.size();//lembrando que each value de uma rule é de uma entidade diferente, pois é de uma página diferente e cada página representa uma entidade diferente
    }

    private void train(Map<String, String> r1, Map<String, String> r2) {
        List<Double> numericValuesR1 = new ArrayList<>();
        for (String value : r1.values()) {
            try {
                numericValuesR1.add(normalize(value)); //lembrando que each value de uma rule é de uma entidade diferente, pois é de uma página diferente e cada página representa uma entidade diferente
            } catch (NoiseException ex) {
                //Logger.getLogger(NumberSimilarity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        double avgR1 = getAverageAbsoluteValues(numericValuesR1);

        List<Double> numericValuesR2 = new ArrayList<>();
        for (String value : r2.values()) {
            try {
                numericValuesR2.add(normalize(value)); //lembrando que each value de uma rule é de uma entidade diferente, pois é de uma página diferente e cada página representa uma entidade diferente
            } catch (NoiseException ex) {
                //Logger.getLogger(NumberSimilarity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        double avgR2 = getAverageAbsoluteValues(numericValuesR2);

        p = Math.min(avgR1, avgR2) * TETA;

    }

    public Double normalize(String numericValue) throws NoiseException {
        String aux = numericValue;
        aux = aux.replaceAll("R\\$", "").replaceAll("\\$", "").replaceAll("€", ""); //retirando o simbolo de moeda pq currencyXnumber=numberXnumber, ou seja, se uma das regras e number, compara tudo com number
        aux = aux.replaceAll("[a-zA-Z]", ""); //retirando cm, m, etc. mesmo motivo acima

        NumberFormat format = NumberFormat.getNumberInstance(new Locale("en", "US"));
        try {
            return format.parse(aux.trim()).doubleValue();
        } catch (ParseException ex) {
            throw new NoiseException(numericValue, DataType.NUMBER);
        }
    }
}
