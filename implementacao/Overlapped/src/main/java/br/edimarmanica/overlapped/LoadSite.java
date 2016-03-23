/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.overlapped;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
public class LoadSite {

    /**
     *
     * @param site
     * @return Set of entities
     */
    public static Set<String> getEntities(Site site) {
        Set<String> entities = new HashSet<>();

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getEntityPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    entities.add(record.get("entityID").trim());
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadSite.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadSite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entities;
    }
    
     /**
     *
     * @param site
     * @return Map<URL, EntityID>
     */
    public static Map<String,String> getURL_Entity(Site site) {
        Map<String, String> entities = new HashMap<>();

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getEntityPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    entities.put(record.get("url").trim(), record.get("entityID").trim());
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadSite.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadSite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entities;
    }
    
     /**
     *
     * @param site
     * @return Map<EntityID, URL>
     */
    public static Map<String,String> getEntity_URL(Site site) {
        Map<String, String> entities = new HashMap<>();

        try (Reader in = new FileReader(Paths.PATH_BASE + site.getEntityPath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    entities.put(record.get("entityID").trim(), record.get("url").trim());
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadSite.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadSite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return entities;
    }
}
