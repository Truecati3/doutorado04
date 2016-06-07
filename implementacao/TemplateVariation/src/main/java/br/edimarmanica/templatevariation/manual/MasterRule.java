/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.MergeResults;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class MasterRule {

    /** Pegando direto do intrasite **/
    public static String getMasterRule(Site site, Attribute attribute) throws SiteWithoutThisAttribute {

        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/result.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {

                for (CSVRecord record : parser) {
                    if (record.get("ATTRIBUTE").equals(attribute.getAttributeID())) {
                        if (record.get("RULE").equals("Attribute not found")){
                            throw new SiteWithoutThisAttribute(attribute.getAttributeID(), site.getFolderName());
                        }
                        return record.get("RULE");
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MergeResults.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeResults.class.getName()).log(Level.SEVERE, null, ex);
        }

        throw new SiteWithoutThisAttribute(attribute.getAttributeID(), site.getFolderName());
    }

    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.swde.book.Site.CHRISTIANBOOK;
        Attribute attribute = br.edimarmanica.dataset.swde.book.Attribute.TITLE;

        try {
            String result = MasterRule.getMasterRule(site, attribute);
            System.out.println("Result: " + result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(MasterRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
