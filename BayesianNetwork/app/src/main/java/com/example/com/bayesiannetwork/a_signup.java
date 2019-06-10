package com.example.com.bayesiannetwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.bayesiannetwork.transaction.activity_creditcard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class a_signup extends AppCompatActivity {

    Button signup,registercard;

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
                //signup sign = new signup(a_signup.this,);
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
        ProgressDialog dialog;
        String urldata=urlsource.getloginurl;
        JSONObject svrdata;
        Context ctx;

        public signup(Context ctx ,String usr,String pss){
            username = usr;
            password = pss;
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
                        .add("password",password)
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
}
