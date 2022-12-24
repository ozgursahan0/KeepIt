package com.ozgursahan.keepit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KullaniciKaydiActivity extends AppCompatActivity implements ForKullaniciKaydi { //extends AppCompatActivity: bu sınıfın bir aktivite ekranına sahip olduğunu belirtiyor

    private EditText newNametext,newSurnameText,newPasswordText;
    private ListView usersListView;
    private Button grubuOlusturButton;

    private String isim, soyisim, sifre, grupIsmi;
    private int kullaniciSayisi;

    private ArrayList<Kullanicilar> kullanicilarArrayList;
    private ArrayList<Gruplar> gruplarArrayList;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor; // =sharedPreferences.edit(); 'e eşdeğer

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Bu metod içerisindekiler, aktivite açıldığında çalışır.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_kaydi);

        //görünümleriyle eşleştiriliyorlar
        newNametext=findViewById(R.id.newNameText);
        newSurnameText=findViewById(R.id.newSurnameText);
        newPasswordText=findViewById(R.id.newPasswordText);
        usersListView=findViewById(R.id.usersListView);
        grubuOlusturButton=findViewById(R.id.grubuOlusturButton);
        kullaniciSayisi=0;

        getData(); // sharedPreferences ve intent ile gelen veriler alınıyor.

        Toast.makeText(getApplicationContext(),grupIsmi+" grubu oluşturuldu!",Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),"En az 2 kullanıcı girilmeli!",Toast.LENGTH_LONG).show();

        grubuOlusturButton.setVisibility(View.INVISIBLE); // 'ekle butonu'na en az 2 kere basılınca visible olacak
    }

    public void addNewUser(View view) // kullanıcı ekleme metodu (View view >> metodun bir görünüm tarafından çağırıldığını gösterir)
    {
        for(Gruplar g : gruplarArrayList)
        {
            if(g.grupIsmi.equals(grupIsmi)) // kullanıcılardaki grup ismi parametresine göre filtreleme yapmış oluyoruz.
            {
                isim =newNametext.getText().toString().trim();  // trim(); girdinin baş ve sonundaki boşlukları siler
                soyisim =newSurnameText.getText().toString().trim();
                sifre =newPasswordText.getText().toString();

                if(!sifre.contains(" ")) // şifre boşluk içermiyorsa
                {
                    if(!isim.equals("") && !soyisim.equals("") && !sifre.equals("")) //girdiler boş değilse
                    {
                        boolean control = true; //else'li yapamayız, for içindeki arraylist değişince çalışmaz kod
                        for(Kullanicilar k : kullanicilarArrayList)
                        {
                            if(k.getGrupIsmi().equals(grupIsmi) && k.getIsim().equals(isim) && k.getSoyisim().equals(soyisim)) // bu isimde bir kullanıcı var mı?
                            {
                                control=false;
                                Toast.makeText(getApplicationContext(),"Bu kullanıcı ismi kullanılmakta!",Toast.LENGTH_LONG).show();
                                kullaniciSayisi--; // kullanıcı eklenmemesine rağmen sayı artmasın diye yapıyoruz. (Çünkü 'EKLE' butonuna her basışta kullaniciSayisi++ yapıyoruz.)
                            }
                        }
                        if(control==true)
                        {
                            Kullanicilar newUser = new Kullanicilar(grupIsmi, isim, soyisim, sifre,0); // yeni kullanıcı
                            kullanicilarArrayList.add(newUser); // yeni kullanıcı ekleme
                        }

                        newNametext.setText(""); // eklemeden sonra edittext'ler içerisi boşaltılıyor.
                        newSurnameText.setText("");
                        newPasswordText.setText("");
                    }

                    else {
                        Toast.makeText(getApplicationContext(),"Kutucuklar boş bırakılamaz!",Toast.LENGTH_LONG).show();
                        kullaniciSayisi--; // kullanıcı eklenmemesine rağmen sayı artmasın diye yapıyoruz. (Çünkü 'EKLE' butonuna her basışta kullaniciSayisi++ yapıyoruz.)
                    }

                    kullanicilariListele(); //kullanıcılar listview'da listeleniyor
                }

                else // şifreye boşluk girilemez
                {
                    Toast.makeText(getApplicationContext(),"Şifreniz boşluk içeremez!",Toast.LENGTH_SHORT).show();
                }
            }
        }

        //kullanıcı sayısının yeni oluşturulan gruba eklenen her kullanıcıda güncellenmesi
        kullaniciSayisi++;
        for(Gruplar g : gruplarArrayList)
        {
            if(g.grupIsmi.equals(grupIsmi))
            {
                g.kullaniciSayisi=kullaniciSayisi;
            }
        }

        if(kullaniciSayisi==2) // artık 'Grup Oluştur' butonu görünür, tıklanabilir
        {
            grubuOlusturButton.setVisibility(View.VISIBLE);
        }

    }

    public void getData(){ // sharedPreferences ve intent ile gelen veriler alınıyor.
        sharedPreferences = getSharedPreferences("com.ozgursahan.keepit",MODE_PRIVATE); // paketten sharedPreferences verileri alınıyor
        editor = sharedPreferences.edit();

        Intent intent = getIntent(); // gelen intenti alır.
        //intent içerisindekileri alıp bu sınıfta tanımlamalar
        grupIsmi=intent.getStringExtra("grupIsmi");
        kullanicilarArrayList = (ArrayList<Kullanicilar>) getIntent().getSerializableExtra("kullanicilarArraylist");
        gruplarArrayList = (ArrayList<Gruplar>) getIntent().getSerializableExtra("gruplarArraylist");
    }

    public void grubuOlustur(View view) // butonun onClick metodu
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        // Ana menü yeni listeleri alabilsin diye, sharedPreferences güncellemesi yapılıyor.
        editor.putString("kullanicilarArraylist", new Gson().toJson(kullanicilarArrayList)).apply();
        editor.putString("gruplarArraylist", new Gson().toJson(gruplarArrayList)).apply();
        finish();
        startActivity(intent);
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
        usersListView.setAdapter(arrayAdapter);
    }

    @Override
    public void onBackPressed() { // Back tuşu basılsın istemiyoruz. Kullanıcı 'GRUBU OLUŞTUR' butonuna tıklamalı
        //super.onBackPressed(); << bu metod çağırılmayarak 'BACK' tuşu bloklanıyor.
        Toast.makeText(getApplicationContext(),"can't go back",Toast.LENGTH_LONG).show();
    }
}