package com.narmware.samista.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.narmware.samista.R;
import com.narmware.samista.activity.DetailedLeadActivity;
import com.narmware.samista.pojo.Lead;

import java.util.ArrayList;

/**
 * Created by rohitsavant on 22/08/18.
 */

public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.LeadHolder> {
    private Context mContext;
    private ArrayList<Lead> mData;

    public LeadAdapter(Context mContext, ArrayList<Lead> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public LeadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_lead, parent, false);
        return new LeadHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadHolder holder, int position) {
        Lead lead = mData.get(position);
        holder.mItem = lead;

        //setting data onto card
        holder.leadId.setText(lead.getId());
        holder.tradingName.setText(lead.getTrading_name());
        holder.contactName.setText(lead.getContact_name());
        holder.contactNumber.setText(lead.getMobile_number());
        holder.address.setText(lead.getBusiness_address());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class LeadHolder extends RecyclerView.ViewHolder {
        TextView leadId, tradingName, contactName, contactNumber, address;
        Button mBtnMore;

        Lead mItem;

        public LeadHolder(View itemView) {
            super(itemView);
            leadId = itemView.findViewById(R.id.lead_id);
            tradingName = itemView.findViewById(R.id.trading_name);
            contactName = itemView.findViewById(R.id.contact_name);
            contactNumber = itemView.findViewById(R.id.contact_number);
            address = itemView.findViewById(R.id.address);
            mBtnMore=itemView.findViewById(R.id.lead_bt_more);

            mBtnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, DetailedLeadActivity.class);
                    intent.putExtra("Lead",mItem);
                    mContext.startActivity(intent);
                }
            });


        }
    }
}
