/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.dataset.weir.videogame;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.weir.Domain;
import java.io.File;

/**
 *
 * @author edimar
 */
public enum Site implements br.edimarmanica.dataset.Site {

    TEAMBOX("games.teamxbox.com"), ASKMEN("www.askmen.com"), METACRITIC("www.metacritic.com"),
    GAMESPY("ps2.gamespy.com"), CDUNIVERSE("www.cduniverse.com"), NINTENDO("www.nintendo.com"),
    CNET("reviews.cnet.com"), GAMEQUESTDIRECT("www.gamequestdirect.com"),
    YAHOO("videogames.yahoo.com"), GAMES("www.games.net");
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
        return Domain.VIDEOGAME;
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
