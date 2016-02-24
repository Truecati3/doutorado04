/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.htmltocsvtoneo4j.check;

import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;

/**
 *
 * @author edimar
 */
public class CheckRelNumber {
    public static void check(Site site) {
        System.out.println("Site: " + site.getFolderName());

        Neo4jHandler neo4j = new Neo4jHandler(site);

        String query = "MATCH (n) return count(distinct n.URL) AS urls";
        Long nrUrls = (Long) neo4j.querySingleColumn(query, "urls").get(0);
        System.out.println("\t URLs: " + nrUrls);


        query = "MATCH (n) RETURN count(n) AS nrNodes";
        Long nrNodes = (Long) neo4j.querySingleColumn(query, "nrNodes").get(0);
        System.out.println("\t nrNodes: " + nrNodes);

        query = "MATCH p=(n)-->(m) RETURN count(p) AS nrRels";
        Long nrRels = (Long) neo4j.querySingleColumn(query, "nrRels").get(0);
        System.out.println("\t nrRels: " + nrRels);
        
        if (nrNodes - nrUrls - nrRels == 0) {
            System.out.println("\t OK!");
        } else {
            System.out.println("\t NOPS!");
        }

        neo4j.shutdown();
    }
    
    public static void main(String[] args) {
         Site site = br.edimarmanica.dataset.swde.nba.Site.USATODAY;
         check(site);
    }
}
