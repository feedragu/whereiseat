package com.ancora.gmaps2.federico.googlemapsactivity.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ancora.gmaps2.federico.googlemapsactivity.R;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

/**
 * Created by Federico on 21/05/2016.
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>{
    private long FADE_DURATION=500;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView v, us;
        private ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.imageProf);
            v = (TextView) view.findViewById(R.id.txtC);
            us = (TextView) view.findViewById(R.id.txtUs);
        }
    }

    private LinkedList<String> pm, u, listP;
    private int resource;
    private Context context;
    private LayoutInflater inflater;

    public CategoriesAdapter(Context context, int resource, LinkedList<String> objects, LinkedList<String> u, LinkedList<String> listP) {
        this.pm = objects;
        this.context=context;
        this.u = u;
        this.listP=listP;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listcomment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.v.setText(pm.get(position));
        holder.us.setText(u.get(position));
        Log.i(" cosa cazzo succede", listP.size() + " " + position);
        Picasso.with(context)
                .load(listP.get(position))
                .fit()
                .into(holder.icon);
        //setScaleAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return pm.size();
    }

    public void addItem(String us, String c, String url) {
        pm.add(c);
        u.add(us);
        listP.add(url);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public void add(String us, String comm, LinkedList<String> listP, int position) {
        u.add(position,comm);
        pm.add(position, us);
        this.listP=listP;
        notifyItemInserted(position);
    }

}
