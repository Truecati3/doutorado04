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

    WEIR("WEIR"), SWDE("SWDE");
    private String folderName;

    private Dataset(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }

    /**
     *
     * @param dataset
     * @return
     */
    public Domain[] getDomains() {
        switch (this) {
            case WEIR:
                return br.edimarmanica.dataset.weir.Domain.values();
            case SWDE:
                return br.edimarmanica.dataset.swde.Domain.values();
            default:
                throw new UnsupportedOperationException("Dataset ainda não configurado");
        }
    }
}
