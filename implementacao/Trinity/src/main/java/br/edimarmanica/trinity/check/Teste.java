/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.check;
import com.wcohen.ss.Jaccard;
import com.wcohen.ss.api.Token;
import com.wcohen.ss.tokens.NGramTokenizer;
import com.wcohen.ss.tokens.SimpleTokenizer;

/**
 *
 * @author edimar
 */
public class Teste {

    public static void main(String[] args) {
        NGramTokenizer tokenizer = new NGramTokenizer(3, 3, false, SimpleTokenizer.DEFAULT_TOKENIZER);
        int n = 0;

        Token[] tokens = tokenizer.tokenize("testeedimar");
        for (Token token : tokens) {
            System.out.println("token " + (++n) + ":"
                    + " id=" + token.getIndex() + " value: '" + token.getValue() + "'");
        }

        Jaccard sim = new Jaccard(tokenizer);
        System.out.println(sim.score("", "City1920Highway2830"));
    }
}
