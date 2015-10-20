/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.runner;

import br.edimarmanica.dataset.Site;

/**
 *
 * @author edimar
 */
public class Intrasite {

    public void execute(Site site) {
        br.edimarmanica.intrasite.Intrasite intrasite = new br.edimarmanica.intrasite.Intrasite();
        intrasite.execute(site);
    }
}
