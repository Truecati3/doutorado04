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
import static br.edimarmanica.trinity.check.CheckNrSites.check;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class CheckNrPages {

    private Site site;
    

    public CheckNrPages(Site site) {
        this.site = site;
    }

    private int nrPagesExtracted() {
        Set<String> pages = new HashSet<>();
        File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/offset");

        if (!dir.exists()){
            return 0;
        }
        
        for (File offset : dir.listFiles()) {

            try (Reader in = new FileReader(offset)) {
                try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL)) {
                    for (CSVRecord record : parser) {
                        pages.add(record.get(0));
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CheckNrPages.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CheckNrPages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pages.size();
    }
    
    private int nrPagesBase(){
        File dir = new File(Paths.PATH_BASE + site.getPath());
        return dir.list().length;
    }
    
    public void check(){
        int nrPagesBase = nrPagesBase();
        int nrPagesExtracted = nrPagesExtracted();
        
        if (nrPagesBase != nrPagesExtracted){
            System.out.println("\t\tSite: "+site+" - Expected: "+nrPagesBase+" - Obtido: "+nrPagesExtracted);
        }
    }

    
    public static void main(String[] args) {
        for(Dataset dataset: Dataset.values()){
            System.out.println("Dataset: "+dataset);
            for(Domain domain: dataset.getDomains()){
                System.out.println("\tDomain: "+domain);
                for(Site site: domain.getSites()){
                    CheckNrPages check = new CheckNrPages(site);
                    check.check();
                }
            }
        }
    }
}
