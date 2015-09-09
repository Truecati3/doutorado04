/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.load;

/**
 *
 * @author edimar
 */
public class InvalidTextNode extends Exception {

    public InvalidTextNode(String value) {
        super("Invalid Text Node -- Value:["+value+"]");
    }
    
    
}
