/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.weir.finance;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.weir.Domain;
import java.io.File;

/**
 *
 * @author edimar
 */
public enum Site implements br.edimarmanica.dataset.Site {

    BIGCHARTS("bigcharts.marketwatch.com"), MONEYCENTRAL("moneycentral.msn.com"), BARCHART("quote.barchart.com"), FREEREALTIME("quotes.freerealtime.com"),
    NASDAQ("quotes.nasdaq.com"), STOCKNOD("quotes.stocknod.com"), BLOOMBERG("www.bloomberg.com"), MAKETOCRACY("www.marketocracy.com"),
    MARKETWATCH("www.marketwatch.com"), QUOTE("www.quote.com");
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
        return Domain.FINANCE;
    }

    @Override
    public String getPath() {
        return getDomain().getPath() + File.separator + getFolderName();
    }

    @Override
    public String getGroundTruthPath() {
        return getDomain().getDataset().getFolderName() + File.separator + "groundtruth/" + getDomain().getFolderName() + File.separator + getFolderName() + ".csv";
    }

    @Override
    public String getEntityPath() {
        return getDomain().getDataset().getFolderName() + File.separator + "entity/" + getDomain().getFolderName() + File.separator + getFolderName() + ".csv";
    }

    @Override
    public String getGroundTruthPath(Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
