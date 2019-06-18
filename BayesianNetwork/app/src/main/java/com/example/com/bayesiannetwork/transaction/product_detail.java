package com.example.com.bayesiannetwork.transaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.urlsource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class product_detail extends AppCompatActivity {

    ImageView image;
    TextView title,price,leftstock,description;

    Button addtocart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("name"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        image = findViewById(R.id.detailproduct);

        if(getIntent().getStringExtra("image").equals("")){
            Glide.with(this).load(R.drawable.empty).into(image);
        }
        else{
            Glide.with(this).load(urlsource.getproductsimg+getIntent().getStringExtra("image")).into(image);
        }


        title = findViewById(R.id.title);
        price = findViewById(R.id.price);

        title.setText(getIntent().getStringExtra("name"));

        leftstock = findViewById(R.id.left);

        price.setText("Price : Rp "+getIntent().getStringExtra("price"));
        description = findViewById(R.id.description);


        description.setText(getIntent().getStringExtra("desc"));
        leftstock.setText(getIntent().getStringExtra("left"));


        addtocart = findViewById(R.id.btnadd);

        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addtocart add = new addtocart(product_detail.this,getIntent().getStringExtra("id"),getIntent().getStringExtra("price"));
                add.execute();
            }
        });

    }

    public class addtocart extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.addtocardurl;
        JSONObject svrdata;
        Context ctx;
        String iddata;
        Double price;

        public addtocart(Context ctx ,String id,String price){
            this.ctx = ctx;
            this.iddata = id;
            this.price = Double.parseDouble(price.replace(",",""));
            prefs = getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Adding...");
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
                if(svrdata!=null){
                    Toast.makeText(ctx, svrdata.getString("message"), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ctx, "Tidak terkoneksi", Toast.LENGTH_SHORT).show();
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
                        .add("id",iddata)
                        .add("username",prefs.getString("username",""))
                        .add("price",String.valueOf(price))
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
            } catch (Exception e){
                e.printStackTrace();
            }


            return "";
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
