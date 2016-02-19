/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics;

/**
 *
 * @author edimar
 */
public class SiteWithoutThisAttribute extends Exception{

    public SiteWithoutThisAttribute(String attribute, String site) {
        super("Site "+site+" does not have the attribute "+attribute);
    }
    
}
