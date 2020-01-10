package com.example.tutoringapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewAdapterTutorProfile extends RecyclerView.Adapter<RecyclerViewAdapterTutorProfile.MyViewHolder>{

    private Context mContext ;
    private ArrayList<TutorProfile> mData ;


    public RecyclerViewAdapterTutorProfile(Context mContext, ArrayList<TutorProfile> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.layout_cardview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.text_Name.setText(mData.get(position).getName());
        holder.text_Subjects.setText(mData.get(position).getSubjectsStr());
        Glide.with(mContext)
                .load(mData.get(position).getUrlPic())
                .fitCenter()
                .into(holder.img_profilePic);
        holder.cardView.setTag(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text_Name;
        TextView text_Subjects;
        ImageView img_profilePic;
        CardView cardView ;

        public MyViewHolder(final View itemView) {
            super(itemView);
            text_Name = (TextView) itemView.findViewById(R.id.text_Name);
            text_Subjects = (TextView) itemView.findViewById(R.id.text_Subjects);
            img_profilePic = (ImageView) itemView.findViewById(R.id.img_profilePic);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ActivityTutorProfile.class);
                    TutorProfile tutorProfile = (TutorProfile)cardView.getTag();
                    intent.putExtra("tutorProfile", tutorProfile);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }


}


