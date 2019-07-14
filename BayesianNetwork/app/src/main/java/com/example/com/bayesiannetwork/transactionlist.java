package com.example.com.bayesiannetwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.bayesiannetwork.adapter.mainmenu_adapter;
import com.example.com.bayesiannetwork.adapter.transaction_adapter;
import com.example.com.bayesiannetwork.object.product;
import com.example.com.bayesiannetwork.object.transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class transactionlist extends AppCompatActivity {

    TextView notransaction;
    RecyclerView rv;

    transaction_adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction History");

        rv = findViewById(R.id.transactiondata);
        notransaction = findViewById(R.id.notransaction);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadtransaction load = new loadtransaction(this);
        load.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class loadtransaction extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        String urldata= urlsource.gettransaction;
        JSONObject svrdata;
        Context ctx;

        public loadtransaction(Context ctx ){
            this.ctx = ctx;
            dialog = new ProgressDialog(ctx);
            dialog.setTitle("Please Wait");
            dialog.setMessage("Loading");
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
                    if (svrdata.getString("status").equals("true")) {
                        if(svrdata.getInt("total")==0){
                            notransaction.setVisibility(View.VISIBLE);
                            rv.setVisibility(View.GONE);
                        }
                        else{
                            List<transaction> listdata = new ArrayList<>();
                            JSONArray dataarray = svrdata.getJSONArray("data");
                            for(int i = 0 ; i<dataarray.length();i++){
                                JSONObject obj = dataarray.getJSONObject(i);
                                transaction pro = new transaction();
                                pro.setTcart(obj.getString("cart_id"));
                                pro.setTid(obj.getString("transaction_id"));
                                pro.setTdate(obj.getString("create_at"));
                                pro.setTdouble(obj.getDouble("transaction_amount"));
                                pro.setTpayment(obj.getString("transaction_payment"));
                                pro.setTstatus(obj.getString("transaction_process"));
                                listdata.add(pro);
                            }

                            notransaction.setVisibility(View.GONE);
                            adapter = new transaction_adapter(ctx,listdata);
                            rv.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(ctx, 1);
                            rv.setLayoutManager(layoutmanager);
                            rv.setHasFixedSize(true);
                            rv.setAdapter(adapter);
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
                Request request = new Request.Builder()
                        .url(urldata)
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
