/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.overlapped;

import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class Overlapped {

    public static boolean DEBUG = false;
    private Domain domain;
    private Map<Site, Set<String>> entities = new HashMap<>();

    public Overlapped(Domain domain) {
        this.domain = domain;
    }

    public void print() {
        for (Site site : domain.getSites()) {
            entities.put(site, LoadSite.getEntities(site));
        }

        for (Site targetSite : entities.keySet()) {
            System.out.println("Site: " + targetSite.getFolderName());
            for (Site comparedSite : entities.keySet()) {
                if (targetSite == comparedSite) {
                    continue;
                }

                System.out.println("\t->" + comparedSite.getFolderName() + ": " + getNrOverlappedEntities(entities.get(targetSite), entities.get(comparedSite)));
            }
        }

    }

    private int getNrOverlappedEntities(Set<String> site01, Set<String> site02) {
        Set<String> intersection = new HashSet<>();
        intersection.addAll(site01);
        intersection.retainAll(site02);
        return intersection.size();
    }
    
    public static void main(String[] args) {
        Overlapped overlapped = new Overlapped(br.edimarmanica.dataset.weir.Domain.SOCCER);
        overlapped.print();
    }
}
