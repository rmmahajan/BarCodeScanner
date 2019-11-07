package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
public Button button;
public EditText user,pwd,eml;
public RadioGroup rg;
public RadioButton r1;
private EditText cms,cmpnm;
    String compname="0";
    String coms="0";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        user = findViewById(R.id.username);
        pwd = findViewById(R.id.pwd);
        button=findViewById(R.id.button);
        eml=findViewById(R.id.email);
        rg = findViewById(R.id.radiogrp);
        cms = findViewById(R.id.comm);
        cmpnm = findViewById(R.id.cname);


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int selectedId) {
                r1 = findViewById(selectedId);

                String rbtn = r1.getText().toString();


                if(rbtn.equals("Carrier"))
                {
                    cms.setVisibility(View.VISIBLE);
                    cmpnm.setVisibility(View.VISIBLE);




                    compname = cmpnm.getText().toString();
                    coms = cms.getText().toString();

                }
                if(rbtn.equals("User"))
                {
                    cms.setVisibility(View.GONE);
                    cmpnm.setVisibility(View.GONE);
                    compname = "0";
                    coms = "0";
                }
            }
        });




        button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String usr = user.getText().toString();
        String psd = pwd.getText().toString();
        String email = eml.getText().toString();

        if(usr.equals("") || psd.equals("") || email.equals(""))
        {
            Toast.makeText(MainActivity.this, "Fill the fields", Toast.LENGTH_SHORT).show();
        }
        else {


            cms = findViewById(R.id.comm);
            cmpnm = findViewById(R.id.cname);

            compname = cmpnm.getText().toString();
            coms = cms.getText().toString();

            if(compname.equals("") || coms.equals(""))
            {
                Toast.makeText(MainActivity.this, "Fill the fields", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Toast.makeText(MainActivity.this, compname+"***********"+coms, Toast.LENGTH_SHORT).show();

                new request(usr, psd, email, coms, compname).execute();
            }
        }
    }
});
        }


    public void login(View view) {

        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
class request extends AsyncTask<URL, Integer,Long>{
public String name,password,email,companyName,commission;
    request(String name,String password,String email,String companyName,String commission){

        this.name=name;
        this.password=password;
        this.email=email;
        this.commission=commission;
        this.companyName=companyName;
    }
    @Override
    protected Long doInBackground(URL... urls) {
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("Username" , name);
            post_dict.put("Password",password);
           post_dict.put("Email",email);
            post_dict.put("Company_Name",companyName);
            post_dict.put("Commission",commission);


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
            Log.i(TAG,JsonResponse);

            return null;



        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
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
