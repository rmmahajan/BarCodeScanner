package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    EditText usernm, pswd;
    Button loginbtn;
    Bundle bundle;
    public RadioGroup rg;
    public RadioButton r1;
    private String type = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernm = findViewById(R.id.usernameL);
        pswd = findViewById(R.id.pwdL);
        loginbtn = findViewById(R.id.buttonL);
        rg = findViewById(R.id.radiogrpL);

        bundle = getIntent().getExtras();


        if (rg.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Fill the fields", Toast.LENGTH_SHORT).show();
        } else {
            int selectedId = rg.getCheckedRadioButtonId();
            // find the radiobutton by returned id

        }


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int selectedId) {
                r1 = findViewById(selectedId);

                String rbtn = r1.getText().toString();


                if (rbtn.equals("Carrier")) {
                    type = "0";
                }
                if (rbtn.equals("User")) {
                    type = "1";
                }
            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usr = usernm.getText().toString();
                String psd = pswd.getText().toString();
//                LocationGetter obj=new LocationGetter();
//                obj.CheckPermission();
//                obj.getLocation();
                if(usr.equals("") || psd.equals(""))
                {
                    Toast.makeText(LoginActivity.this, "Fill the fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String latitude = "30.7333";
                    String longitude = "76.7794";


                    new requestLogin(usr, psd, latitude, longitude, type).execute();

                    Intent intent = new Intent(LoginActivity.this,BarCodeScanner.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }


   /* public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        String username;
        String password;

        HttpGetRequest(String username,String password)
        {
            this.username=username;
            this.password=password;
        }

        @Override
        protected String doInBackground(String... params){

            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL("http://18.236.187.48:8000/login");
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }*/


    class requestLogin extends AsyncTask<URL, Integer, Long> {
        public String name, password, type,longitude,latitude;

        requestLogin(String name, String password, String latitude,String longitude,String type) {

            this.name = name;
            this.password = password;
            this.type=type;
            this.longitude=longitude;
            this.latitude=latitude;
        }

        @Override
        protected Long doInBackground(URL... urls) {
            JSONObject post_dict = new JSONObject();

            try {
                post_dict.put("Username", name);
                post_dict.put("Password", password);
                post_dict.put("Latitude", latitude);
                post_dict.put("Longitude", longitude);
                post_dict.put("Type",type);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://18.236.187.48:8000/register");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(String.valueOf(post_dict));
                // System.out.println("+hghjgjhghjg+============="+post_dict);
// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                String JsonResponse = buffer.toString();
//response data
                Log.i(TAG, JsonResponse);

                return null;


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }
    }
}