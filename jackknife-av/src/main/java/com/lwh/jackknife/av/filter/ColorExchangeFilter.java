package com.lwh.jackknife.av.filter;

public class ColorExchangeFilter implements VideoFilter {

    @Override
    public String getFilter() {
        return "hue=H=2*PI*t: s=sin(2*PI*t)+1";
    }
}
