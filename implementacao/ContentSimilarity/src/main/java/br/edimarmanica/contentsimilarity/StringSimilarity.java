/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.contentsimilarity;

import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.UnsmoothedJS;
import com.wcohen.ss.api.StringWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author edimar
 */
public class StringSimilarity extends TypeAwareSimilarity {

    private UnsmoothedJS similarity;

    @Override
    public double similarity(Collection<String> r1, Collection<String> r2) {
        train(r1, r2);

        return super.similarity(r1, r2);
    }

    /**
     * train the similarity on some strings - in general, this would be a large
     * corpus of existing strings, so that some meaningful frequency estimates
     * can be accumulated. for efficiency, you train on an iterator over
     * StringWrapper objects, which are produced with the 'prepare' function.
     *
     * @param r1
     * @param s1
     * @return
     */
    private void train(Collection<String> r1, Collection<String> r2) {
        similarity = new UnsmoothedJS();

        /**
         * add the values of r2 to the corpus
         */
        List<StringWrapper> list = new ArrayList<>();
        for (String value : r1) {
            /**
             * Na linha 32 do JensenShannonDistance tem um bug que ele chama o
             * next duas vezes. Desta forma ele sempre perde os valores da
             * posição par. Lembrando que índice começa em zero
             */
            list.add(similarity.prepare("nulo"));
            list.add(similarity.prepare(normalize(value)));
        }

        for (String value : r2) {
            /**
             * Na linha 32 do JensenShannonDistance tem um bug que ele chama o
             * next duas vezes. Desta forma ele sempre perde os valores da
             * posição par. Lembrando que índice começa em zero
             */
            list.add(similarity.prepare("nulo"));
            list.add(similarity.prepare(normalize(value)));
        }

        similarity.train(new BasicStringWrapperIterator(list.iterator()));
    }

    @Override
    public double similaritySpecific(String valueR1, Collection<String> valuesR2) {
        double maxSimilarity = 0;
        
        for(String valueR2: valuesR2){
            double currentSim = similarity.score(valueR1, valueR2);
            if (maxSimilarity < currentSim){
                maxSimilarity = currentSim;
            }
        }
        
        return maxSimilarity;
    }

    public String normalize(String st) {
        return st.toLowerCase().trim().replaceAll("\\s+", "");
    }
}
