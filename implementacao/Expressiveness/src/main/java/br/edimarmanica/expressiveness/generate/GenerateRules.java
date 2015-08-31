/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness.generate;

import br.edimarmanica.dataset.Configuration;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.Teste;
import br.edimarmanica.expressiveness.generate.beans.AttributeInfo;
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
        try (Reader in = new FileReader(Configuration.PATH_EXPRESSIVENESS + site.getPath() + "/attributes_info.csv")) {
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
    public static Map<String, String> getRules(Site site) {
        Map<String, String> rules = new HashMap<>();
        List<AttributeInfo> attrsInfo = loadAttributeInfo(site);
        for (AttributeInfo attr : attrsInfo) {
            rules.put(attr.getAttribute(), CypherNotation.getNotation(attr.getLabel(), attr.getUniquePathLabel(), attr.getUniquePathValue()));
        }
        return rules;
    }

    public static void printRules(Site site) {
        Map<String, String> rules = getRules(site);

        try (Writer out = new FileWriter(Configuration.PATH_EXPRESSIVENESS + site.getPath() + "/generated_rules.csv")) {
            String[] header = {"ATTRIBUTE", "RULE"};
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, CSVFormat.EXCEL.withHeader(header))) {
                for (String attr : rules.keySet()) {
                    List<String> studentDataRecord = new ArrayList<>();
                    studentDataRecord.add(attr);
                    studentDataRecord.add(rules.get(attr).replaceAll("\n", " "));
                    csvFilePrinter.printRecord(studentDataRecord);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Teste.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        printRules(br.edimarmanica.dataset.weir.book.Site.BOOKMOOCH);
    }
}
