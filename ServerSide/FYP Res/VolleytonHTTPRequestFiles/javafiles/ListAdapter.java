package com.ascomp.database_prac;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.View> {

    ArrayList<Model> data;
    Context context;

    public ListAdapter(ArrayList<Model> data, Context context){
        this.data=data;
        this.context=context;
    }

    @Override
    public View onCreateViewHolder(ViewGroup parent, int viewType) {
        android.view.View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item ,parent, false);
        View vh= new View( view);
        return vh;
    }

    @Override
    public void onBindViewHolder(View holder, int position) {
        holder.tv_id.setText(data.get(position).id);
        holder.tv_title.setText(data.get(position).title);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class View extends RecyclerView.ViewHolder{
        TextView tv_id, tv_title;

        public View(android.view.View itemView) {
            super(itemView);
            tv_id=itemView.findViewById(R.id.tv_id);
            tv_title=itemView.findViewById(R.id.tv_title);


        }
    }
}

