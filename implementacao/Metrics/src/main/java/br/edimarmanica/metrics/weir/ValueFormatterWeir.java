/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.weir;

/**
 *
 * @author edimar
 */
public class ValueFormatterWeir implements br.edimarmanica.metrics.ValueFormatter{

    @Override
    public String format(String value) {
        return value.trim();
    }
    
}
