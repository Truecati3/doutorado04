/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2;

import br.edimarmanica.dataset.weir.finance.Site;
import br.edimarmanica.weir2.distance.CurrencyDistance;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class Teste {

    public static void main(String[] args) {
        System.out.println(Site.values()[4]);
        Locale local = new Locale("en", "US");
        NumberFormat form01 = NumberFormat.getCurrencyInstance(local);
        String aux = "($19.0)";
        
        try {
            System.out.println(form01.format(-19.15));
            System.out.println(form01.parse(aux).doubleValue());

        } catch (ParseException ex) {
            Logger.getLogger(CurrencyDistance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
