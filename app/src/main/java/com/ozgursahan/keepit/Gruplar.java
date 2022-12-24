package com.ozgursahan.keepit;

import java.io.Serializable;

public class Gruplar implements Serializable {
    protected String grupIsmi;
    protected int kullaniciSayisi;

    public Gruplar(String grupIsmi, int kullaniciSayisi) {
        this.grupIsmi = grupIsmi;
        this.kullaniciSayisi = kullaniciSayisi;
    }
}
