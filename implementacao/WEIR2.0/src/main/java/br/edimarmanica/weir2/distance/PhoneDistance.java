/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.distance;


/**
 *
 * @author edimar
 */
public class PhoneDistance extends TypeAwareDistance {

    /**
     * 0 if vR1 == vR2, 1 otherwise
     *
     * @param vR1
     * @param vR2
     * @return
     */
    @Override
    public double distanceSpecific(String vR1, String vR2) {
        String auxR1 = normalize(vR1);
        String auxR2 = normalize(vR2);

        if (auxR1.equals(auxR2)) {
            return 0;
        } else {
            return 1;
        }

    }

    /**
     *
     * @param phone
     * @return só ficam os números e apenas os últimos 4 - devido a ramal
     */
    private static String normalize(String phone) {
        String aux = phone.replaceAll("[^\\d]", ""); //só ficam os números
        return aux.substring(aux.length() - 4);
    }
}
