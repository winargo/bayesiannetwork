package com.example.com.bayesiannetwork.transaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.a_login;
import com.example.com.bayesiannetwork.adapter.mainmenu_adapter;
import com.example.com.bayesiannetwork.object.product;
import com.example.com.bayesiannetwork.urlsource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class browse extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView usrname,email;
    SharedPreferences prefs;

    TextView noproduct;
    RecyclerView rv;

    mainmenu_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.productlist);
        noproduct = findViewById(R.id.noproduct);

        prefs = getSharedPreferences("bayesiannetwork",MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(browse.this);

        View viewdata = navigationView.getHeaderView(0);
        usrname = viewdata.findViewById(R.id.name);
        email = viewdata.findViewById(R.id.email);

        usrname.setText(prefs.getString("username",""));
        email.setText(prefs.getString("email",""));

        loadproducts load = new loadproducts(this);
        load.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.product) {
            // Handle the camera action
        } else if (id == R.id.transaction) {

        } else if (id == R.id.creditcard) {

        } else if (id == R.id.report) {

        } else if (id == R.id.cart) {

        }else if (id == R.id.logout) {
            startActivity(new Intent(browse.this, a_login.class));
            finish();
            prefs.edit().clear().apply();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class loadproducts extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        String urldata= urlsource.getproducts;
        JSONObject svrdata;
        Context ctx;

        public loadproducts(Context ctx ){
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
                if(svrdata!=null){
                    if (svrdata.getString("status").equals("true")) {
                        if(svrdata.getInt("total")==0){
                            noproduct.setVisibility(View.VISIBLE);
                            rv.setVisibility(View.GONE);
                        }
                        else{
                            List<product> listdata = new ArrayList<>();
                            JSONArray dataarray = svrdata.getJSONArray("data");
                            for(int i = 0 ; i<dataarray.length();i++){
                                JSONObject obj = dataarray.getJSONObject(i);
                                product pro = new product();
                                pro.setId(obj.getString("product_id"));
                                pro.setDescription(obj.getString("product_description"));
                                pro.setImage(obj.getString("product_image"));
                                pro.setLeft(obj.getInt("product_stock"));
                                pro.setPrice(obj.getDouble("product_price"));
                                pro.setProductname(obj.getString("product_name"));
                                listdata.add(pro);
                            }

                            noproduct.setVisibility(View.GONE);
                            adapter = new mainmenu_adapter(ctx,listdata);
                            rv.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(ctx, 2);
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
}
