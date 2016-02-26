/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.extract;

/**
 *
 * @author edimar
 */
public class IncorrectBlockException extends Exception {

    private long nrRels;
    private long nrTemplates;

    public IncorrectBlockException(long nrRels, long nrTemplates) {
        super("Blocking was not performed correctly. [TEMPLATES: " + nrTemplates + ", RELS: " + nrRels + "]");
        this.nrRels = nrRels;
        this.nrTemplates = nrTemplates;
    }
}
