/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;



/**
 *
 * @author edimar
 */
public class PhoneSimilarity extends TypeAwareSimilarity {

    /**
     * 1 if valueR1 == valueR2, 0 otherwise
     *
     * @param valueR1
     * @param valueR2
     * @return
     */
    @Override
    public double similaritySpecific(String valueR1, String valueR2) {
        String auxR1;
        try {
            auxR1 = normalize(valueR1);
        } catch (NoiseException ex) {
            return 0; 
        }
        String auxR2;
        try {
            auxR2 = normalize(valueR2);
        } catch (NoiseException ex) {
            return 0;
        }

        if (auxR1.equals(auxR2)) {
            return 1;
        } else {
            return 0;
        }

    }

    /**
     *
     * @param phone
     * @return só ficam os números e apenas os últimos 4 - devido a ramal
     */
    private static String normalize(String phone) throws NoiseException {
        String aux = phone.replaceAll("[^\\d]", ""); //só ficam os números
        
        if (phone.length() < 8){
            throw new NoiseException(phone, DataType.PHONE);
        }
        
        return aux.substring(aux.length() - 4);

    }
}
