/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.swde;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Site;
import java.io.File;

/**
 *
 * @author edimar
 */
public enum Domain implements br.edimarmanica.dataset.Domain {

    AUTO("auto"), BOOK("book"), CAMERA("camera"), JOB("job"), MOVIE("movie"), 
    NBA_PLAYER("nbaplayer"), RESTAURANT("restaurant"), UNIVERSITY("university");
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
        return Dataset.SWDE;
    }

    @Override
    public Attribute getAttributebyMyID(String attributeID) {
        for (Attribute attr : br.edimarmanica.dataset.swde.auto.Attribute.values()) {
            if (attr.getAttributeID().equals(attributeID)) {
                return attr;
            }
        }
        return null;
    }

    @Override
    public Attribute getAttributeIDbyDataset(String attributeIDbyDataset) {
        for (Attribute attr : br.edimarmanica.dataset.swde.auto.Attribute.values()) {
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
            case AUTO:
                return br.edimarmanica.dataset.swde.auto.Attribute.values();
            default:
                throw new UnsupportedOperationException("Domain not configurated yet!");
        }

    }

    @Override
    public Site[] getSites() {
        switch (this) {
            case AUTO:
                return br.edimarmanica.dataset.swde.auto.Site.values();
            default:
                throw new UnsupportedOperationException("Domain not configurated yet!");
        }
    }
}
