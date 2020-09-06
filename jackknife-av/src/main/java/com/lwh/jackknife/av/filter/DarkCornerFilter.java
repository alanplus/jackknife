package com.lwh.jackknife.av.filter;

public class DarkCornerFilter implements VideoFilter {

    @Override
    public String getFilter() {
        return "vignette=PI/3";
    }
}
