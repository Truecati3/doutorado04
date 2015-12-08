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
import java.util.HashSet;
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
                    entities.add(record.get("entityID"));
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
