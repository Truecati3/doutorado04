/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.load;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandler;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerLocal;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;
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

    private Neo4jHandler neo4j;
    private Site site;

    /**
     *
     * @param dir directory with pages to be loaded
     * @param deleteCurrentDatabase if we should delete current database
     */
    public DirectoryToNeo4j(Site site, boolean deleteCurrentDatabase) {

        if (deleteCurrentDatabase && General.NEO4J_TYPE == Neo4jHandlerType.LOCAL) {
            Neo4jHandlerLocal.deleteDatabase(site);
        }

        this.site = site;
    }

    public void loadPages() throws FileNotFoundException {


        File fDir = new File(Paths.PATH_BASE + "/" + site.getPath());

        int i = 0;
        for (File f : fDir.listFiles()) {

            if (i < 0 || i >= 50) {//400 - 450 deu erro
                i++;
                continue;
            }

            if (General.DEBUG) {
                printMemoryInfo();
                System.out.println("i: " + i + "-" + f.getAbsolutePath());
            }

            if (i % 10 == 0) {//para efeciencia. A cada 10 páginas para, fecha o banco, assim libera um pouco de memória
                neo4j = Neo4jHandler.getInstance(site);
            }
            try (Transaction tx1 = neo4j.beginTx()) {

                loadPage(f);
                tx1.success();
                tx1.close();
            }
            if ((i+1) % 10 == 0) { //para efeciencia. A cada 10 páginas para, fecha o banco, assim libera um pouco de memória
                if (General.NEO4J_TYPE == Neo4jHandlerType.LOCAL) {
                    neo4j.shutdown();
                    neo4j = null;
                    System.gc();
                }
            }
            i++;
        }
    }

    private void loadPage(File page) {
        HtmlToNeo4j hh = new HtmlToNeo4j(page.getAbsolutePath(), neo4j);
        hh.insertAllNodes();
        hh = null;
        System.gc();
    }

    private static void printMemoryInfo() {
        double gb = 1024 * 1024 * 1024;

        System.out.println("#### Heap utilization statistics [GB]: ###");

        Runtime runtime = Runtime.getRuntime();

        System.out.println("Max available memory: " + runtime.maxMemory() / gb);
        System.out.println("Total memory: " + runtime.totalMemory() / gb);
        System.out.println("Used memory: " + (runtime.totalMemory() - runtime.freeMemory()) / gb);
        System.out.println("Free memory: " + runtime.freeMemory() / gb);
    }

    public static void main(String[] args) {
        General.DEBUG = true;
        Site site = br.edimarmanica.dataset.swde.auto.Site.MSN;
        DirectoryToNeo4j load = new DirectoryToNeo4j(site, true);
        try {
            load.loadPages();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DirectoryToNeo4j.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
