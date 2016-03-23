/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.htmltocsvtoneo4j.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import java.io.File;

/**
 *
 * @author edimar
 */
public class CheckUrlsNumber {
    public static void check(Site site) {
        System.out.println("Site: " + site.getFolderName());

        Neo4jHandler neo4j = new Neo4jHandler(site);

        String query = "MATCH (n) return count(distinct n.URL) AS urls";
        Long nrUrls = (Long) neo4j.querySingleColumn(query, "urls").get(0);
        System.out.println("\t URLs: " + nrUrls);

        neo4j.shutdown();
        
        File dir = new File(Paths.PATH_BASE + site.getPath());
        int nrPagesSite = dir.list().length;
        
        if (nrPagesSite == nrUrls){
            System.out.println("Ok");
        }else{
            System.out.println("Nops: Esperado=["+nrPagesSite+"] - Encontrado=["+nrUrls+"]");
        }
    }
    
    public static void main(String[] args) {
         Site site = br.edimarmanica.dataset.weir.videogame.Site.GAMES;
         check(site);
    }
}
