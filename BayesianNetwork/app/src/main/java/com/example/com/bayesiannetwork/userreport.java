package com.example.com.bayesiannetwork;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.com.bayesiannetwork.adapter.creditcardview_adapter;
import com.example.com.bayesiannetwork.adapter.transaction_adapter;
import com.example.com.bayesiannetwork.object.creditcard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class userreport extends AppCompatActivity {

    EditText fullname,email,message;
    ImageView image;

    Bitmap mBitmap;

    Button report;
    int ist=0,isp=0,iso=0,isc=0,ispr=0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userreport);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Report Issue");

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        message = findViewById(R.id.messages);

        report = findViewById(R.id.btnreport);

        image = findViewById(R.id.proof);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String[] arraySpinner = new String[] {
                "Report Transaction issue","Report Credit Card issue", "Report Product issue", "Report Payment issue","Report Other"
        };
        final Spinner s = findViewById(R.id.dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheckStorage = ContextCompat.checkSelfPermission(userreport.this,
                        Manifest.permission.CAMERA);
                if(permissionCheckStorage == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(userreport.this,new String[]{
                            Manifest.permission.CAMERA}, 14);
                }
                else{
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, 7);
                }
            }
        });

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(s.getSelectedItemPosition()==0){
                    ist = 1;
                    isc = 0;
                    ispr= 0;
                    iso = 0;
                    isp = 0;
                }
                else if(s.getSelectedItemPosition()==1){
                    isc = 1;
                    ist = 0;
                    ispr= 0;
                    iso = 0;
                    isp = 0;
                }
                else if(s.getSelectedItemPosition()==2){
                    ispr = 1;
                    ist = 0;
                    isc= 0;
                    iso = 0;
                    isp = 0;
                }
                else if(s.getSelectedItemPosition()==3){
                    isp = 1;
                    ist = 0;
                    ispr= 0;
                    iso = 0;
                    isc = 0;
                }
                else if(s.getSelectedItemPosition()==4){
                    iso = 1;
                    ist = 0;
                    ispr= 0;
                    isp = 0;
                    isc = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fullname.getText().toString().equals("")){
                    if(isvalidemail(email.getText().toString()))
                    {
                        sentreport sent = new sentreport(userreport.this,iso,isc,isp,ispr,ist,fullname.getText().toString(),email.getText().toString(),message.getText().toString());
                        sent.execute();
                    }
                    else{
                        Toast.makeText(userreport.this, "Email is invalid", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(userreport.this, "Full name cannot be empty", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class sentreport extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.userreporturl;
        JSONObject svrdata;
        Context ctx;
        String fullname,email,message;
        int istrans,ispayment,isother,isproduct,iscreditcard;


        public sentreport(Context ctx,int iso,int isc, int isp,int ispr,int ist,String fullname,String email,String message){
            iscreditcard = isc;
            isother = iso;
            isproduct = ispr;
            ispayment = isp;
            this.message = message;
            istrans = ist;
            this.email = email;
            this.fullname = fullname;
            this.ctx = ctx;
            prefs = ctx.getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
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
                        Toast.makeText(ctx, "Report Sent", Toast.LENGTH_SHORT).show();
                        ((Activity)ctx).finish();
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
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String name = String.valueOf(Calendar.getInstance().getTimeInMillis());

                File f = new File(ctx.getCacheDir(), name);
                f.createNewFile();
                //Convert bitmap to byte array
                Bitmap bitmap1 = mBitmap ;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, out);
                byte[] bitmapdata = out.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                RequestBody bodydata=  new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", name,
                                RequestBody.create(MediaType.get("image/jpeg"),f))
                        .addFormDataPart("transaction",istrans+"")
                        .addFormDataPart("payment",ispayment+"")
                        .addFormDataPart("product",isproduct+"")
                        .addFormDataPart("creditcard",iscreditcard+"")
                        .addFormDataPart("message",message)
                        .addFormDataPart("fullname",fullname)
                        .addFormDataPart("email",email)
                        .addFormDataPart("username",prefs.getString("username",""))
                        .build();

                Request request = new Request.Builder()
                        .url(urldata)
                        .post(bodydata)
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
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mBitmap = bitmap;
            image.setImageBitmap(bitmap);
        }
        else{

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 14:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(userreport.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(userreport.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
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
}


