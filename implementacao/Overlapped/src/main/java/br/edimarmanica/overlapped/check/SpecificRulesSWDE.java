/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.overlapped.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.overlapped.LoadSite;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class SpecificRulesSWDE {

    private Site s1;
    private Map<String, String> rS1;
    private Site s2;
    private Map<String, String> rS2;

    public SpecificRulesSWDE(Site s1, int rS1, Site s2, int rS2) {
        this.s1 = s1;
        this.rS1 = loadRuleValues(s1, rS1);
        this.s2 = s2;
        this.rS2 = loadRuleValues(s2, rS2);
    }

    private Map<String, String> loadRuleValues(Site site, int ruleID) {
        Map<String, String> values = new HashMap<>();
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values/rule_" + ruleID + ".csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    values.put(formatUrl(site, record.get("URL")), record.get("EXTRACTED VALUE"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SpecificRulesSWDE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SpecificRulesSWDE.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return values;
    }
    
    private String formatUrl(Site site, String url){
        if (site.getDomain().getDataset() == Dataset.WEIR){
            return url.replaceAll(".*" + site.getDomain().getDataset().getFolderName() + "/", "");
        }else if (site.getDomain().getDataset() == Dataset.SWDE){
            return url.replaceAll(".*" + site.getPath() + "/", "").replaceAll(".htm", "");
        }else{
            return null;
        }
    }

    private void printShareEntities() {
        Map<String, String> urlEntityS1 = LoadSite.getURL_Entity(s1);
        Map<String, String> entityUrlS2 = LoadSite.getEntity_URL(s2);

        for (String urlS1 : rS1.keySet()) {
            String entity = urlEntityS1.get(urlS1 + ".htm");

            if (entityUrlS2.containsKey(entity)) {//compartilham a mesma entidade
                String urlS2 = entityUrlS2.get(entity);
                System.out.println(urlS1 + " X " + urlS2);
                System.out.println(rS1.get(urlS1) + " X " + rS2.get(urlS2.replaceAll(".htm", "")));
            }
        }
    }

    public static void main(String[] args) {
        Site s1 = br.edimarmanica.dataset.swde.movie.Site.MSN;
        int rS1 = 189;
        Site s2 = br.edimarmanica.dataset.swde.movie.Site.BOXOFFICEMOJO;
        int rS2 = 6708;

        SpecificRulesSWDE sr = new SpecificRulesSWDE(s1, rS1, s2, rS2);
        sr.printShareEntities();
    }
}
