package com.example.tutoringapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterChat extends RecyclerView.Adapter<RecyclerViewAdapterChat.MyViewHolder>{

    private Context mContext ;
    private ArrayList<ObjectMessage> mData;


    public RecyclerViewAdapterChat(Context mContext, ArrayList<ObjectMessage> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.layout_chat,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.text_Name.setText(mData.get(position).getSender());
        holder.text_Date.setText(mData.get(position).getDate() + " : " + mData.get(position).getTime());
        holder.text_Message.setText(mData.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text_Name, text_Message, text_Date;

        public MyViewHolder(final View itemView) {
            super(itemView);
            text_Name = itemView.findViewById(R.id.text_Name);
            text_Message = itemView.findViewById(R.id.text_Message);
            text_Date = itemView.findViewById(R.id.text_Date);
        }
    }
}


