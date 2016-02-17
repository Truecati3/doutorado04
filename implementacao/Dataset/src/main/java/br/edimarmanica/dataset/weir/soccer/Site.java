/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.weir.soccer;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.weir.Domain;
import java.io.File;

/**
 *
 * @author edimar
 */
public enum Site implements br.edimarmanica.dataset.Site {

    REUTERS("football.uk.reuters.com"), CNN("sports.sportsillustrated.cnn.com"), FOOTBALL("www.123football.com"), SKYSPORTS("www.skysports.com"),
    WALLPAPER("www.wallpaper-football.com"), ESPN("soccernet.espn.go.com"), YAHOO("uk.eurosport.yahoo.com"),
    FOOTYMANIA("www.footymania.com"), TEAMTALK("www.teamtalk.com"), WORLDFOOTBALLERS("www.worldfootballers.com");
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
        return Domain.SOCCER;
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
