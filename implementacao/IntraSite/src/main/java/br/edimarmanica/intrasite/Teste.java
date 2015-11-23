/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.expressiveness.generate.CypherNotation;
import java.io.File;

/**
 *
 * @author edimar
 */
public class Teste {
    public static void main(String[] args) {
        CypherNotation cypherNotation = new CypherNotation("# of Trades", "/HTML[1]/BODY[1]/TABLE[1]/TBODY[1]/TR[1]/TD[1]/TABLE[5]/TBODY[1]/TR[1]/TD[4]/TABLE[4]/TBODY[1]/TR[2]/TD[2]/TABLE[1]/TBODY[1]/TR[11]/TD[1]/FONT[1]/text()[1]", "/HTML[1]/BODY[1]/TABLE[1]/TBODY[1]/TR[1]/TD[1]/TABLE[5]/TBODY[1]/TR[1]/TD[4]/TABLE[4]/TBODY[1]/TR[2]/TD[2]/TABLE[1]/TBODY[1]/TR[10]/TD[1]/FONT[1]/text()[1]");
        
        System.out.println("Testando: "+cypherNotation.getNotation().getQueryWithoutParameters());
    }
}
