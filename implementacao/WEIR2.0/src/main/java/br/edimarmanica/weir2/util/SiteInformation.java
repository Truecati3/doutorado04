/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.util;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import java.io.File;

/**
 *
 * @author edimar
 */
public class SiteInformation {
    
    public static int nrPages(Site site){
        File dir = new File(Paths.PATH_BASE+"/"+site.getPath());
        return dir.listFiles().length;
    }
}
