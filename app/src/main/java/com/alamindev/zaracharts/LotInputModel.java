package com.alamindev.zaracharts;

public class LotInputModel {
    private String lotNo;
    private String shade;
    private double layQty;
    private String bundleRange;

    public LotInputModel(String lotNo, String shade, double layQty, String bundleRange) {
        this.lotNo = lotNo;
        this.shade = shade;
        this.layQty = layQty;
        this.bundleRange = bundleRange;
    }

    public String getLotNo() { return lotNo; }
    public void setLotNo(String lotNo) { this.lotNo = lotNo; }

    public String getShade() { return shade; }
    public void setShade(String shade) { this.shade = shade; }

    public double getLayQty() { return layQty; }
    public void setLayQty(double layQty) { this.layQty = layQty; }

    public String getBundleRange() { return bundleRange; }
    public void setBundleRange(String bundleRange) { this.bundleRange = bundleRange; }
}
