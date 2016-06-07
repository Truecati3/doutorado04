/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.extract;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tdg.cedar.utilities.UTF8FileUtil;

/**
 * Executa a expressão regular usando gnu.regexp.RE;
 *
 * @author edimar
 */
public class ExtractGnuRegex extends Extract {

    protected RE reRegex;

    public ExtractGnuRegex(Site site) {
        super(site);
    }

    @Override
    protected void compileRegex(String regex) {
        try {
            reRegex = new RE(regex);
        } catch (REException ex) {
            Logger.getLogger(ExtractGnuRegex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void execute(File file, int offset) throws IOException {

        List<String> dataRecord = new ArrayList<>();
        REMatch match = getMatcher(file);
        dataRecord.add(file.getName());

        if (match != null) {
            for (int i = 1; i <= reRegex.getNumSubs(); i++) {

                if (match.getStartIndex(i) > -1 && i < match.getEndIndex()) {
                    try {
                        dataRecord.add(format(match.toString(i)));
                    } catch (StringIndexOutOfBoundsException ex) {
                        dataRecord.add("Indexout");
                    }
                } else {
                    dataRecord.add("");
                }
            }
        } else {
            for (int i = 1; i <= reRegex.getNumSubs(); i++) {
                dataRecord.add("not matched");
            }
        }
        printResults(dataRecord, offset);
    }

    protected REMatch getMatcher(File file) throws IOException {
        return reRegex.getMatch(UTF8FileUtil.readStrippedHTML(file.toURI()));
    }

    public static void main(String[] args) {
        //configurar essa opção (-Xss40m) da VM para não dar stackoverflow 
        General.DEBUG = true;
        Extract.WINDOW_SIZE=100;
        Domain domain = br.edimarmanica.dataset.swde.Domain.JOB;
        for (Site site : domain.getSites()) {

            if (site != br.edimarmanica.dataset.swde.job.Site.CAREERBUILDER) {
                continue;
            }
            System.out.println("Site: " + site);
            Extract run = new ExtractGnuRegex(site);
            try {
                run.execute();
            } catch (IOException | REException ex1) {
                Logger.getLogger(Extract.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
}
