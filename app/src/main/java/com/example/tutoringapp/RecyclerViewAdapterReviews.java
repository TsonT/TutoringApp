package com.example.tutoringapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewAdapterReviews extends RecyclerView.Adapter<RecyclerViewAdapterReviews.MyViewHolder>{

    private Context mContext ;
    private ArrayList<ObjectReview> mData ;


    public RecyclerViewAdapterReviews(Context mContext, ArrayList<ObjectReview> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.layout_review,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String strStars;
        strStars = String.format("%s", mData.get(position).getStars());
        holder.text_Name.setText(mData.get(position).getReviewer());
        holder.text_Stars.setText(strStars);
        holder.text_Comment.setText(mData.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text_Name;
        TextView text_Stars;
        TextView text_Comment;

        public MyViewHolder(final View itemView) {
            super(itemView);
            text_Name = itemView.findViewById(R.id.text_Name);
            text_Stars = itemView.findViewById(R.id.text_Stars);
            text_Comment = itemView.findViewById(R.id.text_Comment);
        }
    }


}


