/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.weir.PrinterWeir;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
public abstract class Printer {

    public static final String[] header = {"DATASET", "DOMAIN", "SITE", "ATTRIBUTE", "RULE", "LABEL", "RELEVANTS", "RETRIEVED",
        "RETRIEVED RELEVANTS", "RECALL", "PRECISION", "DATE"};
    protected Site site;
    protected String outputPath;

    public Printer(Site site, String outputPath) {
        this.site = site;
        this.outputPath = outputPath;
    }

    /**
     * 
     * @param dataRecord "DATASET", "DOMAIN", "SITE", "ATTRIBUTE", "RULE", "LABEL", "RELEVANTS", "RETRIEVED",
        "RETRIEVED RELEVANTS", "RECALL", "PRECISION", "DATE"
     * @param append 
     */
    protected void printResults(List<String> dataRecord, boolean append) {
        /**
         * ********************** results ******************
         */
        File dir = new File(outputPath + "/" + site.getPath());
        dir.mkdirs();
        
        File file = new File(dir.getAbsolutePath() + "/result.csv");
        CSVFormat format;
        if (append) {
            format = CSVFormat.EXCEL;
        } else {
            format = CSVFormat.EXCEL.withHeader(header);
        }

        try (Writer out = new FileWriter(file, append)) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                csvFilePrinter.printRecord(dataRecord);
            }
        } catch (IOException ex) {
            Logger.getLogger(PrinterWeir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    protected void printLog(List<String> dataRecord, Map<String,String> relevantNotRetrieved, Map<String,String> irrelevantRetrieved, boolean append){
         try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "/" + site.getPath() + "/log.txt", append))) {
            bw.write("\n\n*************" + dataRecord.get(11) + " - " + dataRecord.get(3) + "********************************\n");
            bw.write("dataset;" + dataRecord.get(0) + ";domain;" + dataRecord.get(1)
                    + ";site;" + dataRecord.get(2) + ";attribute;"
                    + dataRecord.get(3) + ";relevantes;"
                    + dataRecord.get(6) + ";recuperados;" + dataRecord.get(7) + ";relevantes recuperados; " + dataRecord.get(8) +
                    ";recall;" + dataRecord.get(9) + ";precision;" + dataRecord.get(10) + ";date;" + dataRecord.get(11));
            bw.newLine();
            bw.write("--------------------------------------------\n");
            bw.write("Relevantes não recuperados (Problema de recall):\n");
            for (String rel : relevantNotRetrieved.keySet()) {
                    bw.write("Faltando: [" + relevantNotRetrieved.get(rel) + "] na página: " + rel);
                    bw.newLine();
            }
            bw.write("--------------------------------------------\n");
            bw.write("Irrelevantes recuperados (Problema de precision):\n");
            for (String rel : irrelevantRetrieved.keySet()) {
                    bw.write("Faltando: [" + irrelevantRetrieved.get(rel) + "] na página: " + rel);
                    bw.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(PrinterWeir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
