/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.contentsimilarity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.math.stat.descriptive.moment.Variance;

/**
 *
 * @author edimar
 */
public class NumberSimilarity extends TypeAwareSimilarity {

    private Double standardDesviation;
    private Double average;

    @Override
    public double similaritySpecific(String valueR1, Collection<String> valuesR2) {

        if (standardDesviation == null || average == null) {
            throw new UnsupportedOperationException("You should train first!");
        }

        double numericValueR1;
        try {
            numericValueR1 = normalize(valueR1);
        } catch (NoiseException ex) {
            //System.out.println(ex.getMessage());
            return 0; //é um lixo que a regra pegou. Por ex: London
        }

        double aux = (numericValueR1 - average) / standardDesviation; //Fazendo de acordo com Wikipedi (Families of densities) pq ONDUX tinha falha https://en.wikipedia.org/wiki/Probability_density_function
        double exp = -0.5 * Math.pow(aux, 2);
        double nm = Math.pow(Math.E, exp);

        // double mpd =  1/(Math.sqrt(2*Math.PI*Math.pow(standardDesviation, 2))); //The maximum probability density of V (ai) -- não precisa mais

        return nm;
    }

    @Override
    public double similarity(Collection<String> r1, Collection<String> r2) {
        train(r2);

        return super.similarity(r1, r2);
    }

    /**
     *
     * @param r2 : r2 será como a KB do Ondux
     */
    public void train(Collection<String> r2) {

        /**
         * Formatando os valores *
         */
        List<Double> numericValuesR2 = new ArrayList<>();
        for (String value : r2) {
            try {
                numericValuesR2.add(normalize(value));
            } catch (NoiseException ex) { //ignora o ruído
                //Logger.getLogger(NumberSimilarity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Média *
         */
        double sum = 0;
        double[] vector = new double[numericValuesR2.size()];
        int i = 0;
        for (Double v : numericValuesR2) {
            sum += v;
            vector[i] = v;
            i++;
        }
        average = sum / numericValuesR2.size();

        /**
         * Desvio padrão *
         */
        Variance variance = new Variance();
        standardDesviation = Math.sqrt(variance.evaluate(vector));
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
