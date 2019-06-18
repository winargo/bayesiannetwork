package com.example.com.bayesiannetwork.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.object.product;
import com.example.com.bayesiannetwork.transaction.product_detail;
import com.example.com.bayesiannetwork.urlsource;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class mainmenu_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DecimalFormat formatter = new DecimalFormat("###,###,###.00");

    List<product> items = new ArrayList<>();

    private OnLoadMoreListener onLoadMoreListener;



    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public mainmenu_adapter(Context context, List<product> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView price,left;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.pname);
            price = v.findViewById(R.id.pprice);
            left = v.findViewById(R.id.pqty);
            lyt_parent =  v.findViewById(R.id.viewall);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_browse, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final product obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            if(obj.getImage().equals("")){
                Glide.with(ctx).load(R.drawable.empty).into(view.image);
            }
            else{
                Glide.with(ctx).load(urlsource.getproductsimg+obj.getImage()).into(view.image);
            }

            view.price.setText("Rp "+ formatter.format(obj.getPrice()));
            view.price.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
            view.left.setText("Left :"+String.valueOf(obj.getLeft()));
            view.name.setText(obj.getProductname());

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent a = new Intent(ctx, product_detail.class);
                    a.putExtra("desc",obj.getDescription());
                    a.putExtra("name",obj.getProductname());
                    a.putExtra("price",formatter.format(obj.getPrice()));
                    a.putExtra("left","Left :"+String.valueOf(obj.getLeft()));
                    a.putExtra("id",obj.getId());
                    if(obj.getImage().equals("")){
                        a.putExtra("image","");
                    }
                    else{
                        a.putExtra("image",obj.getImage());
                    }
                    ctx.startActivity(a);

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

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}