/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.distance;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.InterSite;
import br.edimarmanica.weir2.rule.type.DataType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public abstract class TypeAwareDistance {

    /**
     *
     * @param entityValuesR1 Map<entity,value> -- se a regra não extraiu valor
     * na página que descreve a entidade, deve colocar o valor
     * @param typeR1
     * @param entityValuesR2 Map<entity,value> -- se a regra não extraiu valor
     * na página que descreve a entidade, deve colocar o valor
     * @param typeR2
     * @return
     */
    public static double typeDistance(Map<String, String> entityValuesR1, DataType typeR1, Map<String, String> entityValuesR2, DataType typeR2) {
        
        if (typeR1 != typeR2) {
            return 1;
        }

        TypeAwareDistance distance;
        switch (typeR1) {
            case DATE:
                distance = new DateDistance();
                break;
            case ISBN:
                distance = new ISBNDistance();
                break;
            case PHONE:
                distance = new PhoneDistance();
                break;
            case CURRENCY:
                distance = new CurrencyDistance();
                break;
            case LENGHT:
                distance = new LenghtDistance();
                break;
            case WEIGHT:
                distance = new WeightDistance();
                break;
            case NUMBER:
                distance = new NumberDistance();
                break;
            case STRING:
                distance = new StringDistance();
                break;
            default:
                distance = null;
        }

        double distanceValue;
        try {
            distanceValue = distance.distance(entityValuesR1, entityValuesR2);
        } catch (InsufficientOverlapException ex) {
            distanceValue = 1;
        }
        return distanceValue;
    }

    /**
     *
     * @param entityValuesR1 Map<entity,value> -- se a regra não extraiu valor
     * na página que descreve a entidade, deve colocar o valor
     * nullMap<entity,value>
     * @param entityValuesR2 Map<entity,value> -- se a regra não extraiu valor
     * na página que descreve a entidade, deve colocar o valor
     * nullMap<entity,value>
     * @return
     * @throws InsufficientOverlapException
     */
    public double distance(Map<String, String> entityValuesR1, Map<String, String> entityValuesR2) throws InsufficientOverlapException {

        Set<String> sharedEntityIds = new HashSet<>();
        sharedEntityIds.addAll(entityValuesR1.keySet());
        sharedEntityIds.retainAll(entityValuesR2.keySet());

        if (sharedEntityIds.size() < InterSite.MIN_SHARED_ENTITIES) {
            throw new InsufficientOverlapException(sharedEntityIds.size());
        }

        double distance = 0;
        for (String entity : sharedEntityIds) { //não importam os valores de entidades não compartilhadas
            String valueR1 = entityValuesR1.get(entity);
            String valueR2 = entityValuesR2.get(entity);

            if (valueR1 == null && valueR2 == null) { //os dois não extraem valores para essa entidade
                distance += 0;
            } else if (valueR1 == null || valueR2 == null) { //apenas um extrai valor para essa entidade
                distance += 1;
            } else { //as duas regras extraem valores para a entidade
                distance += distance(valueR1, valueR2);
            }
        }

        return distance / sharedEntityIds.size(); //tem que dividir pelo nr de instâncias compartilhadas entre os sites
    }

    public abstract double distanceSpecific(String vR1, String vR2);

    public double distance(String vR1, String vR2) {
        if (vR1.equals(vR2)) {
            return 0;
        }

        if (vR1.isEmpty() || vR2.isEmpty()) {
            return 1;
        }
        return distanceSpecific(vR1, vR2);
    }
}
