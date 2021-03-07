package com.example.gestioarticles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class WeatherActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        setTitle("Quin temps fa?");

        //ADD BUTTON
        Button btn = (Button) findViewById(R.id.btnInfo);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String ciutat;

                TextView tv;

                tv = (TextView) findViewById(R.id.txtCity);
                ciutat = String.valueOf(tv.getText());

                String url = "http://api.openweathermap.org/data/2.5/weather?q=" + ciutat + "&appid=b4f6e217e0406958886cd9355f8fbe39&lang=ca";


                new AsyncHttpClient().get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String resposta = new String(responseBody);
                        //Toast.makeText(getApplicationContext(), resposta, Toast.LENGTH_LONG).show();
                        String descripcio ="";
                        String iconUrl = "http://openweathermap.org/img/w/01d.png";
                        double temperatura;
                        double temperaturaMax;
                        double temperaturaMin;

                        JSONObject obj = null;

                        try {
                            obj = new JSONObject(resposta);
                            JSONArray weather = obj.getJSONArray("weather");
                            JSONObject weatherObject = weather.getJSONObject(0);

                            descripcio = weatherObject.getString("description");

                            TextView descripcioText = (TextView) findViewById(R.id.descripcioText);
                            descripcioText.setText(descripcio);

                            iconUrl = "http://openweathermap.org/img/wn/" + weatherObject.get("icon") + "@2x.png";

                            //TEMPERATURA
                            JSONObject main = obj.getJSONObject("main");

                            temperatura = main.getDouble("temp");
                            TextView temperaturaText = (TextView) findViewById(R.id.temperaturaText);
                            temperaturaText.setText(String.format("%.2f", temperatura - 273.15));

                            temperaturaMin = main.getDouble("temp_min");
                            TextView temperaturaMinima = (TextView) findViewById(R.id.temperaturaMin);
                            temperaturaMinima.setText(String.format("%.2f", temperaturaMin - 273.15));

                            temperaturaMax = main.getDouble("temp_max");
                            TextView temperaturaMaxima = (TextView) findViewById(R.id.temperaturaMax);
                            temperaturaMaxima.setText(String.format("%.2f", temperaturaMax - 273.15));

                            //HUMITAT
                            TextView humitat = (TextView) findViewById(R.id.humitatValorId);
                            humitat.setText(String.format("%.2f", main.getDouble("humidity")));


                            //POBLACIO
                            TextView poblacioText = (TextView) findViewById(R.id.poblacioText);
                            poblacioText.setText(obj.getString("name"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        ImageView imgWeatherIcon = (ImageView) findViewById(R.id.imgWeatherIcon);
                        Picasso.with(getApplicationContext()).load(iconUrl).fit().into(imgWeatherIcon);


                       //Toast.makeText(getApplicationContext(), descripcio, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getApplicationContext(), "FAILURE", Toast.LENGTH_LONG).show();
                    }
                });


            }

        });

    }
}