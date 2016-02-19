/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.weir;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.ValueFormatter;
import java.io.File;
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
public class ResultsWeir {

    private Site site;
    private ValueFormatter formatter = new ValueFormatterWeir();

    public ResultsWeir(Site site) {
        this.site = site;
    }

    public Set<String> loadRule(File rule) {
        Set<String> values = new HashSet<>();
        try (Reader in = new FileReader(rule.getAbsolutePath())) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    values.add(record.get("URL").replaceAll(".*" + site.getDomain().getDataset().getFolderName() + "/", "") + 
                            General.SEPARADOR + formatter.format(record.get("EXTRACTED VALUE")));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResultsWeir.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResultsWeir.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values;
    }

    /**
     * 
     * @return <RuleName,Set<ExtractedValues>>
     */
    public Map<String, Set<String>> loadAllRules() {
        Map<String, Set<String>> myResults = new HashMap<>();//<RuleName,Set<ExtractedValues>>
        File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values");
        for (File rule : dir.listFiles()) {
            myResults.put(rule.getName(), loadRule(rule));
        }
        return myResults;
    }
}
