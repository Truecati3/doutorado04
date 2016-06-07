/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.extract.timeout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author edimar
 */
public class RegularExpressionUtils {

    // demonstrates behavior for regular expression running into catastrophic backtracking for given input
    public static void main(String[] args) {
        try {
            Matcher matcher = createMatcherWithTimeout("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "(x+x+)+y", 20000);
            //Matcher matcher = createMatcherWithTimeout("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "x.*", 20000);
            System.out.println(matcher.matches());
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
        
        try {
           // Matcher matcher = createMatcherWithTimeout("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "(x+x+)+y", 20000);
            Matcher matcher = createMatcherWithTimeout("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "x.*", 20000);
            System.out.println(matcher.matches());
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static Matcher createMatcherWithTimeout(String stringToMatch, String regularExpression, int timeoutMillis) {
        System.out.println("Um");
        Pattern pattern = Pattern.compile(regularExpression);
        System.out.println("dois");
        return createMatcherWithTimeout(stringToMatch, pattern, timeoutMillis);
    }

    public static Matcher createMatcherWithTimeout(String stringToMatch, Pattern regularExpressionPattern, int timeoutMillis) {
        System.out.println("tres");
        CharSequence charSequence = new TimeoutRegexCharSequence(stringToMatch, timeoutMillis, "page01");
        System.out.println("quatro");
        return regularExpressionPattern.matcher(charSequence);
    }

}
