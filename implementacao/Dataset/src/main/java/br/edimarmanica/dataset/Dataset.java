/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset;

/**
 *
 * @author edimar
 */
public enum Dataset {

    WEIR("WEIR"), SWDE("SDWE");

    private String folderName;

    private Dataset(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
