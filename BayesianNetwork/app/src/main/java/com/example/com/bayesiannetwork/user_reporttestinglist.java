package com.example.com.bayesiannetwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.com.bayesiannetwork.object.cart;
import com.example.com.bayesiannetwork.object.listreport;

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

import static android.content.Context.MODE_PRIVATE;

public class user_reporttestinglist extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DecimalFormat formatter = new DecimalFormat("###,###,###.00");

    List<listreport> items = new ArrayList<>();

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

    public user_reporttestinglist(Context context, List<listreport> items) {
        this.items = items;
        ctx = context;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView email,type,message;

        public OriginalViewHolder(View v) {
            super(v);
            email = v.findViewById(R.id.email);
            type = v.findViewById(R.id.type);
            message =  v.findViewById(R.id.message);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_report, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final listreport obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            view.email.setText(obj.getEmail());
            view.type.setText(obj.getType());
            view.message.setText(obj.getMessage());
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

    public class actioncart extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.checkcarturl;
        JSONObject svrdata;
        Context ctx;
        String iddata;
        int type=0;
        int positions = 0;
        String id;
        int price;
        TextView change,realprice;

        public actioncart(Context ctx ,int type,String id,int price,TextView edit,int pos,TextView realprice){
            this.ctx = ctx;
            positions = pos;
            prefs = ctx.getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            this.iddata = prefs.getString("username","");
            this.type = type;

            change = edit;

            this.realprice=realprice;

            this.id=id;

            this.price=price;

            if(type == 0){
                urldata = urlsource.mincarturl;
            }
            else if(type==1){
                urldata = urlsource.addcarturl;
            }
            else if(type==2){
                urldata = urlsource.removecarturl;
            }


            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Executing...");
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
                        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
                        if(type == 0){
                            realprice.setText("Total : Rp "+formatter.format(price*(Double.parseDouble(change.getText().toString())-1)));
                            change.setText(String.valueOf(Integer.parseInt(change.getText().toString())-1));
                            totals.setText("Rp "+formatter.format(Double.parseDouble(totals.getText().toString().replace("Rp","").replace(" ","").replace(",",""))-price));
                        }
                        else if(type==1){
                            realprice.setText("Total : Rp "+formatter.format(price*(Double.parseDouble(change.getText().toString())+1)));
                            change.setText(String.valueOf(Integer.parseInt(change.getText().toString())+1));
                            totals.setText("Rp "+formatter.format(price+Double.parseDouble(totals.getText().toString().replace("Rp","").replace(" ","").replace(",",""))));
                        }
                        else if(type==2){
                            items.remove(positions);
                            notifyItemRemoved(positions);
                            if(items.size()==0){
                                noproduct.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                                checkout.setVisibility(View.GONE);
                                layout.setVisibility(View.GONE);
                            }
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
                        .add("id",id)
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



}