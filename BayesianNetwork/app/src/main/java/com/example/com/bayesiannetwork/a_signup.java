package com.example.com.bayesiannetwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.bayesiannetwork.object.creditcard;
import com.example.com.bayesiannetwork.transaction.activity_creditcard;
import com.example.com.bayesiannetwork.transaction.browse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class a_signup extends AppCompatActivity {

    Button signup,registercard;

    String type = "";

    EditText username,password,confirmpass,address,email,hp;

    RadioButton male,female;

    TextView registeredcreditcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_registration);

        username = findViewById(R.id.rusername);
        password = findViewById(R.id.rpassword);
        confirmpass = findViewById(R.id.rconfirm);
        address = findViewById(R.id.raddress);
        email = findViewById(R.id.remail);
        hp = findViewById(R.id.rhp);

        male = findViewById(R.id.rmale);
        female = findViewById(R.id.rfemale);

        registeredcreditcard = findViewById(R.id.rcredittotal);

        male.setChecked(true);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(female.isChecked()){
                    female.setChecked(false);
                }
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(male.isChecked()){
                    male.setChecked(false);
                }
            }
        });

        signup = findViewById(R.id.rsignup);
        registercard = findViewById(R.id.rregistercard);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(username.getText().toString().equals("")){
                    Toast.makeText(a_signup.this, "Username can't be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(password.getText().toString().equals("")){
                        Toast.makeText(a_signup.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(confirmpass.getText().toString().equals("")){
                            Toast.makeText(a_signup.this, "Confirm Password can't be empty", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(!password.getText().toString().equals(confirmpass.getText().toString())){
                                Toast.makeText(a_signup.this, "Password confirmation does not match", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if(address.getText().toString().equals("")){
                                    Toast.makeText(a_signup.this, "Address can't be empty", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    if(email.getText().toString().equals("")){
                                        Toast.makeText(a_signup.this, "email can't be empty", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        if(hp.getText().toString().equals("")){
                                            Toast.makeText(a_signup.this, "Phone number can't be empty", Toast.LENGTH_SHORT).show();
                                        }
                                        else{

                                            if(male.isChecked()){
                                                type="Male";
                                            }
                                            else{
                                                type="Female";
                                            }
                                            signup sign = new signup(a_signup.this,username.getText().toString(),password.getText().toString(),address.getText().toString(),email.getText().toString(),hp.getText().toString(),type);
                                            sign.execute();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        });

        registercard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                core.alllistcreditcard = new ArrayList<>();
                startActivityForResult(new Intent(a_signup.this, activity_creditcard.class),2);
            }
        });
    }


    public class signup extends AsyncTask<String, String, String> {
        String username;
        String password;
        String email;
        String adress;
        String hp;
        String type;
        ProgressDialog dialog;
        String urldata=urlsource.signupurl;
        JSONObject svrdata;
        Context ctx;

        public signup(Context ctx ,String usr,String pss,String addres,String email,String hp,String gender){
            username = usr;
            password = pss;
            this.email = email;
            this.adress = addres;
            this.hp = hp;
            this.type = gender;
            this.ctx = ctx;
            dialog = new ProgressDialog(ctx);
            dialog.setTitle("Please Wait");
            dialog.setMessage("Login...");
            dialog.show();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                if (svrdata==null){
                    Toast.makeText(ctx, "Cant Establish Connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (svrdata.getString("status").equals("true")) {

                        if (core.alllistcreditcard == null) {

                        }
                        else{
                            for (int i = 0; i < core.alllistcreditcard.size(); i++) {
                                registercreditcard card = new registercreditcard(a_signup.this, core.alllistcreditcard.get(i), username, urlsource.md5(password));
                                card.execute();
                            }
                        }

                        Toast.makeText(ctx, svrdata.getString("message"), Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor edit = getSharedPreferences("bayesiannetwork",MODE_PRIVATE).edit();
                        edit.putInt("login",1);
                        edit.putString("username",username);
                        edit.putString("password",password);
                        edit.putString("email",email);
                        edit.apply();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(ctx, svrdata.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody body=new FormBody.Builder()
                        .add("username",username)
                        .add("password",urlsource.md5(password))
                        .add("nama",username)
                        .add("address",adress)
                        .add("email",email)
                        .add("hp",hp)
                        .add("limit","0")
                        .add("gender",type)
                        .build();

                Request request = new Request.Builder()
                        .url(urldata)
                        .post(body)
                        .build();
                Response responses = null;

                try {
                    responses = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                svrdata = new JSONObject(responses.body().string());
                Log.e("server error",svrdata+"" );
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();

            }


            return "";
        }
    }

    public boolean isvalidemail (final String email){

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==2){
            if(resultCode==RESULT_OK){
                if(core.alllistcreditcard.size()==0){
                    registeredcreditcard.setText("No Credit Card Registered");
                }
                else{
                    registeredcreditcard.setText(core.alllistcreditcard.size() + " Credit Card Registered");
                }
            }
        }
    }

    public class registercreditcard extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        String urldata=urlsource.signupcardurl;
        JSONObject svrdata;
        Context ctx;
        creditcard carddata;

        public registercreditcard(Context ctx , creditcard card,String usr,String pss){
            username = usr;
            password = pss;
            this.ctx = ctx;
            carddata = card;
            dialog = new ProgressDialog(ctx);
            dialog.setTitle("Please Wait");
            dialog.setMessage("Login...");
            dialog.show();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                if (svrdata.getString("status").equals("true")) {
                    Toast.makeText(ctx, svrdata.getString("message"), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ctx, browse.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(ctx, svrdata.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();



                RequestBody body=new FormBody.Builder()
                        .add("username",username)
                        .add("password",urlsource.md5(password))
                        .add("cvv",String.valueOf(carddata.getCvv()))
                        .add("month",String.valueOf(carddata.getMonth()))
                        .add("year",String.valueOf(carddata.getYear()))
                        .add("billing",carddata.getCard_billing())
                        .add("cardname",carddata.getCard_name())
                        .add("number",carddata.getCard_number())
                        .add("type",carddata.getCard_type())
                        .add("limit",carddata.getLimit()+"")
                        .build();

                Request request = new Request.Builder()
                        .url(urldata)
                        .post(body)
                        .build();
                Response responses = null;

                try {
                    responses = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                svrdata = new JSONObject(responses.body().string());
                Log.e("server error",svrdata+"" );
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }


            return "";
        }
    }
}
