package com.ozgursahan.keepit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ForMain { //extends AppCompatActivity: bu sınıfın bir aktivite ekranına sahip olduğunu belirtiyor

    private ListView listView;
    private EditText groupNameText;

    private String grupIsmi = null;
    private int kullaniciSayisi=0;

    private ArrayList<Kullanicilar> kullanicilarArrayList;
    private ArrayList<Girdiler> girdilerArrayList;
    private ArrayList<Gruplar> gruplarArrayList;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor; // =sharedPreferences.edit(); 'e eşdeğer

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Bu metod içerisindekiler, aktivite açıldığında çalışır.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findViewById görünümleri tanımlıyoruz
        listView=findViewById(R.id.listView);
        groupNameText=findViewById(R.id.groupNameText);

        getData(); //uygulama açıldığında (aktivite çalıştığında) sharedpreferences içindeki veriler çekiliyor

        gruplariListele(); //grupları listview'a listeleme
    }

    public void addGroup(View view) // grup ekleme (View view >> metodun bir görünüm tarafından çağırıldığını gösterir.)
    {
        grupIsmi=groupNameText.getText().toString().trim(); // trim(); girdinin baş ve sonundaki boşlukları siler

        if(!grupIsmi.equals("")) // girdi kısmı doluysa
        {
            for(Gruplar g : gruplarArrayList)
            {
                if(g.grupIsmi.equals(grupIsmi))
                {
                    grupIsmi=grupIsmi+"1"; // girilen grup ismi zaten varsa "1" ekliyor ismin yanına
                }
            }
            Gruplar yeniGrup=new Gruplar(grupIsmi,0); // yeni grup nesnesi oluşturma
            gruplarArrayList.add(yeniGrup); // oluşturulan grubu gruplarArraylist'e ekleme

            Intent intent = new Intent(getApplicationContext(), KullaniciKaydiActivity.class); // KullaniciKaydi'na giden intent
            intent.putExtra("grupIsmi",grupIsmi);
            intent.putExtra("kullanicilarArraylist",kullanicilarArrayList);
            intent.putExtra("gruplarArraylist",gruplarArrayList);
            finish();
            startActivity(intent);

        }
        else Toast.makeText(getApplicationContext(),"Grup ismi giriniz.",Toast.LENGTH_LONG).show(); // grup ismi girilmemişse

    }

    public void deleteGroup(View view) // grup silme (View view >> metodun bir görünüm tarafından çağırıldığını gösterir.)
    {
        try
        { //yaşanabilecek hatalarda app'in kapanmaması için try and catch metodu

            grupIsmi=groupNameText.getText().toString().trim();

            if(!grupIsmi.equals("")) // girdi boş değilse
            {
                AlertDialog.Builder alert= new AlertDialog.Builder(this);
                alert.setTitle("Sil");
                alert.setMessage(grupIsmi+" grubu silinsin mi?");
                alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() { // Evet basılırsa
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(gruplarArrayList!=null)
                        {
                            for(Gruplar g : gruplarArrayList)
                            {
                                if(g.grupIsmi.equals(grupIsmi))
                                {   //burada belki daha iyi bir çözüm bulunabilirdi (çünkü bir bakıma hafızayı boşaltmıyoruz, kullanılamayacak hale getiriyoruz!)
                                    if(girdilerArrayList!=null)
                                    {
                                        for(Girdiler a : girdilerArrayList)
                                        {
                                            if(a.getGrupIsmi().equals(grupIsmi)) //o gruptan bir girdi varsa çalışacak
                                            {
                                                //girdilerArrayList.remove(a); // ilk seferde silmiyor bu
                                                a.setGrupIsmi(""); //boşluk grubu bir daha oluşturulamaz bu yüzden hata çözüldü
                                                editor.putString("girdilerArraylist", new Gson().toJson(girdilerArrayList)).apply(); // yeni arraylist'i sharedPreferences'a yükleme
                                            }
                                        }
                                    }

                                    if(kullanicilarArrayList!=null)
                                    {
                                        for(Kullanicilar b : kullanicilarArrayList)
                                        {
                                            if(b.getGrupIsmi().equals(grupIsmi))
                                            {
                                                //kullanicilarArrayList.remove(b); // ilk seferde silmiyor bu
                                                b.setGrupIsmi(""); //boşluk grubu bir daha oluşturulamaz bu yüzden hata çözüldü
                                                editor.putString("kullanicilarArraylist", new Gson().toJson(kullanicilarArrayList)).apply(); // yeni arraylist'i sharedPreferences'a yükleme
                                            }
                                        }
                                    }

                                    gruplarArrayList.remove(g); // g (grup nesnesi) kaldırılıyor
                                    editor.putString("gruplarArraylist", new Gson().toJson(gruplarArrayList)).apply(); // yeni arraylist'i sharedPreferences'a yükleme

                                    //ekranın refresh'lenmesi için intent kullanımı
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                });
                alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        groupNameText.setText(""); // girilen eski girdi ile tekrar karşılaşılmasın diye
                    }
                });
                alert.show();
            }
            else Toast.makeText(getApplicationContext(),"Grup ismi giriniz.",Toast.LENGTH_LONG).show(); // grup ismi girilmemişse


        }catch (Exception error)
        {
            error.printStackTrace();
        }
    }

    public void gruplariListele() { //grupları listview'a listeleme
        List<String> stringListOfGroups = new ArrayList<>(gruplarArrayList.size());
        for (Gruplar g : gruplarArrayList)
        {
            stringListOfGroups.add(Objects.toString(g.grupIsmi,null));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, stringListOfGroups);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { // listview'dan bir iteme tıklandığında
                Intent intent = new Intent(getApplicationContext(),KullaniciGirisActivity.class);
                grupIsmi=adapterView.getItemAtPosition(i).toString(); // tıklanan grupismi alınıyor

                // intentler içerisine yükleme
                intent.putExtra("grupIsmi",grupIsmi);

                // tıklanan grubun kullanıcı sayısı çekiliyor
                for(Gruplar g : gruplarArrayList)
                {
                    if(g.grupIsmi.equals(grupIsmi))
                    {
                        kullaniciSayisi=g.kullaniciSayisi;
                    }
                }

                intent.putExtra("kullaniciSayisi",kullaniciSayisi);
                intent.putExtra("gruplarArraylist",gruplarArrayList);
                intent.putExtra("kullanicilarArraylist",kullanicilarArrayList);
                intent.putExtra("girdilerArraylist",girdilerArrayList);
                startActivity(intent);
            }
        });
    }

    public void getData() { //uygulama açıldığında (aktivite çalıştığında) sharedpreferences içindeki veriler çekiliyor
        sharedPreferences = getSharedPreferences("com.ozgursahan.keepit",MODE_PRIVATE); // paketten sharedPreferences verileri alınıyor
        editor = sharedPreferences.edit();

        kullanicilarArrayList = new Gson().fromJson(sharedPreferences.getString("kullanicilarArraylist",null), new TypeToken<List<Kullanicilar>>(){}.getType());
        gruplarArrayList = new Gson().fromJson(sharedPreferences.getString("gruplarArraylist",null), new TypeToken<List<Gruplar>>(){}.getType());
        girdilerArrayList = new Gson().fromJson(sharedPreferences.getString("girdilerArraylist",null), new TypeToken<List<Girdiler>>(){}.getType());

        //İLK KURULUM'da arraylistler null olacağı için bu ifler içerisinde tanımlıyoruz.
        if(gruplarArrayList==null)
        {
            gruplarArrayList = new ArrayList<>();
            editor.putString("gruplarArraylist", new Gson().toJson(gruplarArrayList)).apply();
        }

        if(kullanicilarArrayList==null)
        {
            kullanicilarArrayList = new ArrayList<>();
            editor.putString("kullanicilarArraylist", new Gson().toJson(kullanicilarArrayList)).apply();
        }

        if(girdilerArrayList==null)
        {
            girdilerArrayList = new ArrayList<>();
            editor.putString("girdilerArraylist", new Gson().toJson(girdilerArrayList)).apply();
        }
    }

}