/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import java.io.File;

/**
 *
 * @author edimar
 */
public class CheckNrSites {
    public static boolean check(Site site){
        File dir = new File(Paths.PATH_TRINITY + site.getPath());
        return dir.exists();
    }
    
    public static void main(String[] args) {
        for(Dataset dataset: Dataset.values()){
            System.out.println("Dataset: "+dataset);
            for(Domain domain: dataset.getDomains()){
                System.out.println("\tDomain: "+domain);
                for(Site site: domain.getSites()){
                    if (!check(site)){
                        System.out.println("\t\tProblemas no site: "+site);
                    }
                }
            }
        }
    }
}
