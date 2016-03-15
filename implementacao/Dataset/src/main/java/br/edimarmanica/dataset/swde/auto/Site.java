/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.swde.auto;
import br.edimarmanica.dataset.swde.Domain;
import java.io.File;

/**
 *
 * @author edimar
 */
public enum Site implements br.edimarmanica.dataset.Site {

    AOL("auto-aol"), AUTOBYTEL("auto-autobytel"), AUTOMOTIVE("auto-automotive"), AUTOWEB("auto-autoweb"), CARQUOTES("auto-carquotes"),
    CARS("auto-cars"), KBB("auto-kbb"), MOTORTREND("auto-motortrend"), MSN("auto-msn"), YAHOO("auto-yahoo");
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
        return Domain.AUTO;
    }

    @Override
    public String getPath() {
        return getDomain().getPath() + File.separator + getFolderName();
    }
    
    @Override
    public String getGroundTruthPath(br.edimarmanica.dataset.Attribute attr){
        return getDomain().getDataset().getFolderName() + File.separator + "groundtruth/" + getDomain().getFolderName() + File.separator + getFolderName()+"-"+attr.getAttributeID()+".txt";
    }

    @Override
    public String getGroundTruthPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEntityPath() {
        return getDomain().getDataset().getFolderName() + File.separator + "entity/" + getDomain().getFolderName() + File.separator + getFolderName()+".csv";
    }
}
