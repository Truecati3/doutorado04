/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.distance;

import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.UnsmoothedJS;
import com.wcohen.ss.api.StringWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class StringDistance extends TypeAwareDistance {

    private UnsmoothedJS distance;

    @Override
    public double distance(Map<String, String> entityValuesR1, Map<String, String> entityValuesR2) throws InsufficientOverlapException {
        train(entityValuesR1.values(), entityValuesR2.values());

        return super.distance(entityValuesR1, entityValuesR2);
    }

    /**
     * train the distance on some strings - in general, this would be a large
     * corpus of existing strings, so that some meaningful frequency estimates
     * can be accumulated. for efficiency, you train on an iterator over
     * StringWrapper objects, which are produced with the 'prepare' function.
     *
     * @param valuesR1
     * @param valuesR2
     * @return
     */
    private void train(Collection<String> valuesR1, Collection<String> valuesR2) {
        distance = new UnsmoothedJS();

        /**
         * add the values of r1 and s1 to the corpus
         */
        List<StringWrapper> list = new ArrayList<>();
        for (String value : valuesR1) {
            if (value == null){
                continue;
            }
            /**
             * Na linha 32 do JensenShannonDistance tem um bug que ele chama o
             * next duas vezes. Desta forma ele sempre perde os valores da
             * posição par. Lembrando que índice começa em zero
             */
            list.add(distance.prepare("nulo"));
            list.add(distance.prepare(normalize(value)));
        }

        for (String value : valuesR2) {
            if (value == null){
                continue;
            }
            /**
             * Na linha 32 do JensenShannonDistance tem um bug que ele chama o
             * next duas vezes. Desta forma ele sempre perde os valores da
             * posição par. Lembrando que índice começa em zero
             */
            list.add(distance.prepare("nulo"));
            list.add(distance.prepare(normalize(value)));
        }

        distance.train(new BasicStringWrapperIterator(list.iterator()));
    }

    @Override
    public double distanceSpecific(String vR1, String vR2) {
        return 1 - distance.score(vR1, vR2); //1- pois quero a distância e ele dá a similaridade
    }

    public String normalize(String st) {
        return st.toLowerCase().trim().replaceAll("\\s+", "");
    }
}
