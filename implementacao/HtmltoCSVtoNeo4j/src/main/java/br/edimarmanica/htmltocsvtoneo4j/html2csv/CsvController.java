/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.htmltocsvtoneo4j.html2csv;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.htmltocsvtoneo4j.csv2neo4j.RelTypes;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 *
 * @author edimar
 */
public class CsvController {

    private Site site;
    private boolean appendNodes = false;
    private boolean appendRel = false;
    private final String[] HEADER_NODES = {"VALUE", "NODE_TYPE", "PATH", "UNIQUE_PATH", "URL", "POSITION"};
    private final String[] HEADER_REL = {"start", "end", "type"};
    private final static String NODE_FILE_NAME = "nodes.csv";
    private final static String REL_FILE_NAME = "rels.csv";

    public CsvController(Site site, boolean append) {
        this.site = site;
        this.appendNodes = append;
        this.appendRel = append;
    }

    public void addNode(Map<String, String> properties) {

        File dir = new File(Paths.PARTIAL_CSV_PATH + "/" + site.getPath() );
        dir.mkdirs();
        CSVFormat format;
        if (appendNodes) {
            format = CSVFormat.TDF;
        } else {
            format = CSVFormat.TDF.withHeader(HEADER_NODES);
        }

        try (Writer out = new FileWriter(dir+ "/" + NODE_FILE_NAME, appendNodes)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                List<String> dataRecord = new ArrayList<>();
                for (String attributeName : HEADER_NODES) {
                    dataRecord.add(properties.get(attributeName));
                }
                csvFilePrinter.printRecord(dataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(CsvController.class.getName()).log(Level.SEVERE, null, ex);
        }

        appendNodes = true;
    }

    public void addRelationShips(Map<Long, Long> relationships) {

        File file = new File(Paths.PARTIAL_CSV_PATH + "/" + site.getPath() + "/" + REL_FILE_NAME);
        
        CSVFormat format;
        if (appendRel) {
            format = CSVFormat.TDF;
        } else {
            format = CSVFormat.TDF.withHeader(HEADER_REL);
        }

        try (Writer out = new FileWriter(file, appendRel)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {

                for (Long child : relationships.keySet()) {
                    List<String> dataRecord = new ArrayList<>();
                    dataRecord.add(relationships.get(child)+"");
                    dataRecord.add(child+"");
                    dataRecord.add(RelTypes.has_child.name());
                    csvFilePrinter.printRecord(dataRecord);
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(CsvController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
