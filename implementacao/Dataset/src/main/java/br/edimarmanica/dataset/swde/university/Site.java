/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.swde.university;

import br.edimarmanica.dataset.swde.Domain;
import java.io.File;

/**
 *
 * @author edimar
 */
public enum Site implements br.edimarmanica.dataset.Site {

    COLLEGEBOARD("university-collegeboard"), COLLEGEPROWLER("university-collegeprowler"), ECAMPUSTOURS("university-ecampustours"),
    MATCHCOLLEGE("university-matchcollege"), STUDENTAID("university-studentaid"), COLLEGENAVIGATOR("university-collegenavigator"),
    COLLEGETOOLKIT("university-collegetoolkit"), EMBARK("university-embark"),
    PRINCETONREVIEW("university-princetonreview"), USNEWS("university-usnews");
    private String folderName;

    private Site(String folderName) {
        this.folderName = folderName;
    }

    @Override
    public String getFolderName() {
        return folderName;
    }

    @Override
    public Domain getDomain() {
        return Domain.UNIVERSITY;
    }

    @Override
    public String getPath() {
        return getDomain().getPath() + File.separator + getFolderName();
    }

    @Override
    public String getGroundTruthPath() {
        return getDomain().getDataset().getFolderName() + File.separator + "groundtruth/" + getDomain().getFolderName() + File.separator + getFolderName() + "-!attrName!.txt";
    }

    @Override
    public String getEntityPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
