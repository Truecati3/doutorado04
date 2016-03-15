/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.overlapped;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
public class Overlapped {

    private static final String[] header = {"SITE01", "SITE02", "OVERLAPS"};
    private boolean append = false;
    public static boolean DEBUG = false;
    private Domain domain;
    private Map<Site, Set<String>> entities = new HashMap<>();

    public Overlapped(Domain domain) {
        this.domain = domain;
    }

    public void print() {

        for (Site site : domain.getSites()) {
            entities.put(site, LoadSite.getEntities(site));
        }

        for (Site targetSite : entities.keySet()) {

            for (Site comparedSite : entities.keySet()) {
                if (targetSite == comparedSite) {
                    continue;
                }
                List<String> dataRecord = new ArrayList<>();
                dataRecord.add(targetSite.getFolderName());
                dataRecord.add(comparedSite.getFolderName());
                dataRecord.add(getNrOverlappedEntities(entities.get(targetSite), entities.get(comparedSite)) + "");
                print(dataRecord, append);
                append = true;
            }
        }

    }

    private int getNrOverlappedEntities(Set<String> site01, Set<String> site02) {
        Set<String> intersection = new HashSet<>();
        intersection.addAll(site01);
        intersection.retainAll(site02);
        return intersection.size();
    }

    private void print(List<String> dataRecord, boolean append) {
        /**
         * ********************** results ******************
         */
        File dir = new File(Paths.PATH_OVERLAP + "/" + domain.getDataset().getFolderName());
        dir.mkdirs();

        File file = new File(dir.getAbsolutePath() + "/" + domain.getFolderName() + ".csv");
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
            Logger.getLogger(Overlapped.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        Dataset dataset = Dataset.WEIR;

        for (Domain domain : dataset.getDomains()) {
            Overlapped overlapped = new Overlapped(domain);
            overlapped.print();
        }
    }
}
