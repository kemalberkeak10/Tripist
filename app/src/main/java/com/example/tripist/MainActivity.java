package com.example.tripist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button deneme;
    ListView listView;
    TextView havadurumu;
    SQLiteDatabase database;
    Adapter adapter;
    ArrayList<Yerler> yerlerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deneme = findViewById(R.id.deneme);
        listView = findViewById(R.id.listView);
        havadurumu= findViewById(R.id.havaDurumu);

        getData();

       verileriCek();

    }
   class Weather extends AsyncTask<Void, Void , String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL weatheraddress = new URL("http://api.openweathermap.org/data/2.5/weather?q=Istanbul&appid=e9f28400958342fd868a8a775923208a");
                HttpURLConnection httpURLConnection = (HttpURLConnection) weatheraddress.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));

                String sonuc = "";
                String satir = "";


                while ((satir=br.readLine())!=null){
                    sonuc += satir ;



                }
                br.close();
                httpURLConnection.disconnect();
                return sonuc;
            } catch (Exception e) {
                System.out.println("Hata:" +  e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            havadurumu.setText(s);
        }
    }
    public void verileriCek(){
        Weather havadurumucek = new Weather();
        havadurumucek.execute();
    }



      public void nextPage(View view){

        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra("info","new");
        startActivity(intent);

     }
            //database verileri almak
     public void getData(){
         adapter = new Adapter(this,yerlerList);
        try{
            database = this.openOrCreateDatabase("Yerler",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT * FROM yerler",null);

           int nameIX = cursor.getColumnIndex("name");
           int latitudeIX = cursor.getColumnIndex("latitude");
           int longitudeIX = cursor.getColumnIndex("longitude");

           while (cursor.moveToNext()){
               String nameFromDatabase = cursor.getString(nameIX);
               String latitudeFromDatabase = cursor.getString(latitudeIX);
               String longitudeFromDatabase = cursor.getString(longitudeIX);

               Double latitude = Double.parseDouble(latitudeFromDatabase);
               Double longitude = Double.parseDouble(longitudeFromDatabase);

               Yerler yer = new Yerler(nameFromDatabase,latitude,longitude);

               System.out.println(yer.name);
               yerlerList.add(yer);

           }
           //veri değişikligini onayla
           adapter.notifyDataSetChanged();
           cursor.close();

        }catch (Exception e){
            e.printStackTrace();


        }

        listView.setAdapter(adapter);

        //LISTViewe tıklandıgında nolacak
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("info","oldu");
                intent.putExtra("yer",yerlerList.get(position));
                startActivity(intent);
            }
        });




     }








}