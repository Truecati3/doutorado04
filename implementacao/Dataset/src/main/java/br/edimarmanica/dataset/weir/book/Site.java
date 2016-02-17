/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.weir.book;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.weir.Domain;
import java.io.File;

/**
 *
 * @author edimar
 */
public enum Site implements br.edimarmanica.dataset.Site {

    BOOKMOOCH("bookmooch.com"), BLACKWELL("bookshop.blackwell.co.uk"), AMAZON("www.amazon.com"), BARNESANDNOBLE("www.barnesandnoble.com"), BOOKDEPOSITORY("www.bookdepository.co.uk"),
    BOOKFINDER4U("www.bookfinder4u.com"), BOOKRENTER("www.bookrenter.com"), BOOKSANDEBOOKS("www.booksandebooks.net"), ECAMPUS("www.ecampus.com"), GOODREADS("www.goodreads.com");
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
        return Domain.BOOK;
    }

    @Override
    public String getPath() {
        return getDomain().getPath() + File.separator + getFolderName();
    }
    
    @Override
    public String getGroundTruthPath(){
        return getDomain().getDataset().getFolderName() + File.separator + "groundtruth/" + getDomain().getFolderName() + File.separator + getFolderName()+".csv";
    }

    @Override
    public String getEntityPath() {
        return getDomain().getDataset().getFolderName() + File.separator + "entity/" + getDomain().getFolderName() + File.separator + getFolderName()+".csv";
    }

    @Override
    public String getGroundTruthPath(Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
