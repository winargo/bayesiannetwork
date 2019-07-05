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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.object.cart;
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

public class checkout_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DecimalFormat formatter = new DecimalFormat("###,###,###.00");

    List<cart> items = new ArrayList<>();

    private OnLoadMoreListener onLoadMoreListener;



    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public checkout_adapter(Context context, List<cart> items) {
        this.items = items;
        ctx = context;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView price,qty,ttlprice;
        public View lyt_parent;


        Button add,minus;
        ImageButton thrower;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.ptitle);

            price = v.findViewById(R.id.price);
            ttlprice = v.findViewById(R.id.pprice);
            qty = v.findViewById(R.id.qty);
            lyt_parent =  v.findViewById(R.id.viewall);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_checkout, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final cart obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
            if(obj.getImg().equals("")){
                Glide.with(ctx).load(R.drawable.empty).into(view.image);
            }
            else{
                Glide.with(ctx).load(urlsource.getproductsimg+obj.getImg()).into(view.image);
            }

            view.price.setText("Rp "+ formatter.format(obj.getTotal()/obj.getQty()));

            view.ttlprice.setText("Total : Rp "+ formatter.format(obj.getTotal()));
            view.name.setText(obj.getProductname());

            view.qty.setText(String.valueOf(obj.getQty()));

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