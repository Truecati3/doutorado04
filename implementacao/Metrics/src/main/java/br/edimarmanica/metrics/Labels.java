/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
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
public class Labels {

    private Site site;
    private Map<String, String> labels = new HashMap<>(); //<RuleName,Label>

    public Labels(Site site) {
        this.site = site;
    }

    public void load() {
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/rule_info.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    labels.put("rule_" + record.get("ID") + ".csv", record.get("LABEL"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Labels.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Labels.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, String> getLabels() {
        return labels;
    }
}
