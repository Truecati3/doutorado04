/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.load;

import br.edimarmanica.dataset.Configuration;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandler;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerLocal;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private Neo4jHandlerType type;

    /**
     *
     * @param dir directory with pages to be loaded
     * @param deleteCurrentDatabase if we should delete current database
     */
    public DirectoryToNeo4j(Site site, boolean deleteCurrentDatabase, Neo4jHandlerType type) {

        if (deleteCurrentDatabase && type == Neo4jHandlerType.LOCAL) {
            Neo4jHandlerLocal.deleteDatabase(site);
        }

        this.site = site;
        this.type = type;
    }

    /**
     * load the pages in the directory (dir) to the neo4j
     *
     * @throws FileNotFoundException
     */
    public void loadPages() throws FileNotFoundException {


        File fDir = new File(Configuration.PATH_BASE + "/" + site.getPath());
        neo4j = Neo4jHandler.getInstance(type, site);
        try (Transaction tx1 = neo4j.beginTx()) {
            int i = 0;
            for (File f : fDir.listFiles()) {
                
                /*if (i <= 470 || i > 480 ){//400 - 450 deu erro
                    i++;
                    continue;
                }
                printMemoryInfo();
                
                System.out.println("i: " + i + "-" + f.getAbsolutePath()); */
                loadPage(f);
                i++;
            }
            tx1.success();
            tx1.close();

        }

        if (type == Neo4jHandlerType.LOCAL) {
            neo4j.shutdown();
        }
    }


    private void loadPage(File page) {
        HtmlToNeo4j hh = new HtmlToNeo4j(page.getAbsolutePath(), neo4j);
        hh.insertAllNodes();
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
        Site site = br.edimarmanica.dataset.weir.videogame.Site.CDUNIVERSE;
        DirectoryToNeo4j load = new DirectoryToNeo4j(site, false, Neo4jHandlerType.LOCAL);
        try {
            load.loadPages();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DirectoryToNeo4j.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * fechar ResourceIterator:
         * http://neo4j.com/docs/stable/tutorials-java-embedded-resource-iteration.html
         */
    }
}
