/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.swde;

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

/**
 *
 * @author edimar
 */
public class PrinterSwde extends Printer {

    private boolean append = false;

    public PrinterSwde(Site site, String outputPath) {
        super(site, outputPath);
    }

    public void print(Attribute attribute, String rule, String label, Map<Integer, String> groundTruth, Map<Integer, String> ruleValues, Set<Integer> intersection, double recall, double precision, int relevantRetrieved) {
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
        for (Integer rel : groundTruth.keySet()) {
            if (!intersection.contains(rel)) {
                relevantNotRetrieved.put(rel + "", groundTruth.get(rel));
            }
        }

        Map<String, String> irrelevantRetrieved = new HashMap<>();
        for (Integer ret : ruleValues.keySet()) {
            if (!intersection.contains(ret)) {
                irrelevantRetrieved.put(ret+"", ruleValues.get(ret));
            }
        }
        
        printLog(dataRecord, relevantNotRetrieved, irrelevantRetrieved, append);
        
        append = true;
    }
}
