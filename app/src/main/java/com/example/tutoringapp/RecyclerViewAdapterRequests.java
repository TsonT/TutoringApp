package com.example.tutoringapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RecyclerViewAdapterRequests extends RecyclerView.Adapter<RecyclerViewAdapterRequests.MyViewHolder>{

    private Context mContext ;
    String strRecipientName;
    String strRequesterName;
    String strStatus, strRequestName = "";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<ObjectRequest> mData;
    MyViewHolder viewHolder;
    DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("TutorInfo");


    public RecyclerViewAdapterRequests(Context mContext, ArrayList<ObjectRequest> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.layout_requests,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        viewHolder = holder;

        Log.e("string", "string");

        final ObjectRequest request = mData.get(position);
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
               strRequesterName = dataSnapshot.child(request.RequesterUid).child("name").getValue().toString();
               strRecipientName = dataSnapshot.child(request.RecipientUid).child("name").getValue().toString();

               viewHolder.itemView.setOnClickListener(null);

               if (request.isCancelled)
                {
                    strStatus = "This Request Was Cancelled";
                    strRequestName = "Requested By: " + strRequesterName + "\n Requested: " + strRecipientName;
                }
               else
               {
                   if (request.RequesterUid.equals(mAuth.getCurrentUser().getUid()) && request.isAccepted == null)
                   {
                       strStatus = "Waiting For Response...";
                       strRequestName = "Requested Tutor: " + strRecipientName;
                       viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       switch (which){
                                           case DialogInterface.BUTTON_POSITIVE:
                                               mUsers.child(mData.get(position).getRequesterUid()).child("SentRequests").child(mData.get(position).getKey()).removeValue();
                                               mUsers.child(mData.get(position).getRecipientUid()).child("PendingRequests").child(mData.get(position).getKey()).removeValue();
                                               mData.get(position).isCancelled = true;
                                               mUsers.child(mData.get(position).getRequesterUid()).child("Responses").child(mData.get(position).getKey()).setValue(mData.get(position));
                                               mUsers.child(mData.get(position).getRecipientUid()).child("Responses").child(mData.get(position).getKey()).setValue(mData.get(position));

                                               dialog.dismiss();
                                               Intent intent = new Intent(mContext, ActivityHome.class);
                                               viewHolder.itemView.getContext().startActivity(intent);
                                               break;

                                           case DialogInterface.BUTTON_NEGATIVE:
                                               dialog.dismiss();
                                               break;
                                       }
                                   }
                               };
                               AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                               builder.setMessage("Would You Like To Cancel This Request?").setPositiveButton("Yes", dialogClickListener)
                                       .setNegativeButton("No", dialogClickListener).show();
                           }
                       });
                   }
                   else if(request.RecipientUid.equals(mAuth.getCurrentUser().getUid()) && request.isAccepted == null)
                   {
                       strStatus = "Someone Sent You A Request!";
                       strRequestName = "Requested By: " + strRequesterName;
                       viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent intent = new Intent(mContext, ActivityStudentProfile.class);
                               intent.putExtra("Name", strRequesterName);
                               intent.putExtra("Request", mData.get(position));
                               intent.putExtra("Image", dataSnapshot.child(request.RequesterUid).child("urlPic").getValue().toString());
                               viewHolder.itemView.getContext().startActivity(intent);
                           }
                       });
                   }
                   else if(request.isAccepted)
                   {
                       String strDate;
                       Date date = null;
                       date = convertToDate(request.getDate());
                       if (new Date().after(date))
                       {
                           request.setFinished(true);
                           strStatus = "This Request Has Been Finished!";
                           strRequestName = "Leave A Review";
                           viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent(mContext, ActivityLeaveReview.class);
                                   if (request.RecipientUid.equals(mAuth.getCurrentUser().getUid()))
                                   {
                                       intent.putExtra("ReviewerUid", request.getRecipientUid());
                                       intent.putExtra("ReviewedUid", request.getRequesterUid());
                                       intent.putExtra("ReviewerName", strRecipientName);
                                       intent.putExtra("isReviewForTutor", false);
                                       intent.putExtra("ObjectRequest", request);
                                   }
                                   else if (request.RequesterUid.equals(mAuth.getCurrentUser().getUid()))
                                   {
                                       intent.putExtra("ReviewerUid", request.getRequesterUid());
                                       intent.putExtra("ReviewedUid", request.getRecipientUid());
                                       intent.putExtra("ReviewerName", strRequesterName);
                                       intent.putExtra("isReviewForTutor", true);
                                       intent.putExtra("ObjectRequest", request);
                                   }
                                   viewHolder.itemView.getContext().startActivity(intent);
                               }
                           });
                       }
                       if (request.RequesterUid.equals(mAuth.getCurrentUser().getUid()) && !request.isFinished)
                       {
                           strStatus = "Request Accepted!";
                           strRequestName = "Requested Tutor: " + strRecipientName;
                           viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent(mContext, ActivityStudentProfile.class);
                                   intent.putExtra("Name", strRecipientName);
                                   intent.putExtra("Request", mData.get(position));
                                   intent.putExtra("Image", dataSnapshot.child(request.RecipientUid).child("urlPic").getValue().toString());
                                   viewHolder.itemView.getContext().startActivity(intent);
                               }
                           });
                       }
                       else if(request.RecipientUid.equals(mAuth.getCurrentUser().getUid()) && !request.isFinished)
                       {
                           strStatus = "You Accepted This Request!";
                           strRequestName = "Requested By: " + strRequesterName;
                           viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent(mContext, ActivityStudentProfile.class);
                                   intent.putExtra("Name", strRequesterName);
                                   intent.putExtra("Request", mData.get(position));
                                   intent.putExtra("Image", dataSnapshot.child(request.RequesterUid).child("urlPic").getValue().toString());
                                   viewHolder.itemView.getContext().startActivity(intent);
                               }
                           });
                       }

                   }
                   else if (!request.isAccepted)
                   {
                       if (request.RequesterUid.equals(mAuth.getCurrentUser().getUid()))
                       {
                           strStatus = "Sorry Your Tutor Denied Your Request :(";
                           strRequestName =  "Requested Tutor: " + strRecipientName + "\n Comment: " + request.getMessage();
                       }
                       else if(request.RecipientUid.equals(mAuth.getCurrentUser().getUid()))
                       {
                           strStatus = "You Denied This Request...";
                           strRequestName = "Requested By: " + strRequesterName;
                       }

                   }
               }
                holder.textView.setText(strStatus);
                holder.textView2.setText(strRequestName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView, textView2;

        public MyViewHolder(final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
        }
    }

    public Date convertToDate(String strDate)
    {
        Date date = null;

        try {
            date = new SimpleDateFormat("MM/dd/yy").parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}


