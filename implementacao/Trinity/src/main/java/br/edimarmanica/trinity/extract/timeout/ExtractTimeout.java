package br.edimarmanica.trinity.extract.timeout;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import gnu.regexp.REException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import tdg.cedar.utilities.UTF8FileUtil;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Define um timeout para extração em cada página
 *
 * @author edimar
 */
public class ExtractTimeout extends br.edimarmanica.trinity.extract.ExtractPattern {

    private static final int TIME_OUT_MILLIS = 60 * 1000; //em milisegundos

    public ExtractTimeout(Site site) {
        super(site);
    }

    @Override
    public Matcher getMatcher(File file) throws IOException {
        CharSequence charSequence = new TimeoutRegexCharSequence(UTF8FileUtil.readStrippedHTML(file.toURI()), TIME_OUT_MILLIS, file.getName());
        Matcher m = pattern.matcher(charSequence);
        return m;
    }

    @Override
    protected void execute(File file, int offset) throws IOException {
        try {
            super.execute(file, offset);
        } catch (RuntimeException ex) {
            System.out.println("Timeout to page: " + file.getName());
        }
    }

    public static void main(String[] args) {
        //configurar essa opção (-Xss40m) da VM para não dar stackoverflow 
        General.DEBUG = true;

        Domain domain = br.edimarmanica.dataset.swde.Domain.NBA_PLAYER;
        for (Site site : domain.getSites()) {

            if (site != br.edimarmanica.dataset.swde.nba.Site.YAHOO) {
                continue;
            }

            System.out.println("Site: " + site);
            ExtractTimeout run = new ExtractTimeout(site);
            try {
                run.execute();
            } catch (IOException | REException ex1) {
                Logger.getLogger(ExtractTimeout.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

}
