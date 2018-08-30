package com.narmware.samista.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.narmware.samista.R;
import com.narmware.samista.activity.DetailedLeadActivity;
import com.narmware.samista.pojo.Comments;
import com.narmware.samista.pojo.Lead;
import com.narmware.samista.support.Endpoints;

import org.w3c.dom.Comment;

import java.util.ArrayList;

/**
 * Created by rohitsavant on 22/08/18.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.LeadHolder> {
    private Context mContext;
    private ArrayList<Comments> mData;

    public CommentAdapter(Context mContext, ArrayList<Comments> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public LeadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
        return new LeadHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadHolder holder, int position) {
        Comments comment = mData.get(position);
        holder.mItem = comment;

        holder.mTxtComment.setText(comment.getComment());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class LeadHolder extends RecyclerView.ViewHolder {
        TextView mTxtComment;
        LinearLayout mRelative;

        Comments mItem;
        View rootView;

        public LeadHolder(View itemView) {
            super(itemView);
            mTxtComment=itemView.findViewById(R.id.txt_comment);
            mRelative=itemView.findViewById(R.id.item_rel);

            rootView=itemView;
        }
    }
}
