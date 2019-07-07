package com.example.com.bayesiannetwork.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.object.cart;
import com.example.com.bayesiannetwork.object.creditcard;
import com.example.com.bayesiannetwork.urlsource;

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

public class creditcardlist_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DecimalFormat formatter = new DecimalFormat("###,###,###.00");

    List<creditcard> items = new ArrayList<>();

    private OnLoadMoreListener onLoadMoreListener;

    RecyclerView rv;
    TextView noproduct;
    Button checkout;



    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public creditcardlist_adapter(Context context, List<creditcard> items, TextView noproduct, RecyclerView view) {
        this.items = items;
        ctx = context;
        rv = view;
        this.noproduct = noproduct;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView number,name;
        public View lyt_parent;


        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            number = v.findViewById(R.id.pnumber);
            name = v.findViewById(R.id.pname);

            lyt_parent = v.findViewById(R.id.viewall);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_creditcard, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final creditcard obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
            if(obj.getCard_type().equals("MasterCard")){
                Glide.with(ctx).load(R.drawable.mastercard).into(view.image);
            }
            else if(obj.getCard_type().equals("Visa")){
                Glide.with(ctx).load(R.drawable.visa).into(view.image);
            }
            else{
                Glide.with(ctx).load(R.drawable.unknown).into(view.image);
            }

            view.name.setText(obj.getCard_name() +" ("+obj.getMonth()+"/"+obj.getYear()+")");
            view.number.setText(obj.getCard_number());

            view.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog dialog = new AlertDialog.Builder(ctx,R.style.AppCompatAlertDialogStyle).setTitle("Warning").setMessage("Hapus Kartu ?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deletecard card = new deletecard(ctx,obj.getCard_id(),position);
                                    card.execute();
                                }
                            })
                            .setNegativeButton("batal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void checkview(){
        if(items.size()==0){
            rv.setVisibility(View.GONE);
            noproduct.setVisibility(View.GONE);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    public class deletecard extends AsyncTask<String, String, String> {
        String username;
        String password;
        ProgressDialog dialog;
        SharedPreferences prefs;
        String urldata= urlsource.deletecreditcardurl;
        JSONObject svrdata;
        Context ctx;
        String iddata;
        int type=0;
        int positions = 0;
        String id;
        int price;
        TextView change,realprice;

        public deletecard(Context ctx ,String id,int positions){
            this.positions = positions;
            this.ctx = ctx;
            prefs = ctx.getSharedPreferences("bayesiannetwork",MODE_PRIVATE);
            this.iddata = prefs.getString("username","");
            this.id = id;

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
                        items.remove(positions);
                        notifyItemRemoved(positions);
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