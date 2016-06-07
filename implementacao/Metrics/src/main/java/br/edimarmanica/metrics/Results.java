package br.edimarmanica.metrics;

import br.edimarmanica.dataset.Site;
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author edimar
 */
public class Results {

    protected Site site;

    public Results(Site site) {
        this.site = site;
    }

    /**
     *
     * @return Map<Rule, Map<Url,Value>>
     */
    public Map<String, Map<String, String>> loadAllRules(String outputPath) {
        Map<String, Map<String, String>> myResults = new HashMap<>();

        File dir = new File(outputPath + "/" + site.getPath() + "/extracted_values");
        for (File rule : dir.listFiles()) {
            myResults.put(rule.getName(), loadRule(rule));
        }
        return myResults;
    }

    public Map<String, String> loadRule(File rule) {
        Map<String, String> values = new HashMap<>();
        try (Reader in = new FileReader(rule.getAbsolutePath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    String url = Formatter.formatURL(record.get("URL"));
                    String value = Formatter.formatValue(record.get("EXTRACTED VALUE"));
                    if (!value.isEmpty()) {
                        values.put(url, value);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Results.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Results.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values;
    }

}
