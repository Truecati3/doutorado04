package br.edimarmanica.metrics.weir;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Printer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author edimar
 */
public class PrinterWeir extends Printer {

    private boolean append = false;

    public PrinterWeir(Site site, String outputPath) {
        super(site, outputPath);
    }

    public void print(Attribute attribute, String rule, String label, Set<String> groundTruth, Set<String> ruleValues, double recall, double precision, int relevantRetrieved) {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(date);


        List<String> dataRecord = new ArrayList<>();
        dataRecord.add(site.getDomain().getDataset().getFolderName());
        dataRecord.add(site.getDomain().getFolderName());
        dataRecord.add(site.getFolderName());
        dataRecord.add(attribute.getAttributeID());
        dataRecord.add(rule);
        dataRecord.add(label);
        dataRecord.add(groundTruth.size() + "");
        dataRecord.add(ruleValues.size() + "");
        dataRecord.add(relevantRetrieved + "");
        dataRecord.add(recall + "");
        dataRecord.add(precision + "");
        dataRecord.add(formattedDate);
        printResults(dataRecord, append);

        /**
         * ********************** log ******************
         */
        Map<String, String> relevantNotRetrieved = new HashMap<>();
        for (String rel : groundTruth) {
            if (!ruleValues.contains(rel)) {
                String[] partes = rel.split(General.SEPARADOR);
                relevantNotRetrieved.put(partes[0], partes[1]);
            }
        }

        Map<String, String> irrelevantRetrieved = new HashMap<>();
        for (String rel : ruleValues) {
            if (!groundTruth.contains(rel)) {
                String[] partes = rel.split(General.SEPARADOR);
                irrelevantRetrieved.put(partes[0], partes[1]);
            }
        }
        printLog(dataRecord, relevantNotRetrieved, irrelevantRetrieved, append);

        append = true;
    }
}
