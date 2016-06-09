/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.rule;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Formatter;
import java.io.File;
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
public class Loader {

    /**
     *
     * @param rule
     * @param formatted valores formatados como na avaliação?
     * @return Map<PageId, Value>
     *
     */
    public static Map<String, String> loadPageValues(File rule, boolean formatted) {
        Map<String, String> pageValues = new HashMap<>();

        try (Reader in = new FileReader(rule.getAbsolutePath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) { //para cada value
                    String url = formatURL(record.get("URL"));
                    String value = record.get("EXTRACTED VALUE");
                    
                    if (formatted){
                        value = Formatter.formatValue(value);
                    }
                    
                    if (!value.trim().isEmpty()) {
                        pageValues.put(url, value);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pageValues;
    }
    
    /**
     * 
     * @param rule
     * @param entityIDs Map<page,entity>
     * @return Map<entity, value> value extracted for each entity or null if a value was not extracted for that entity
     */
    public static Map<String, String> loadEntityValues(File rule, Map<String, String> entityIDs){
        Map<String, String> pageValues = loadPageValues(rule, false);
        
        Map<String, String> entityValues = new HashMap<>();
        for(String page: entityIDs.keySet()){
            entityValues.put(page, pageValues.get(page));
        }
        return entityValues;
    }
    
    /**
     *
     * @param site
     * @return Map<Page,Entity>
     */
    public static Map<String, String> loadEntityID(Site site) {
        Map<String, String> ids = new HashMap<>();

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getEntityPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    String url = formatURL(record.get("url"));
                    ids.put(url, record.get("entityID"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ids;
    }
    
    public static String formatURL(String url){
        return url.replaceAll(".*/", "").replaceAll("\\..*", "");
    }

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.weir.book.Site.BOOKMOOCH;
        Map<String, String> entityValues = loadEntityValues(new File(Paths.PATH_INTRASITE+"/"+site.getPath()+"/extracted_values/rule_379.csv"), loadEntityID(site));
        for(String entity: entityValues.keySet()){
            System.out.println(entity+"->"+entityValues.get(entity));
        }
    }
}
