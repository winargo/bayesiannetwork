package com.example.com.bayesiannetwork.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.detailcart;
import com.example.com.bayesiannetwork.object.cart;
import com.example.com.bayesiannetwork.object.creditcard;
import com.example.com.bayesiannetwork.object.transaction;
import com.example.com.bayesiannetwork.transactionlist;
import com.example.com.bayesiannetwork.urlsource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class transaction_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DecimalFormat formatter = new DecimalFormat("###,###,###.00");

    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    List<transaction> items = new ArrayList<>();

    private OnLoadMoreListener onLoadMoreListener;

    RecyclerView rv;
    TextView noproduct,totals;
    Button checkout;

    LinearLayout layout;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public transaction_adapter(Context context, List<transaction> items) {
        this.items = items;
        ctx = context;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView tid,tdate,tamount,tstatus;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            tid = v.findViewById(R.id.tid);
            tdate = v.findViewById(R.id.tdate);
            tamount = v.findViewById(R.id.ttl);
            tstatus= v.findViewById(R.id.tst);
            lyt_parent =  v.findViewById(R.id.viewall);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_transaction, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final transaction obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            view.tid.setText("Transaction ID : "+obj.getTid());

            try {
                view.tdate.setText(format2.format(format1.parse(obj.getTdate().replace(".000Z","").replace("T"," "))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            view.tamount.setText("Total : Rp"+formatter.format(obj.getTdouble()));

            if(obj.getTstatus().equals("0")){
                view.tstatus.setText("Status : Not Paid");

                view.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popup = new PopupMenu(ctx, view.lyt_parent);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.menunotpaid);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.viewtransa:
                                        Intent intent = new Intent(ctx,detailcart.class);
                                        intent.putExtra("tid",obj.getTid());
                                        ((Activity)ctx).startActivity(intent);
                                        return true;
                                    case R.id.action_pay:
                                        retrivecreditcard card = new retrivecreditcard(ctx, obj.getTdouble(),obj.getTid());
                                        card.execute();
                                    default:
                                        return false;
                                }
                            }
                        });
                        //displaying the popup
                        popup.show();
                        return true;
                    }
                });
            }
            else{
                view.tstatus.setText("Status : Paid");

                view.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popup = new PopupMenu(ctx, view.lyt_parent);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.menupaid);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.viewtransa:
                                        Intent intent = new Intent(ctx,detailcart.class);
                                        intent.putExtra("tid",obj.getTid());
                                        ((Activity)ctx).startActivity(intent);
                                        return true;
                                    case R.id.action_pay:
                                        getpayment getpay = new getpayment(ctx,obj.getTid());
                                        getpay.execute();
                                    default:
                                        return false;
                                }
                            }
                        });
                        //displaying the popup
                        popup.show();
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    public class paynow extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.checkoutpurl;
        JSONObject svrdata;
        Context ctx;
        String iddata;
        Double total ;
        int type;
        int cardid;

        public paynow(Context ctx,Double total,int type,int card_id,String id){
            this.ctx = ctx;
            cardid = card_id;
            this.total = total;
            this.type = type;
            prefs = ctx.getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            this.iddata = id;
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

                            Intent trans = new Intent(ctx, transactionlist.class);
                            ((Activity)ctx).startActivity(trans);
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

                RequestBody body=new FormBody.Builder()
                        .add("username",prefs.getString("username",""))
                        .add("amount",total+"")
                        .add("trans_id",iddata)
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

        public retrivecreditcard(Context ctx,Double total ,String id){
            passed = total;
            this.ctx = ctx;
            prefs = ctx.getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            this.iddata = id;
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
                                    paynow pay = new paynow(ctx,passed+30000.0d,0,Integer.parseInt(((creditcard)list.getItemAtPosition(i)).getCard_id()),iddata);
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

    public class getpayment extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.getpayment;
        JSONObject svrdata;
        Context ctx;
        String iddata;
        Double passed;

        public getpayment(Context ctx,String transactionid){
            this.ctx = ctx;
            prefs = ctx.getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            this.iddata = transactionid;
            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Loading Payment");
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
                        Log.e("detail",svrdata.toString() );
                        JSONArray data = svrdata.getJSONArray("data");
                        JSONObject obj = data.getJSONObject(0);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx).setTitle("Payment Detail")
                                .setMessage("Payment ID : "+obj.getString("payment_id")+"\n"+"Date Time : "+format2.format(format1.parse(obj.getString("payment_period").replace(".000Z","").replace("T"," ")))+"\nCard :"+obj.getString("card_number")+"\nAmount : Rp"+formatter.format(obj.getDouble("transaction_amount"))).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                        dialog.show();
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
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody body=new FormBody.Builder()
                        .add("trans_id",iddata)
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
}