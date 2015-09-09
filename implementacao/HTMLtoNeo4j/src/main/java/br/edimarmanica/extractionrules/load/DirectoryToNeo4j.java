/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.load;

import br.edimarmanica.dataset.Configuration;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.extractionrules.neo4j.Neo4JHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Transaction;

/**
 *
 * load a directory of pages to the neo4j
 *
 * @author edimar
 */
public class DirectoryToNeo4j {

    private Neo4JHandler neo4j;
    private String dir;

    /**
     *
     * @param dir directory with pages to be loaded
     * @param deleteCurrentDatabase if we should delete current database
     */
    public DirectoryToNeo4j(String dir, boolean deleteCurrentDatabase) {

        if (deleteCurrentDatabase) {
            Neo4JHandler.deleteDatabase();
        }

        this.dir = dir;

    }

    /**
     * load the pages in the directory (dir) to the neo4j
     *
     * @throws FileNotFoundException
     */
    public void loadPages() throws FileNotFoundException {
        neo4j = new Neo4JHandler();

        try {
            File fDir = new File(dir);
            for (File f : fDir.listFiles()) {
                try (Transaction tx1 = neo4j.beginTx()) {
                    loadPage(f);
                    tx1.success();
                    tx1.close();
                } catch (Exception ex) {
                    System.out.println("Erro na página: " + f.getAbsolutePath() + "\n" + ex.getMessage());
                }
            }
        } finally {
            neo4j.shutdown();
        }
    }

    private void loadPage(File page) {
        HtmlToNeo4j hh = new HtmlToNeo4j(page.getAbsolutePath(), neo4j);
        hh.insertAllNodes();
    }

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.weir.finance.Site.BARCHART;
        DirectoryToNeo4j load = new DirectoryToNeo4j(Configuration.PATH_BASE + "/" + site.getPath(), true);
        try {
            load.loadPages();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DirectoryToNeo4j.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
