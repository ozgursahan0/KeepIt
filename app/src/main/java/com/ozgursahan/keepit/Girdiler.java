package com.ozgursahan.keepit;

import java.io.Serializable;

public class Girdiler extends Gruplar implements Serializable {
    private String bilgi;
    private double tutar;
    private String isim;
    private String soyisim;

    public Girdiler(String grupIsmi, String isim, String soyisim, String bilgi, double tutar) {
        super(grupIsmi,0);
        this.isim = isim;
        this.soyisim = soyisim;
        this.bilgi = bilgi;
        this.tutar = tutar;
    }

    public String getGrupIsmi() {
        return grupIsmi;
    }

    public void setGrupIsmi(String grupIsmi) {
        this.grupIsmi = grupIsmi;
    }

    public String getIsim() {
        return isim;
    }

    public String getSoyisim() {
        return soyisim;
    }

    public String getBilgi() { // bu kullanılacak mı?
        return bilgi;
    }

    public double getTutar() {
        return tutar;
    }
}
