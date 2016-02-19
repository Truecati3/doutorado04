/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.swde;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.ValueFormatter;
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
public class ResultsSWDE {

    private Site site;
    private ValueFormatter formatter = new ValueFormatterSwde();

    public ResultsSWDE(Site site) {
        this.site = site;
    }

    /**
     *
     * @param rule
     * @return Map<Url,Value>
     */
    public Map<Integer, String> loadRule(File rule) {
        Map<Integer, String> values = new HashMap<>();
        try (Reader in = new FileReader(rule.getAbsolutePath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    values.put(Integer.parseInt(record.get("URL").replaceAll(".*" + site.getPath() + "/", "").replaceAll(".htm", "")),
                            formatter.format(record.get("EXTRACTED VALUE")));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResultsSWDE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResultsSWDE.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values;
    }

    /**
     *
     * @return Map<Rule, Map<Url,Value>>
     */
    public Map<String, Map<Integer, String>> loadAllRules() {
        Map<String, Map<Integer, String>> myResults = new HashMap<>();

        File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values");
        System.out.println("DIR:" + dir.getAbsolutePath());
        for (File rule : dir.listFiles()) {
            myResults.put(rule.getName(), loadRule(rule));
        }
        return myResults;
    }
}
