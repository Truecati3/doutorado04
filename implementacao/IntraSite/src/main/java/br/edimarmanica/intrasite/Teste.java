/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.swde.auto.Site;
import br.edimarmanica.expressiveness.generate.CypherNotation;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import java.io.File;
import java.util.List;

/**
 *
 * @author edimar
 */
public class Teste {

    public static void main(String[] args) {

        Dataset dataset = Dataset.SWDE;
        for (Domain domain : dataset.getDomains()) {


            for (br.edimarmanica.dataset.Site site : domain.getSites()) {
                System.out.println(domain.getFolderName()+";"+site.getFolderName());
            }
        }
    }
}
