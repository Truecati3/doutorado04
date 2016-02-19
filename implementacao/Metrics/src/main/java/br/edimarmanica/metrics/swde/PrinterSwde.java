/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.swde;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.weir.PrinterWeir;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 *
 * @author edimar
 */
public class PrinterSwde {

    private final String[] header = {"DATASET", "DOMAIN", "SITE", "ATTRIBUTE", "RULE", "LABEL", "RELEVANTS", "RETRIEVED",
        "RETRIEVED RELEVANTS", "RECALL", "PRECISION", "DATE"};
    private boolean append = false;
    private Site site;
    private String outputPath;

    public PrinterSwde(Site site, String outputPath) {
        this.site = site;
        this.outputPath = outputPath;
    }

    public void print(Attribute attribute, String rule, String label, Map<Integer, String> groundTruth, Map<Integer, String> ruleValues, Set<Integer> intersection, double recall, double precision, int relevantRetrieved) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(date);

        /**
         * ********************** results ******************
         */
        File file = new File(outputPath + "/" + site.getPath() + "/result.csv");
        CSVFormat format;
        if (append) {
            format = CSVFormat.EXCEL;
        } else {
            format = CSVFormat.EXCEL.withHeader(header);
        }

        try (Writer out = new FileWriter(file, append)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                List<String> studentDataRecord = new ArrayList<>();
                studentDataRecord.add(site.getDomain().getDataset().getFolderName());
                studentDataRecord.add(site.getDomain().getFolderName());
                studentDataRecord.add(site.getFolderName());
                studentDataRecord.add(attribute.getAttributeID());
                studentDataRecord.add(rule);
                studentDataRecord.add(label);
                studentDataRecord.add(groundTruth.size() + "");
                studentDataRecord.add(ruleValues.size() + "");
                studentDataRecord.add(relevantRetrieved + "");
                studentDataRecord.add(recall + "");
                studentDataRecord.add(precision + "");
                studentDataRecord.add(formattedDate);
                csvFilePrinter.printRecord(studentDataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(PrinterWeir.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * ********************** log ******************
         */
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "/" + site.getPath() + "/log.txt", append))) {
            bw.write("\n\n*************" + formattedDate + " - " + attribute.getAttributeID() + "********************************\n");
            bw.write("dataset;" + site.getDomain().getDataset().getFolderName() + ";domain;" + site.getDomain().getFolderName()
                    + ";site;" + site.getFolderName() + ";attribute;"
                    + attribute.getAttributeID() + ";relevantes;"
                    + groundTruth.size() + ";recuperados;" + ruleValues.size() + ";relevantes recuperados; " + relevantRetrieved + ";recall;" + recall + ";precision;" + precision + ";date;" + formattedDate);
            bw.newLine();
            bw.write("--------------------------------------------\n");
            bw.write("Relevantes não recuperados (Problema de recall):\n");
            for (Integer rel : groundTruth.keySet()) {
                if (!intersection.contains(rel)) {
                    bw.write("Faltando: [" + groundTruth.get(rel) + "] na página: " + rel);
                    bw.newLine();
                }
            }
            bw.write("--------------------------------------------\n");
            bw.write("Irrelevantes recuperados (Problema de precision):\n");
            for (Integer ret : ruleValues.keySet()) {
                if (!intersection.contains(ret)) {
                    bw.write("Faltando: [" + ruleValues.get(ret) + "] na página: " + ret);
                    bw.newLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PrinterWeir.class.getName()).log(Level.SEVERE, null, ex);
        }
        append = true;
    }
}
