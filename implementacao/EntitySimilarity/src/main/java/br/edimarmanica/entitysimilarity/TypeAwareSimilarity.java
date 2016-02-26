/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;

import java.util.Map;

/**
 *
 * @author edimar
 */
public abstract class TypeAwareSimilarity {

    /**
     *
     * @param r1 Map<Entity,Value>
     * @param r2 Map<Entity,Value>
     * @return the entity similarity between r1 and r2 -- Baseado na distancia
     * baseada em tipo do WEIR
     */
    public static double typeSimilarity(Map<String, String> r1, Map<String, String> r2) throws InsufficientOverlapException {

        DataType type = DataTypeController.getMostSpecificType(r1, r2);
        TypeAwareSimilarity similarity;

        switch (type) {
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
     * só calcula a similaridade entre os valores referentes a mesma entidade
     * por isso é entity similarity
     *
     * @param r1
     * @param r2
     * @return
     */
    public double similarity(Map<String, String> r1, Map<String, String> r2) throws InsufficientOverlapException {

        double similarity = 0;
        int nrSharedEntities = 0;
        for (String entityR1 : r1.keySet()) {

            String valueR1 = r1.get(entityR1);
            String valueR2 = r2.get(entityR1);

            if (valueR2 != null) { //entidades sobrepostas
                nrSharedEntities++;
                similarity += similarity(valueR1, valueR2); //o problema é quando o site tem a entidade mas a regra não extraiu valor
            }
        }

        if (nrSharedEntities < br.edimarmanica.configuration.TypeAwareSimilarity.MIN_SHARED_ENTITIES) {
            throw new InsufficientOverlapException(nrSharedEntities);
        }

        return similarity / nrSharedEntities; //tem que dividir pelo nr de instâncias compartilhadas entre os sites
    }

    public abstract double similaritySpecific(String valueR1, String valueR2);

    public double similarity(String valueR1, String valueR2) {

        if (valueR1.equals(valueR2)) {
            return 1;
        }

        return similaritySpecific(valueR1, valueR2);
    }
}
