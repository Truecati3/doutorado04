/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.intrasitemapping.auto;

import br.edimarmanica.trinity.extract.Extract;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
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
public class MergeOffsets {

    private Site site;
    private boolean append = false;
    private MappingController mc;

    public MergeOffsets(Site site) {
        this.site = site;

    }

    public void execute() {
        mc = new MappingController(site);
        mc.execute();

        File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/offset");

        for (int indexOffset = 0; indexOffset < dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        }).length; indexOffset++) {
            executeOffset(indexOffset);
        }
    }

    private void executeOffset(int indexOffset) {
        File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/offset");
        try (Reader in = new FileReader(dir.getAbsoluteFile() + "/result_" + indexOffset + ".csv")) {
            List<List<String>> lines = new ArrayList<>();
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL)) {

                int indexRegistro = 0;
                for (CSVRecord record : parser) {
                    if (indexOffset != 0 && indexRegistro < Extract.NR_SHARED_PAGES) { //senão vai extrair repetido
                        indexRegistro++;
                        continue;
                    }
                    List<String> line = new ArrayList<>();
                    for (int nrRegra = 0; nrRegra < record.size(); nrRegra++) {
                        try {
                            line.add(Preprocessing.filter(record.get(nrRegra)));
                        } catch (InvalidValue ex) {
                            line.add("");
                        }
                    }
                    lines.add(line);
                    indexRegistro++;
                }

                print(indexOffset, lines);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MergeOffsets.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeOffsets.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void print(int indexOffset, List<List<String>> lines) {
        for (int indexRule = 1; indexRule < lines.get(0).size(); indexRule++) {
            int map = mc.getSpecificMap(indexOffset, indexRule);
            if (map == -1) { //não tem mapeamento
                continue;
            }

            File dir = new File(Paths.PATH_TRINITY + site.getPath() + "/extracted_values");
            dir.mkdirs();
            File file = new File(dir.getAbsolutePath() + "/rule_" + map + ".csv");

            CSVFormat format;
            if (append) {
                format = CSVFormat.EXCEL;
            } else {
                String[] header = {"URL", "EXTRACTED VALUE"};
                format = CSVFormat.EXCEL.withHeader(header);
            }

            try (Writer out = new FileWriter(file, append)) {
                try (CSVPrinter csvFilePrinter = new CSVPrinter(out, format)) {

                    for (int indexRegistro = 0; indexRegistro < lines.size(); indexRegistro++) {
                        String page = lines.get(indexRegistro).get(0);
                        String value = lines.get(indexRegistro).get(indexRule);

                        if (value.isEmpty()) {//não tem valor
                            continue;
                        }

                        List<String> dataRecord = new ArrayList<>();
                        dataRecord.add(page);
                        dataRecord.add(value);
                        csvFilePrinter.printRecord(dataRecord);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MergeOffsets.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        append = true;
    }

    public static void main(String[] args) {
        Domain domain = br.edimarmanica.dataset.weir.Domain.VIDEOGAME;
        for (Site site : domain.getSites()) {
            if (site == br.edimarmanica.dataset.weir.soccer.Site.FOOTBALL) {
                continue;
            }

            System.out.println("Site: " + site);
            MergeOffsets fo = new MergeOffsets(site);
            fo.execute();
        }
    }
}
