/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.distance;

import br.edimarmanica.weir2.rule.type.DataType;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class NumberDistance extends TypeAwareDistance {

    private static final double TETA = 0.02;
    private Double p;

    @Override
    public double distanceSpecific(String vR1, String vS1) {

        if (p == null) {
            throw new UnsupportedOperationException("You should train first!");
        }

        double numericValueR1;
        try {
            numericValueR1 = normalize(vR1);
        } catch (NoiseException ex) {
            //System.out.println(ex.getMessage());
            return 1; //é um lixo que a regra pegou. Por ex: London
        }
        double numericValueS1;
        try {
            numericValueS1 = normalize(vS1);
        } catch (NoiseException ex) {
            //System.out.println(ex.getMessage());
            return 1; //é um lixo que a regra pegou. Por ex: London
        }

        if (Math.abs(numericValueR1 - numericValueS1) > p) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public double distance(Map<String, String> entityValuesR1, Map<String, String> entityValuesR2) throws InsufficientOverlapException {
        train(entityValuesR1.values(), entityValuesR2.values());
        return super.distance(entityValuesR1, entityValuesR2);
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

    private void train(Collection<String> valuesR1, Collection<String> valuesR2) {
        List<Double> numericValuesR1 = new ArrayList<>();
        for (String value : valuesR1) {
            if (value == null){
                continue;
            }
            try {
                numericValuesR1.add(normalize(value)); //lembrando que each value de uma rule é de uma entidade diferente, pois é de uma página diferente e cada página representa uma entidade diferente
            } catch (NoiseException ex) {
                //Não faz nada -- possívelmente é algum lixo que a regra está pegando
                //Logger.getLogger(NumberDistance.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        double avgR1 = getAverageAbsoluteValues(numericValuesR1);

        List<Double> numericValuesR2 = new ArrayList<>();
        for (String value : valuesR2) {
            if (value == null){
                continue;
            }
            try {
                numericValuesR2.add(normalize(value)); //lembrando que each value de uma rule é de uma entidade diferente, pois é de uma página diferente e cada página representa uma entidade diferente
            } catch (NoiseException ex) {
                //Não faz nada -- possívelmente é algum lixo que a regra está pegando
                // Logger.getLogger(NumberDistance.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        double avgR2 = getAverageAbsoluteValues(numericValuesR2);

        p = Math.min(avgR1, avgR2) * TETA;

    }

    public Double normalize(String numericValue) throws NoiseException {
        String aux = numericValue;
        aux = aux.replaceAll("R\\$", "").replaceAll("\\$", "").replaceAll("€", ""); //retirando o simbolo de moeda pq currencyXnumber=numberXnumber, ou seja, se uma das regras e number, compara tudo com number
        aux = aux.replaceAll("[a-zA-Z]", ""); //retirando cm, m, etc. mesmo motivo acima

        NumberFormat form01 = NumberFormat.getNumberInstance(new Locale("en", "US"));
        try {
            return form01.parse(aux.trim()).doubleValue();
        } catch (ParseException ex) {
            throw new NoiseException(numericValue, DataType.NUMBER);
        }
    }
}
