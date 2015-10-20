/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.generate;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.beans.AttributeInfo;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class GenerateRules {

    private static List<AttributeInfo> loadAttributeInfo(Site site) {
        List<AttributeInfo> attrsInfo = new ArrayList<>();
        try (Reader in = new FileReader(Paths.PATH_EXPRESSIVENESS + site.getPath() + "/attributes_info.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    attrsInfo.add(new AttributeInfo(record.get("ATTRIBUTE"), record.get("LABEL"), record.get("UNIQUE PATH LABEL"), record.get("UNIQUE PATH VALUE")));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateRules.class.getName()).log(Level.SEVERE, null, ex);
        }
        return attrsInfo;
    }

    /**
     *
     * @return the cypher rules === Map<Attribute,Rule>
     */
    public static Map<String, CypherRule> getRules(Site site) {
        Map<String, CypherRule> rules = new HashMap<>();
        List<AttributeInfo> attrsInfo = loadAttributeInfo(site);
        for (AttributeInfo attr : attrsInfo) {
            CypherNotation cypherNotation = new CypherNotation(attr.getLabel(), attr.getUniquePathLabel(), attr.getUniquePathValue());
            rules.put(attr.getAttribute(), cypherNotation.getNotation());
        }
        return rules;
    }

    public static void printRules(Site site) {
        Map<String, CypherRule> rules = getRules(site);

        try (Writer out = new FileWriter(Paths.PATH_EXPRESSIVENESS + site.getPath() + "/generated_rules.csv")) {
            String[] header = {"ATTRIBUTE", "RULE"};
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, CSVFormat.EXCEL.withHeader(header))) {
                for (String attr : rules.keySet()) {
                    List<String> dataRecord = new ArrayList<>();
                    dataRecord.add(attr);
                    dataRecord.add(rules.get(attr).getQueryWithoutParameters().replaceAll("\n", " "));
                    csvFilePrinter.printRecord(dataRecord);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GenerateRules.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        printRules(br.edimarmanica.dataset.weir.finance.Site.BIGCHARTS);
    }
}
