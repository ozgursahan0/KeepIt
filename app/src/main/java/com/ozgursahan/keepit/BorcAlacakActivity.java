package com.ozgursahan.keepit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BorcAlacakActivity extends AppCompatActivity implements ForBorcAlacak { //extends AppCompatActivity: bu sınıfın bir aktivite ekranına sahip olduğunu belirtiyor

    private ArrayList<Kullanicilar> kullanicilarArrayList;
    private ArrayList<Girdiler> girdilerArrayList;

    private EditText infoText,amountText;
    private TextView currentUserText;
    private ListView hesapListView, girdilerListView;

    private List<String> stringListOfInfos;
    private List<String> hesaplar;
    private ArrayAdapter<String> hesaplarArrayAdapter;

    private Girdiler yeniGirdi;

    private String info, isim, soyisim, grupIsmi;
    private double amount, hesap;
    private int kullaniciSayisi;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Bu metod içerisindekiler, aktivite açıldığında çalışır.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borc_alacak);

        sharedPreferences = getSharedPreferences("com.ozgursahan.keepit",MODE_PRIVATE); // paketten sharedPreferences verileri alınıyor
        editor = sharedPreferences.edit();

        //görünümleriyle eşleştiriliyorlar
        infoText=findViewById(R.id.infoText);
        amountText=findViewById(R.id.amountText);
        currentUserText=findViewById(R.id.currentUserText);
        girdilerListView=findViewById(R.id.girdilerListView);
        hesapListView=findViewById(R.id.hesapListView);

        getData(); // KullaniciGirisActivity'den intent ile gelen veriler alınıyorprivate

        hesaplar=new ArrayList<>(); // borç-alacak hesabını ekranda gösterecek listview arraylisti

        currentUserText.setText(grupIsmi+" -> "+ isim +" "+ soyisim); // giriş yapan kullanıcı ismi ekranın sol üstüne yazılır

        girdileriListele(); //girdileri ekrana listeleyen metod

        hesapla(); //HESAP İŞLEMİ
    }

    public void addInfo(View view) { // Ekle butonu metodu

        if(!infoText.getText().toString().equals("") && !amountText.getText().toString().equals("") && !amountText.getText().toString().equals("."))
        {
            hesaplarArrayAdapter.clear(); //eski hesabın içi boşaltılıyor, aşağıda ekleme zaten yapılacak
            info=infoText.getText().toString().trim();
            amount=Double.parseDouble(amountText.getText().toString());

            yeniGirdi = new Girdiler(grupIsmi, isim, soyisim,info,amount); // yeni girdi objesi oluşturuluyor
            girdilerArrayList.add(yeniGirdi); // girdi listeye ekleniyor
            infoText.setText(""); // eklemeden sonra edittext içi boşaltılıyor
            amountText.setText("");

            for(Kullanicilar k : kullanicilarArrayList)
            {
                if(k.getGrupIsmi().equals(grupIsmi) && k.getIsim().equals(isim) && k.getSoyisim().equals(soyisim)) // giriş yapan kullanıcı çekiliyor
                {
                    k.setPara(k.getPara()+amount); // girdiyi ekleyen kullanıcının hanesine girdiği tutar ekleniyor
                }
            }

            hesapla(); //HESAP İŞLEMİ

            girdileriListele(); //girdileri ekrana listeleyen metod

            //güncellenmiş arraylistler, ana menüden alınabilsin diye sharedPreferencese yükleniyor
            editor.putString("girdilerArraylist", new Gson().toJson(girdilerArrayList)).apply();
            editor.putString("kullanicilarArraylist", new Gson().toJson(kullanicilarArrayList)).apply();

        }

        else
        {
            Toast.makeText(getApplicationContext(),"Kutucuklar boş bırakılamaz!",Toast.LENGTH_LONG).show();
        }

    }

    public void anamenu(View view){ // ana menü butonu metodu
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void getData(){ // KullaniciGirisActivity'den intent ile gelen veriler alınıyor
        Intent intent = getIntent(); // gelen Intent alınıyor
        //intent içerisinde bulunan veriler bu sınıfa tanımlanıyor
        grupIsmi=intent.getStringExtra("grupIsmi");
        kullaniciSayisi=intent.getIntExtra("kullaniciSayisi",0);
        isim =intent.getStringExtra("isim");
        soyisim =intent.getStringExtra("soyisim");
        kullanicilarArrayList = (ArrayList<Kullanicilar>) getIntent().getSerializableExtra("kullanicilarArraylist");
        girdilerArrayList = (ArrayList<Girdiler>) getIntent().getSerializableExtra("girdilerArraylist");
    }

    public void girdileriListele(){ //girdileri ekrana listeleyen metod
        stringListOfInfos = new ArrayList<>(girdilerArrayList.size());
        for (Girdiler g : girdilerArrayList)
        {
            if(g.getGrupIsmi().equals(grupIsmi))
            {
                stringListOfInfos.add(Objects.toString(g.getIsim()+" "+g.getSoyisim()+" "+g.getBilgi()+" "+g.getTutar(),null));
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringListOfInfos);
        girdilerListView.setAdapter(arrayAdapter);
    }

    public void hesapla(){ //HESAP İŞLEMİ
        for(Kullanicilar k : kullanicilarArrayList) // k giriş yapan, l diğer kullanıcılar
        {
            if(k.getGrupIsmi().equals(grupIsmi) && k.getIsim().equals(isim) && k.getSoyisim().equals(soyisim)) // giriş yapan kullanıcının arraylist'ten çekimi
            {
                for(Kullanicilar l : kullanicilarArrayList)
                {
                    if(l.getGrupIsmi().equals(k.getGrupIsmi()) && !l.getIsim().equals(k.getIsim()) && !l.getSoyisim().equals(k.getSoyisim())) // giriş yapan kullanıcı ile aynı grupta
                    {                                                                                                                         // bulunan ancak farklı kullanıcının alımı
                        if(l.getPara() > k.getPara()) // giriş yapan kullanıcın hanesi diğer bir kullanıcının hanesinden az ise
                        {
                            hesap=(l.getPara()-k.getPara())/kullaniciSayisi;
                            hesaplar.add(l.getIsim()+" "+l.getSoyisim()+"'e "+hesap+" TL borcunuz var.");
                        }

                        else if(l.getPara() < k.getPara()) // giriş yapan kullanıcın hanesi diğer bir kullanıcının hanesinden çok ise
                        {
                            hesap=(k.getPara()-l.getPara())/kullaniciSayisi;
                            hesaplar.add(l.getIsim()+" "+l.getSoyisim()+"'den "+hesap+" TL alacağınız var.");
                        }

                        //borç-alacak hesabını gösteren listview
                        hesaplarArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hesaplar);
                        hesapListView.setAdapter(hesaplarArrayAdapter);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() { // Back tuşu basılsın istemiyoruz. Kullanıcı 'ANA MENU' butonuna tıklamalı
        //super.onBackPressed(); << bu metod çağırılmayarak 'BACK' tuşu bloklanıyor.
        Toast.makeText(getApplicationContext(),"can't go back",Toast.LENGTH_LONG).show();
    }
}