package com.lwh.jackknife.av.filter;

public class PhotographicPlateFilter implements VideoFilter {

    @Override
    public String getFilter() {
        return "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";
    }
}
