package com.example.com.bayesiannetwork.transaction;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.adapter.cart_adapter;
import com.example.com.bayesiannetwork.detailcart;
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

public class activity_cart extends AppCompatActivity {

    ImageView image;
    TextView title,price,leftstock,description;

    RecyclerView rv;

    cart_adapter adapter;

    TextView noproduct,totals;

    LinearLayout layout;

    DecimalFormat formatter = new DecimalFormat("###,###,###.00");



    Button checkout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartl);

        checkout = findViewById(R.id.btnadd);
        layout = findViewById(R.id.layouts);

        noproduct = findViewById(R.id.noproduct);
        totals = findViewById(R.id.totals);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cart");

        rv = findViewById(R.id.productsdata);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        viewcart add = new viewcart(activity_cart.this);
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
                       if(dataarray.length()==0){
                           noproduct.setVisibility(View.VISIBLE);
                           rv.setVisibility(View.GONE);
                           layout.setVisibility(View.GONE);
                           checkout.setVisibility(View.GONE);
                       }
                       else{
                           double count = 0.0d;
                           for(int i = 0 ; i<dataarray.length();i++){
                               JSONObject obj = dataarray.getJSONObject(i);
                               cart pro = new cart();
                               pro.setCart_id(obj.getString("cart_id"));
                               pro.setProduct_id(obj.getString("product_id"));
                               pro.setImg(obj.getString("product_image"));
                               pro.setQty(obj.getInt("qty"));
                               pro.setProductname(obj.getString("product_name"));
                               pro.setTotal(obj.getDouble("total"));
                               count = count+obj.getDouble("total");
                               listdata.add(pro);
                           }
                           totals.setText("Rp "+formatter.format(count));

                           adapter = new cart_adapter(ctx,listdata,checkout,noproduct,totals,layout,rv);

                           noproduct.setVisibility(View.GONE);
                           rv.setVisibility(View.VISIBLE);
                           layout.setVisibility(View.VISIBLE);
                           checkout.setVisibility(View.VISIBLE);
                           checkout.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   startActivityForResult(new Intent(activity_cart.this, detailcart.class),1001);
                               }
                           });
                           RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(ctx, 1);
                           rv.setLayoutManager(layoutmanager);
                           rv.setHasFixedSize(true);
                           rv.setAdapter(adapter);
                       }


                   }
                   else{
                       noproduct.setVisibility(View.VISIBLE);
                       rv.setVisibility(View.GONE);
                       layout.setVisibility(View.GONE);
                       checkout.setVisibility(View.GONE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1001){
            if(resultCode==RESULT_OK){
                finish();
            }
        }
        else{

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
