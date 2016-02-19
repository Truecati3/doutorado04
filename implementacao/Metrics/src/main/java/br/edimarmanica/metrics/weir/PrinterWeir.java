package br.edimarmanica.metrics.weir;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author edimar
 */
public class PrinterWeir {

    private final String[] header = {"DATASET", "DOMAIN", "SITE", "ATTRIBUTE", "RULE", "LABEL", "RELEVANTS", "RETRIEVED",
        "RETRIEVED RELEVANTS", "RECALL", "PRECISION", "DATE"};
    private boolean append = false;
    
    private Site site;
    private String outputPath;

    public PrinterWeir(Site site, String outputPath) {
        this.site = site;
        this.outputPath = outputPath;
    }

    public void print(Attribute attribute, String rule, String label, Set<String> groundTruth, Set<String> ruleValues, double recall, double precision, int relevantRetrieved) {

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
            for (String rel : groundTruth) {
                if (!ruleValues.contains(rel)) {
                    String[] partes = rel.split(General.SEPARADOR);
                    bw.write("Faltando: [" + partes[1] + "] na página: " + partes[0]);
                    bw.newLine();
                }
            }
            bw.write("--------------------------------------------\n");
            bw.write("Irrelevantes recuperados (Problema de precision):\n");
            for (String rel : ruleValues) {
                if (!groundTruth.contains(rel)) {
                    String[] partes = rel.split(General.SEPARADOR);
                    bw.write("Faltando: [" + partes[1] + "] na página: " + partes[0]);
                    bw.newLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PrinterWeir.class.getName()).log(Level.SEVERE, null, ex);
        }


        append = true;
    }
}
