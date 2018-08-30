package com.narmware.samista.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.narmware.samista.R;
import com.narmware.samista.activity.CommentActivity;
import com.narmware.samista.pojo.Comments;
import com.narmware.samista.support.Endpoints;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    private ArrayList<Comments> mData;

    public MyAdapter(Context mContext, ArrayList<Comments> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    class ViewHolder0 extends RecyclerView.ViewHolder {

        TextView mTxtComment,mTxtDate,mTxtName;


        public ViewHolder0(View itemView) {
            super(itemView);
            mTxtComment=itemView.findViewById(R.id.txt_comment);
            mTxtName=itemView.findViewById(R.id.txt_name);
            mTxtDate=itemView.findViewById(R.id.txt_date);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder {

        TextView mTxtComment,mTxtDate,mTxtName;

        public ViewHolder2(View itemView) {
            super(itemView);
            mTxtComment=itemView.findViewById(R.id.txt_comment);
            mTxtName=itemView.findViewById(R.id.txt_name);
            mTxtDate=itemView.findViewById(R.id.txt_date);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        Log.e("Adapter size",mData.size()+"");

        if(mData.get(position).getFlag().equals(Endpoints.GENERATOR))
        {
            return 0;
        }
        if(mData.get(position).getFlag().equals(Endpoints.CONVERTOR))
        {
            return 2;
        }
        return 0;
        //return position % 2 * 2;
    }

    @Override
    public int getItemCount() {

        if(mData.size()==0)
        {
            //Toast.makeText(mContext,"No data",Toast.LENGTH_SHORT).show();
            CommentActivity.mEmptyLinear.setVisibility(View.VISIBLE);
        }
        else {
            CommentActivity.mEmptyLinear.setVisibility(View.GONE);
        }
        return mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new ViewHolder0(LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false));
            case 2:
                return new ViewHolder2(LayoutInflater.from(mContext).inflate(R.layout.item_comment_righ, parent, false));

        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolder0 viewHolder0 = (ViewHolder0)holder;
                viewHolder0.mTxtComment.setText(mData.get(position).getComment());
                viewHolder0.mTxtName.setText(mData.get(position).getName());
                viewHolder0.mTxtDate.setText(mData.get(position).getDate_time());

                break;

            case 2:
                ViewHolder2 viewHolder2 = (ViewHolder2)holder;
                viewHolder2.mTxtComment.setText(mData.get(position).getComment());
                viewHolder2.mTxtName.setText(mData.get(position).getName());
                viewHolder2.mTxtDate.setText(mData.get(position).getDate_time());

                break;
        }
    }
}