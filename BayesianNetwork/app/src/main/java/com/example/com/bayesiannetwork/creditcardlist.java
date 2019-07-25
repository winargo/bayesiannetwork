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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.bayesiannetwork.adapter.checkout_adapter;
import com.example.com.bayesiannetwork.adapter.creditcardlist_adapter;
import com.example.com.bayesiannetwork.formula.thousandedittext;
import com.example.com.bayesiannetwork.object.cart;
import com.example.com.bayesiannetwork.object.creditcard;
import com.example.com.bayesiannetwork.transaction.activity_creditcard;
import com.example.com.bayesiannetwork.transaction.browse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class creditcardlist extends AppCompatActivity {

    RecyclerView rv;
    TextView nocard;

    Button addnew;

    creditcardlist_adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditcardlist);


        nocard = findViewById(R.id.nocreditcard);

        addnew = findViewById(R.id.buttonadd);

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(creditcardlist.this);
                LinearLayout v = (LinearLayout) inflater.inflate(R.layout.dialog_newcreditcard,null);

                final EditText card_number,card_type,card_first,card_last,billing,cvv,month,year,limit;

                card_number = v.findViewById(R.id.rccardnumber);
                card_type = v.findViewById(R.id.rccard_type);
                card_first = v.findViewById(R.id.rcfirstname);
                billing = v.findViewById(R.id.rcaddress);
                cvv = v.findViewById(R.id.rccvv);
                month = v.findViewById(R.id.rcmonth);
                year = v.findViewById(R.id.rcyear);

                limit = v.findViewById(R.id.rclimit);

                limit.addTextChangedListener(new thousandedittext(limit));

                card_number.addTextChangedListener(new TextWatcher() {

                    private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
                    private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
                    private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
                    private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
                    private static final char space = ' ';

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // noop
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (card_number.getText().toString().equals("")){

                        }
                        else{
                            if(card_number.getText().toString().substring(0,1).equals("4")){
                                card_type.setText("Visa");
                            }
                            else if(card_number.getText().toString().substring(0,1).equals("5")){
                                card_type.setText("MasterCard");
                            }
                            else{
                                card_type.setText("Unknown");
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        if (s.length() > 0 && (s.length() % 5) == 0) {
                            final char c = s.charAt(s.length() - 1);
                            if (space == c) {
                                s.delete(s.length() - 1, s.length());
                            }
                        }
                        // Insert char where needed.
                        if (s.length() > 0 && (s.length() % 5) == 0) {
                            char c = s.charAt(s.length() - 1);
                            // Only if its a digit where there should be a space we insert a space
                            if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                                s.insert(s.length() - 1, String.valueOf(space));
                            }
                        }
                    }

                });

                AlertDialog.Builder builder = new AlertDialog.Builder(creditcardlist.this,R.style.AppCompatAlertDialogStyle);
                builder.setView(v);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("Save",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(card_number.getText().toString().equals("")){
                            Toast.makeText(creditcardlist.this, "Card Number Can't Be empty", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(Luhn.Check(card_number.getText().toString())==true){
                                if(card_first.getText().toString().equals("")){
                                    Toast.makeText(creditcardlist.this, "Card Name Can't Be empty", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    if(billing.getText().toString().equals("")){
                                        Toast.makeText(creditcardlist.this, "Billing Address Can't Be empty", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        if(cvv.getText().toString().equals("")){
                                            Toast.makeText(creditcardlist.this, "CVV Can't Be empty", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            if(month.getText().toString().equals("")){
                                                Toast.makeText(creditcardlist.this, "Card Expiracy Month Can't Be empty", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                if(Integer.valueOf(month.getText().toString())>12 || Integer.valueOf(month.getText().toString())<1){
                                                    Toast.makeText(creditcardlist.this, "Card Expiracy month is invalid, specify ranges between (01-12)", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    if(year.getText().toString().equals("")){
                                                        Toast.makeText(creditcardlist.this, "Card Expiracy Year Can't Be empty", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        Calendar cal = Calendar.getInstance();
                                                        cal.setTime(new Date());

                                                        Calendar cal1 = Calendar.getInstance();
                                                        cal1.setTime(new Date());

                                                        int years = cal.get(Calendar.YEAR);
                                                        int yearslater = years+50;
                                                        cal1.set(Calendar.YEAR,Integer.valueOf(year.getText().toString()));
                                                        cal1.set(Calendar.MONDAY,Integer.valueOf(month.getText().toString()));
                                                        if(Integer.valueOf(year.getText().toString())>yearslater || Integer.valueOf(year.getText().toString())<years || cal1.getTime().equals(cal.getTime())){
                                                            Toast.makeText(creditcardlist.this, "Card Expiracy Year is invalid or expired, specify ranges between ("+years+"-"+yearslater+")", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            if(limit.getText().toString().equals("")){
                                                                Toast.makeText(creditcardlist.this, "Card Transaction Limit Can't Be empty", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else{
                                                                creditcard data = new creditcard();
                                                                data.setCard_billing(billing.getText().toString());
                                                                data.setCard_name(card_first.getText().toString());
                                                                data.setCard_number(card_number.getText().toString());
                                                                data.setCard_type(card_type.getText().toString());
                                                                data.setCard_billing(billing.getText().toString());
                                                                data.setCvv(Integer.parseInt(cvv.getText().toString()));
                                                                data.setMonth(Integer.parseInt(month.getText().toString()));
                                                                data.setYear(Integer.parseInt(year.getText().toString()));
                                                                data.setLimit(Integer.parseInt(limit.getText().toString().replace(" ","").replace(",","")));
                                                                registercreditcard card = new registercreditcard(creditcardlist.this, data,dialog);
                                                                card.execute();
                                                                dialog.dismiss();
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                            else{
                                Toast.makeText(creditcardlist.this, "Invalid Card Number", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Credit Card List");

        rv = findViewById(R.id.creditcarddata);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        retrivecreditcard card = new retrivecreditcard(this);
        card.execute();

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

        public retrivecreditcard(Context ctx ){
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
                            nocard.setVisibility(View.VISIBLE);
                            rv.setVisibility(View.GONE);
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

                            adapter = new creditcardlist_adapter(ctx,listdata,nocard,rv);

                            nocard.setVisibility(View.GONE);
                            rv.setVisibility(View.VISIBLE);
                            RecyclerView.LayoutManager layoutmanager = new GridLayoutManager(ctx, 1);
                            rv.setLayoutManager(layoutmanager);
                            rv.setHasFixedSize(true);
                            rv.setAdapter(adapter);
                        }
                    }
                    else{
                        nocard.setVisibility(View.VISIBLE);
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

    public class registercreditcard extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        String urldata=urlsource.signupcardurl;
        JSONObject svrdata;
        Context ctx;
        creditcard carddata;
        SharedPreferences prefs;
        AlertDialog dialogs;

        public registercreditcard(Context ctx , creditcard card,AlertDialog dialogs){
            prefs = ctx.getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            username = prefs.getString("username","");
            password = prefs.getString("password","");
            this.ctx = ctx;
            this.dialogs = dialogs;
            carddata = card;
            dialog = new ProgressDialog(ctx);
            dialog.setTitle("Please Wait");
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
                if (svrdata.getString("status").equals("true")) {
                    retrivecreditcard card = new retrivecreditcard(ctx);
                    card.execute();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
