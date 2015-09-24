/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.weir;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Site;
import java.io.File;

/**
 *
 * @author edimar
 */
public enum Domain implements br.edimarmanica.dataset.Domain {

    BOOK("book"), FINANCE("finance"), SOCCER("soccer"), VIDEOGAME("videogame");
    private String folderName;

    private Domain(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public String getFolderName() {
        return folderName;
    }

    @Override
    public Dataset getDataset() {
        return Dataset.WEIR;
    }

    @Override
    public Attribute getAttributebyMyID(String attributeID) {
        for (Attribute attr : br.edimarmanica.dataset.weir.book.Attribute.values()) {
            if (attr.getAttributeID().equals(attributeID)) {
                return attr;
            }
        }
        return null;
    }

    @Override
    public Attribute getAttributeIDbyDataset(String attributeIDbyDataset) {
        for (Attribute attr : br.edimarmanica.dataset.weir.book.Attribute.values()) {
            if (attr.getAttributeIDbyDataset().equals(attributeIDbyDataset)) {
                return attr;
            }
        }
        return null;
    }

    @Override
    public String getPath() {
        return getDataset().getFolderName() + File.separator + getFolderName();
    }

    @Override
    public Attribute[] getAttributes() {
        switch (this) {
            case BOOK:
                return br.edimarmanica.dataset.weir.book.Attribute.values();
            case FINANCE:
                return br.edimarmanica.dataset.weir.finance.Attribute.values();
            case SOCCER:
                return br.edimarmanica.dataset.weir.soccer.Attribute.values();
            case VIDEOGAME:
                return br.edimarmanica.dataset.weir.videogame.Attribute.values();
            default:
                throw new UnsupportedOperationException("Domain not configurated yet!");
        }

    }

    @Override
    public Site[] getSites() {
        switch (this) {
            case BOOK:
                return br.edimarmanica.dataset.weir.book.Site.values();
            case FINANCE:
                return br.edimarmanica.dataset.weir.finance.Site.values();
            case SOCCER:
                return br.edimarmanica.dataset.weir.soccer.Site.values();
            case VIDEOGAME:
                return br.edimarmanica.dataset.weir.videogame.Site.values();
            default:
                throw new UnsupportedOperationException("Domain not configurated yet!");
        }
    }
}
