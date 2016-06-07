/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.extract.timeout;

/**
 *
 * @author edimar
 */
public class TimeoutRegexCharSequence implements CharSequence {

    private final CharSequence inner;
    private final int timeoutMillis;
    private final long timeoutTime;
    private final String page;

    public TimeoutRegexCharSequence(CharSequence inner, int timeoutMillis, String page) {
        super();
        this.inner = inner;
        this.timeoutMillis = timeoutMillis;
        this.page = page;
        timeoutTime = System.currentTimeMillis() + timeoutMillis;

    }

    @Override
    public char charAt(int index)  {
        if (System.currentTimeMillis() > timeoutTime) {
            throw new RuntimeException("Timeout occurred after " + timeoutMillis + "ms while processing page '" + page + "'!");
        }
        return inner.charAt(index);
    }

    @Override
    public int length() {
        return inner.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new TimeoutRegexCharSequence(inner.subSequence(start, end), timeoutMillis, page);
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
