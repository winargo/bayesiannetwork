package com.example.com.bayesiannetwork.adapter;

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
import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.object.cart;
import com.example.com.bayesiannetwork.object.transaction;
import com.example.com.bayesiannetwork.urlsource;

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
            }
            else{
                view.tstatus.setText("Status : Paid");
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

}