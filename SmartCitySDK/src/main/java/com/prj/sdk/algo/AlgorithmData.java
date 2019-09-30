package com.prj.sdk.algo;

public class AlgorithmData {

    private String key;
    private String dataMing;
    private String dataMi;

    @Override
    public String toString() {
        return "AlgorithmData{" +
                "key='" + key + '\'' +
                ", dataMing='" + dataMing + '\'' +
                ", dataMi='" + dataMi + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataMing() {
        return dataMing;
    }

    public void setDataMing(String dataMing) {
        this.dataMing = dataMing;
    }

    public String getDataMi() {
        return dataMi;
    }

    public void setDataMi(String dataMi) {
        this.dataMi = dataMi;
    }

}
