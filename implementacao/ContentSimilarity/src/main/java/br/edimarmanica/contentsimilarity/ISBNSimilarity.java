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
public class ISBNSimilarity extends TypeAwareSimilarity {

    /**
     * 1 if datatype(valueR1) = ISBN, 0 otherwise
     *
     * @param valueR1
     * @param valueR2
     * @return
     */
    @Override
    public double similaritySpecific(String valueR1, Collection<String> valueR2) {
        DataType typeR1 = DataTypeController.getDataType(valueR1);

        if (typeR1 == DataType.ISBN) {
            return 1;
        } else {
            return 0;
        }
    }
}
