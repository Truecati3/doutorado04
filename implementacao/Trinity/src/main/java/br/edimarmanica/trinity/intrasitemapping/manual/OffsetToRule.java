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
import br.edimarmanica.trinity.util.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
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
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class OffsetToRule {

    public static final String[] header = {"URL", "EXTRACTED VALUE"};
    private Site site;
    private Map<String, Map<String, Integer>> mappings = new HashMap<>(); //<Offset,<Attribute, Group>>
    private boolean append = false;
    private Set<String> pages = new HashSet<>();

    public OffsetToRule(Site site) {
        this.site = site;
    }

    private void readMappings() {
        try (Reader in = new FileReader(Paths.PATH_TRINITY + site.getPath() + "/mappings.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {

                    if (mappings.containsKey(record.get("OFFSET"))) {
                        mappings.get(record.get("OFFSET")).put(record.get("ATTRIBUTE"), Integer.parseInt(record.get("GROUP")));
                    } else {
                        Map<String, Integer> map = new HashMap<>();
                        map.put(record.get("ATTRIBUTE"), Integer.parseInt(record.get("GROUP")));
                        mappings.put(record.get("OFFSET"), map);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OffsetToRule.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OffsetToRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void execute() {
        readMappings();

        File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/offset");

        for (File offset : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        })) {

            try (Reader in = new FileReader(offset)) {
                try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL)) {
                    for (CSVRecord record : parser) {
                        String page = record.get(0);
                        if (pages.contains(page)) {
                            continue;
                        } else {
                            pages.add(page);
                        }

                        List<String> dataRecord = new ArrayList<>();
                        for (Attribute attr : site.getDomain().getAttributes()) {
                            try {
                                int group = mappings.get(offset.getName()).get(attr.getAttributeID());

                                if (group != -1) {
                                    dataRecord.add(record.get(group));
                                } else {
                                    dataRecord.add("");
                                }
                            } catch (Exception ex) {
                                dataRecord.add("");
                            }
                        }
                        print(page, dataRecord);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Mapping.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Mapping.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    private void print(String page, List<String> values) {
        File dir = new File(Paths.PATH_TRINITY + "/" + site.getPath() + "/extracted_values/");

        if (!append) {
            FileUtils.deleteDir(dir);
            dir.mkdirs();
        }

        for (int ruleID = 0; ruleID < values.size(); ruleID++) {

            File file = new File(dir.getAbsolutePath() + "/rule_" + ruleID + ".csv");
            CSVFormat format;
            if (append) {
                format = CSVFormat.EXCEL;
            } else {
                format = CSVFormat.EXCEL.withHeader(header);
            }

            try (Writer out = new FileWriter(file, append)) {
                try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {
                    List<String> dataRecord = new ArrayList<>();
                    dataRecord.add(page);
                    dataRecord.add(values.get(ruleID));
                    csvFilePrinter.printRecord(dataRecord);
                }
            } catch (IOException ex) {
                Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                        OffsetToRule am = new OffsetToRule(site);
                        am.execute();
                    } catch (Exception ex) {
                        System.out.println("\t\t\tIgnorando");
                    }

                }
            }
        }
    }
}
