package com.example.com.bayesiannetwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.adapter.cart_adapter;
import com.example.com.bayesiannetwork.adapter.checkout_adapter;
import com.example.com.bayesiannetwork.object.cart;
import com.example.com.bayesiannetwork.urlsource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class detailcart extends AppCompatActivity {

    ImageView image;
    TextView title,price,leftstock,description;

    RecyclerView rv;

    checkout_adapter adapter;

    TextView noproduct,subtotal,ongkir,totalpayment;

    DecimalFormat formatter = new DecimalFormat("###,###,###.00");

    Button paynow,paylater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        paynow = findViewById(R.id.btnpaynow);
        paylater = findViewById(R.id.btnpaylater);

        subtotal = findViewById(R.id.totaltransaction);
        ongkir = findViewById(R.id.totalongkir);
        totalpayment = findViewById(R.id.totalpayment);


        noproduct = findViewById(R.id.noproduct);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cart");

        rv = findViewById(R.id.productsdata);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        viewcart add = new viewcart(detailcart.this);
        add.execute();
    }

    public class viewcart extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.checkcarturl;
        JSONObject svrdata;
        Context ctx;
        String iddata;

        public viewcart(Context ctx ){
            this.ctx = ctx;
            prefs = getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            this.iddata = prefs.getString("username","");
            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Loading activity_cart...");
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
                    if(svrdata.getString("status").equals("true")){
                        List<cart> listdata = new ArrayList<>();
                        JSONArray dataarray = svrdata.getJSONArray("data");
                        Double countt = 0.0d;
                        if(svrdata.length()==0){
                            noproduct.setVisibility(View.VISIBLE);
                            rv.setVisibility(View.GONE);
                            Toast.makeText(ctx, svrdata.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        else{

                            for(int i = 0 ; i<dataarray.length();i++){
                                JSONObject obj = dataarray.getJSONObject(i);
                                cart pro = new cart();
                                pro.setCart_id(obj.getString("cart_id"));
                                pro.setProduct_id(obj.getString("product_id"));
                                pro.setImg(obj.getString("product_image"));
                                pro.setQty(obj.getInt("qty"));
                                pro.setProductname(obj.getString("product_name"));
                                pro.setTotal(obj.getDouble("total"));
                                countt = countt + obj.getDouble("total");
                                listdata.add(pro);
                            }
                            subtotal.setText("Rp "+formatter.format(countt));
                            ongkir.setText("Rp "+formatter.format(30000.0d));
                            totalpayment.setText("Rp "+formatter.format(countt+30000.0d));

                            adapter = new checkout_adapter(ctx,listdata);

                            noproduct.setVisibility(View.GONE);
                            rv.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(ctx, 1);
                            rv.setLayoutManager(layoutmanager);
                            rv.setHasFixedSize(true);
                            rv.setAdapter(adapter);
                        }
                        final Double finalCountt = countt;
                        paynow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                paylater pay = new paylater(detailcart.this, finalCountt +30000.0d,1);
                                pay.execute();
                            }
                        });


                        paylater.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                paylater pay = new paylater(detailcart.this, finalCountt +30000.0d,0);
                                pay.execute();
                            }
                        });
                    }
                    else{
                        noproduct.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                        Toast.makeText(ctx, svrdata.getString("message"), Toast.LENGTH_SHORT).show();
                    }
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
                        .add("username",prefs.getString("username",""))
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


    public class paylater extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.checkcarturl;
        JSONObject svrdata;
        Context ctx;
        String iddata;
        Double total ;
        int type;

        public paylater(Context ctx,Double total,int type){
            this.ctx = ctx;
            this.total = total;
            this.type = type;
            prefs = getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            this.iddata = prefs.getString("username","");
            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Processing..");
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
                    if(svrdata.getString("status").equals("true")){
                        if(type==0){
                            setResult(RESULT_OK);
                            finish();
                        }
                        else{
                            Intent trans = new Intent(detailcart.this,transactionlist.class);
                            trans.putExtra("paynow",1);
                            startActivity(trans);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                    else{
                        Toast.makeText(ctx, svrdata.getString("message"), Toast.LENGTH_SHORT).show();
                    }
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
                        .add("username",prefs.getString("username",""))
                        .add("amount",total+"")
                        .add("ipaddress",urlsource.getIPAddress(true))
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
