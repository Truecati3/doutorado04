/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;



/**
 *
 * @author edimar
 */
public class Teste {

    public static void main(String[] args) {
        Domain domain = br.edimarmanica.dataset.weir.Domain.VIDEOGAME;
        for (Site site : domain.getSites()) {
            System.out.println(site);
        }
    }
}
