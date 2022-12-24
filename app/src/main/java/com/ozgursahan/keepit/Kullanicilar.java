package com.ozgursahan.keepit;

import java.io.Serializable;

public class Kullanicilar extends Gruplar implements Serializable {
    private String isim;
    private String soyisim;
    private double para;
    private String sifre;

    public Kullanicilar(String grupIsmi, String isim, String soyisim, String sifre, double para){
        super(grupIsmi,0);
        this.sifre = sifre;
        this.isim = isim;
        this.soyisim = soyisim;
        this.para = para;
    }

    public String getSifre() {
        return sifre;
    }

    public String getGrupIsmi() {
        return grupIsmi;
    }

    public void setGrupIsmi(String grupIsmi) {
        this.grupIsmi = grupIsmi;
    }

    public double getPara() {
        return para;
    }

    public void setPara(double para) {
        this.para = para;
    }

    public String getIsim() {
        return isim;
    }

    public String getSoyisim() {
        return soyisim;
    }
}
