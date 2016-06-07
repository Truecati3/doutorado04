/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.extract;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import static br.edimarmanica.trinity.extract.Extract.VALUE_MAX_LENGHT;
import gnu.regexp.REException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tdg.cedar.utilities.UTF8FileUtil;

/**
 * Executa a expressão regular usando java.util.regex.Pattern;
 *
 * @author edimar
 */
public class ExtractPattern extends Extract {

    protected Pattern pattern;

    public ExtractPattern(Site site) {
        super(site);
    }

    @Override
    protected void compileRegex(String regex) {
        pattern = Pattern.compile(regex);
    }

    @Override
    protected void execute(File file, int offset) throws IOException {

        List<String> dataRecord = new ArrayList<>();
        Matcher m = getMatcher(file);
        dataRecord.add(file.getName());

        if (m.matches()) {
            for (int i = 0; i != m.groupCount(); i++) {
                if (m.group(i) == null) {
                    dataRecord.add("");
                } else {
                    dataRecord.add(format(m.group(i)));
                }
            }
        } else {
            for (int i = 0; i != m.groupCount(); i++) {
                dataRecord.add("not matched");
            }
        }
        printResults(dataRecord, offset);
    }
    
     protected Matcher getMatcher(File file) throws IOException {
        return pattern.matcher(UTF8FileUtil.readStrippedHTML(file.toURI()));
    }

     public static void main(String[] args) {
        //configurar essa opção (-Xss40m) da VM para não dar stackoverflow 
        General.DEBUG = true;
        Domain domain = br.edimarmanica.dataset.weir.Domain.BOOK;
        for (Site site : domain.getSites()) {

            if (site != br.edimarmanica.dataset.weir.book.Site.BOOKMOOCH) {
                continue;
            }
            System.out.println("Site: " + site);
            Extract run = new ExtractPattern(site);
            try {
                run.execute();
            } catch (IOException | REException ex1) {
                Logger.getLogger(Extract.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
}
