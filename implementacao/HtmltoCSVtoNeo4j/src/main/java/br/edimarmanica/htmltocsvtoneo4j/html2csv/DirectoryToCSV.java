/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.htmltocsvtoneo4j.html2csv;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * load a directory of pages to the neo4j
 *
 * @author edimar
 */
public class DirectoryToCSV {

    private Site site;
    private long currentNodeID = 0;
    private boolean append = false;

    /**
     *
     * @param dir directory with pages to be loaded
     * @param deleteCurrentDatabase if we should delete current database
     */
    public DirectoryToCSV(Site site) {
        this.site = site;
    }

    public void loadPages() throws FileNotFoundException {
        File fDir = new File(Paths.PATH_BASE + "/" + site.getPath());
        int i = 0;
        for (File f : fDir.listFiles()) {
            i++;
            /*if (nr<0 || nr > 460){
             continue;
             }*/
            if (General.DEBUG) {
                System.out.println("Page["+i+"]:" + f.getAbsolutePath());
            }
            loadPage(f);
        }
    }

    private void loadPage(File page) {
        HtmlToCSV hh = new HtmlToCSV(page.getAbsolutePath(), site, currentNodeID, append);
        currentNodeID = hh.insertAllNodes() + 1;
        append = true;
    }

    public static void main(String[] args) {
        General.DEBUG = true;

        //   Domain domain = br.edimarmanica.dataset.swde.Domain.JOB;
        //   for (Site site : domain.getSites()) {
        //       System.out.println("Site: " + site.getFolderName());
        //     DirectoryToCSV load = new DirectoryToCSV(site);
        DirectoryToCSV load = new DirectoryToCSV(br.edimarmanica.dataset.weir.videogame.Site.GAMES);

        try {
            load.loadPages();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DirectoryToCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
        //}
    }
}
