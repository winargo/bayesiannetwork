package com.example.com.bayesiannetwork;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.adapter.cart_adapter;
import com.example.com.bayesiannetwork.adapter.checkout_adapter;
import com.example.com.bayesiannetwork.adapter.creditcardview_adapter;
import com.example.com.bayesiannetwork.object.cart;
import com.example.com.bayesiannetwork.object.creditcard;
import com.example.com.bayesiannetwork.urlsource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.FileNameMap;
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
        toolbar.setTitle("Checkout");

        rv = findViewById(R.id.productsdata);



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(getIntent().getStringExtra("tid")==null){
            viewcart add = new viewcart(detailcart.this);
            add.execute();
        }
        else{
            if(!getIntent().getStringExtra("tid").equals("")){
                paylater.setVisibility(View.GONE);
                paynow.setVisibility(View.GONE);
                viewcart add = new viewcart(detailcart.this,Integer.parseInt(getIntent().getStringExtra("tid")));
                add.execute();
            }
            else{
                viewcart add = new viewcart(detailcart.this);
                add.execute();
            }
        }



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
        int data = 0 ;

        public viewcart(Context ctx ){
            this.ctx = ctx;
            prefs = getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            this.iddata = prefs.getString("username","");
            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Loading activity_cart...");
            dialog.show();
        }

        public viewcart(Context ctx,int data ){
            this.ctx = ctx;
            this.data = data;
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
                                retrivecreditcard card = new retrivecreditcard(detailcart.this, finalCountt);
                                card.execute();
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

                Log.e("data",data+"" );

                RequestBody body=new FormBody.Builder()
                        .add("username",prefs.getString("username",""))
                        .add("number",data+"")
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
        String urldata= urlsource.checkouturl;
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

    public class paynow extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.checkoutnurl;
        JSONObject svrdata;
        Context ctx;
        String iddata;
        Double total ;
        int type;
        int cardid;

        public paynow(Context ctx,Double total,int type,int card_id){
            this.ctx = ctx;
            cardid = card_id;
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
                            Toast.makeText(ctx, "success check out and payment", Toast.LENGTH_LONG).show();
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
                        .add("idcc",cardid+"")
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

    public class retrivecreditcard extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.creditcardurl;
        JSONObject svrdata;
        Context ctx;
        String iddata;
        Double passed;

        public retrivecreditcard(Context ctx,Double total ){
            passed = total;
            this.ctx = ctx;
            prefs = getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            this.iddata = prefs.getString("username","");
            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Loading Credit cards");
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
                        List<creditcard> listdata = new ArrayList<>();
                        JSONArray dataarray = svrdata.getJSONArray("data");
                        if(svrdata.length()==0){
                            Toast.makeText(ctx, svrdata.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        else{

                            for(int i = 0 ; i<dataarray.length();i++){
                                JSONObject obj = dataarray.getJSONObject(i);
                                creditcard pro = new creditcard();
                                pro.setCard_id(obj.getString("card_id"));
                                pro.setLimit(obj.getInt("limits"));
                                pro.setYear(obj.getInt("card_year"));
                                pro.setMonth(obj.getInt("card_month"));
                                pro.setCard_number(obj.getString("card_number"));
                                pro.setCvv(obj.getInt("card_cvv"));
                                pro.setCard_type(obj.getString("card_type"));
                                pro.setCard_name(obj.getString("card_name"));
                                pro.setCard_billing(obj.getString("card_billing"));
                                listdata.add(pro);
                            }

                            final ListView list = new ListView(ctx);
                            creditcardview_adapter adapters =new creditcardview_adapter(ctx,android.R.layout.select_dialog_singlechoice,listdata);
                            list.setAdapter(adapters);


                            final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx,R.style.AppCompatAlertDialogStyle).setTitle("Choose Credit Card").setView(list).setNegativeButton("Cancel",null);

                            final AlertDialog dialogs = dialog.show();

                            dialogs.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    Button button = dialogs.getButton(AlertDialog.BUTTON_NEGATIVE);
                                    button.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            dialogs.dismiss();
                                        }
                                    });
                                }
                            });

                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    dialogs.dismiss();
                                    paynow pay = new paynow(ctx,passed+30000.0d,0,Integer.parseInt(((creditcard)list.getItemAtPosition(i)).getCard_id()));
                                    pay.execute();
                                }
                            });

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
