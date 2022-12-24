package com.ozgursahan.keepit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KullaniciGirisActivity extends AppCompatActivity implements ForKullaniciGiris { //extends AppCompatActivity: bu sınıfın bir aktivite ekranına sahip olduğunu belirtiyor

    private String isim, soyisim, sifre, grupIsmi;
    private int kullaniciSayisi;

    private EditText nameText, surnameText, sifreText;
    private TextView currentGroupText;

    private ListView kullaniciListView;

    private ArrayList<Kullanicilar> kullanicilarArrayList;
    private ArrayList<Girdiler> girdilerArrayList;
    private ArrayList<Gruplar> gruplarArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // Bu metod içerisindekiler, aktivite açıldığında çalışır.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_giris);

        //görünümleriyle eşleştiriliyorlar
        nameText = findViewById(R.id.nameText);
        surnameText = findViewById(R.id.surnameText);
        sifreText = findViewById(R.id.sifreText);
        kullaniciListView = findViewById(R.id.kullaniciListView);
        currentGroupText = findViewById(R.id.currentGroupText);

        getData(); // ana menüden intent ile gelen veriler alınıyor.

        currentGroupText.setText(grupIsmi); // mevcut grup ismi, ekranın sol üstüne yazılıyor

        kullanicilariListele(); //kullanıcıları listeleme
    }

    public void kullaniciGirisi(View view) { // giriş butonu metodu
        isim =nameText.getText().toString().trim(); // trim(); girdinin baş ve sonundaki boşlukları siler
        soyisim =surnameText.getText().toString().trim();
        sifre=sifreText.getText().toString();

        boolean control=false;
        for(Kullanicilar k : kullanicilarArrayList)
        {
            //girilen girdilerle eşleşen kullanıcı varsa çalışacak if bloğu
            if(Objects.equals(k.getGrupIsmi(), grupIsmi) && Objects.equals(k.getIsim(), isim) && Objects.equals(k.getSoyisim(), soyisim) && Objects.equals(k.getSifre(), sifre))
            {
                control=true;
                Intent intent = new Intent(KullaniciGirisActivity.this,BorcAlacakActivity.class);
                //gidilen sınıfa gönderilecek ögeler intent içerisine konuyor
                intent.putExtra("kullanicilarArraylist",kullanicilarArrayList);
                intent.putExtra("girdilerArraylist",girdilerArrayList);
                intent.putExtra("grupIsmi",grupIsmi);
                intent.putExtra("kullaniciSayisi",kullaniciSayisi);
                intent.putExtra("isim", isim);
                intent.putExtra("soyisim", soyisim);
                finish();
                startActivity(intent);
            }

        }

        if(control==false)
        {
            Toast.makeText(getApplicationContext(),"Böyle bir kullanıcı bulunmamakta!",Toast.LENGTH_LONG).show();
        }
    }

    public void getData(){ // ana menüden intent ile gelen veriler alınıyor.
        Intent intent=getIntent(); // gelen Intent alınıyor
        //intent içerisinde bulunan veriler bu sınıfa tanımlanıyor
        grupIsmi=intent.getStringExtra("grupIsmi");
        kullaniciSayisi=intent.getIntExtra("kullaniciSayisi",0);
        kullanicilarArrayList = (ArrayList<Kullanicilar>) getIntent().getSerializableExtra("kullanicilarArraylist");
        girdilerArrayList = (ArrayList<Girdiler>) getIntent().getSerializableExtra("girdilerArraylist");
        gruplarArrayList = (ArrayList<Gruplar>) getIntent().getSerializableExtra("gruplarArraylist");
    }

    public void kullanicilariListele(){ //kullanıcıları listeleme
        List<String> stringListOfKullanicilar = new ArrayList<>(kullanicilarArrayList.size());
        for(Kullanicilar k : kullanicilarArrayList)
        {
            if(k.getGrupIsmi().equals(grupIsmi))
            {
                stringListOfKullanicilar.add(Objects.toString(k.getIsim()+" "+k.getSoyisim(),null));
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, stringListOfKullanicilar);
        kullaniciListView.setAdapter(arrayAdapter);
    }
}