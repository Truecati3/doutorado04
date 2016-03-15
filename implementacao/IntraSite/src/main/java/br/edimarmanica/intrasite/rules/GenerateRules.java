/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.rules;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.CypherNotation;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
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
public class GenerateRules {

    private Site site;

    public GenerateRules(Site site) {
        this.site = site;
    }

    public Set<CypherRule> readRules() {
        Set<CypherRule> rules = new HashSet<>();
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/closest_template.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    CypherNotation cypherNotation = new CypherNotation(record.get("LABEL"), record.get("UP_LABEL"), record.get("UP_VALUE"));
                    rules.add(cypherNotation.getNotation());
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateRules.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rules;
    }
}
