package com.alamindev.zaracharts;

public class ChartModel {
    private String cutNo;
    private String colour;
    private double ratio;
    private double totalPcs;

    public ChartModel(String cutNo, String colour, double ratio, double totalPcs) {
        this.cutNo = cutNo;
        this.colour = colour;
        this.ratio = ratio;
        this.totalPcs = totalPcs;
    }

    public String getCutNo() { return cutNo; }
    public String getColour() { return colour; }
    public double getRatio() { return ratio; }
    public double getTotalPcs() { return totalPcs; }
}