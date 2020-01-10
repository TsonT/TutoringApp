package com.example.tutoringapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterChecks extends RecyclerView.Adapter<RecyclerViewAdapterChecks.ViewHolder> {

    private Context context;
    ArrayList<ObjectChk> lstSubject;

    public RecyclerViewAdapterChecks(Context context, ArrayList<ObjectChk> lstSubject) {
        this.context = context;
        this.lstSubject = lstSubject;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.checkBox.setText(lstSubject.get(position).getName());
        if (!lstSubject.get(position).isChecked())
        {
            holder.checkBox.setChecked(false);
        }
        else
        {
            holder.checkBox.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return lstSubject.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox checkBox;
        LinearLayout parent_Layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < lstSubject.size(); i++)
                    {
                        if (checkBox.getText().equals(lstSubject.get(i).getName()))
                        {
                            if (!lstSubject.get(i).isChecked())
                            {
                                lstSubject.get(i).setChecked(true);
                            }
                            else
                            {
                                lstSubject.get(i).setChecked(false);
                            }
                        }
                    }
                }
            });
            parent_Layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
