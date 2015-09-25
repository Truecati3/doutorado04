/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.dataset.Configuration;
import br.edimarmanica.dataset.Site;
import java.io.File;

/**
 *
 * @author edimar
 */
public class Teste {
    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.weir.book.Site.BOOKSANDEBOOKS;
         File dir = new File(Configuration.PATH_INTRASITE + "/" + site.getPath());
        if (!dir.exists()) {
            System.out.println(dir.mkdirs());
        }
    }
            
}
