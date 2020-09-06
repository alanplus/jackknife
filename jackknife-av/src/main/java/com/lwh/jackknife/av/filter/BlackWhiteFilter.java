package com.lwh.jackknife.av.filter;

public class BlackWhiteFilter implements VideoFilter {

    @Override
    public String getFilter() {
        return "lutyuv=u=128:v=128";
    }
}
