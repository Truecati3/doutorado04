/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.intrasitemapping.manual;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Printer;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
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
public class AllMappings {

    private Site site;
    public static final String[] header = {"ATTRIBUTE", "OFFSET", "GROUP"};
    private boolean append = false;

    public AllMappings(Site site) {
        this.site = site;
    }

    public void execute() {
        File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/offset");

        for (File offset : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        })) {
            Mapping map = new Mapping(site, offset);
            Map<Attribute, Integer> maps = map.getBestGroups();
            for (Attribute attr : maps.keySet()) {
                List<String> dataRecord = new ArrayList<>();
                dataRecord.add(attr.getAttributeID());
                dataRecord.add(offset.getName());
                dataRecord.add(maps.get(attr).toString());
                print(dataRecord);
            }
        }
    }

    private void print(List<String> dataRecord) {
        /**
         * ********************** results ******************
         */
        File dir = new File(Paths.PATH_TRINITY + "/" + site.getPath());
        dir.mkdirs();

        File file = new File(dir.getAbsolutePath() + "/mappings.csv");
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
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        }

        append = true;
    }

    public static void main(String[] args) {
        for (Dataset dataset : Dataset.values()) {
            System.out.println("Dataset: " + dataset);
            for (Domain domain : dataset.getDomains()) {
                System.out.println("\tDomain: " + domain);
                for (Site site : domain.getSites()) {

                    if (site != br.edimarmanica.dataset.weir.book.Site.BOOKMOOCH) {
                        continue;
                    }
                    try {
                        System.out.println("\t\tSite: " + site);
                        AllMappings am = new AllMappings(site);
                        am.execute();
                    } catch (Exception ex) {
                        System.out.println("\t\t\tIgnorando");
                    }
                }
            }
        }
    }

}
