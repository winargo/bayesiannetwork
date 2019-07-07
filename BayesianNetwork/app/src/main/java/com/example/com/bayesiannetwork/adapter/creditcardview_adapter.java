package com.example.com.bayesiannetwork.adapter;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.object.creditcard;

import java.util.ArrayList;
import java.util.List;

public class creditcardview_adapter extends ArrayAdapter<creditcard> {

    private int resourceLayout;
    private Context ctx;
    List<creditcard> items = new ArrayList<>();

    public creditcardview_adapter(Context context,int resurce, List<creditcard> items) {
        super(context, resurce, items);
        this.ctx = context;
        this.items=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(ctx);
            v = vi.inflate(R.layout.detail_creditcard, null);
        }

        creditcard obj = getItem(position);

        if (obj != null) {
            ImageView image;
            TextView number,name;
            View lyt_parent;
            image = v.findViewById(R.id.image);
            number = v.findViewById(R.id.pnumber);
            name = v.findViewById(R.id.pname);

            lyt_parent = v.findViewById(R.id.viewall);

            if(obj.getCard_type().equals("MasterCard")){
                Glide.with(ctx).load(R.drawable.mastercard).into(image);
            }
            else if(obj.getCard_type().equals("Visa")){
                Glide.with(ctx).load(R.drawable.visa).into(image);
            }
            else{
                Glide.with(ctx).load(R.drawable.unknown).into(image);
            }

            name.setText(obj.getCard_name() +" ("+obj.getMonth()+"/"+obj.getYear()+")");
            number.setText(obj.getCard_number());

        }

        return v;
    }

    @Override
    public int getCount() {
        return items.size();
    }
}