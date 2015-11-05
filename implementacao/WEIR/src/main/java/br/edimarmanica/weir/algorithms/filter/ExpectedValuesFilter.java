/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.filter;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.check.WeakRemovalCheck;
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
public class ExpectedValuesFilter extends RulesFilter {

    public ExpectedValuesFilter(Site site) {
        super(site);
    }

    @Override
    public Set<Rule> execute(Set<Rule> rules) {
        Set<Integer> expectedRuleIds = new HashSet<>();
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + getSite().getDomain().getPath() + "/result.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    if (record.get("SITE").endsWith(getSite().getFolderName())) {
                        try {
                            expectedRuleIds.add(new Integer(record.get("RULE").replace("rule_", "").replace(".csv", "")));
                        } catch (NumberFormatException ex) {
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WeakRemovalCheck.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WeakRemovalCheck.class.getName()).log(Level.SEVERE, null, ex);
        }

        Set<Rule> filteredRules = new HashSet<>();
        for (Rule r : rules) {
            if (expectedRuleIds.contains(r.getRuleID())) {
                filteredRules.add(r);
            }
        }
        return filteredRules;
    }
}
