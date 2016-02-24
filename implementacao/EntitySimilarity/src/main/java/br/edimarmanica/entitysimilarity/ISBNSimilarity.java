/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;

/**
 *
 * @author edimar
 */
public class ISBNSimilarity extends TypeAwareSimilarity {

    /**
     * 1 if valueR1 == valueR2, 0 otherwise
     *
     * @param valueR1
     * @param valueR2
     * @return
     */
    @Override
    public double similaritySpecific(String valueR1, String valueR2) {
        String auxR1 = normalize(valueR1);
        String auxR2 = normalize(valueR2);

        if (auxR1.equals(auxR2)) {
            return 1;
        } else {
            return 0;
        }

    }

    /**
     *
     * @param isbn
     * @return só ficam os números
     */
    private static String normalize(String isbn) {
        String aux = isbn.replaceAll("[^\\d]", ""); //só ficam os números
        return aux;
    }
}
