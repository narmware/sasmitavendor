package com.narmware.samista.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.narmware.samista.MyApplication;
import com.narmware.samista.R;
import com.narmware.samista.activity.HomeActivity;
import com.narmware.samista.activity.LoginActivity;
import com.narmware.samista.adapter.LeadAdapter;
import com.narmware.samista.pojo.Lead;
import com.narmware.samista.pojo.LeadResponse;
import com.narmware.samista.pojo.Login2;
import com.narmware.samista.support.Constants;
import com.narmware.samista.support.Endpoints;
import com.narmware.samista.support.SharedPreferencesHelper;
import com.narmware.samista.support.SupportFunctions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LiveLeadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiveLeadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveLeadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    @BindView(R.id.recycler_lead) protected RecyclerView mRecyclerLead;
    ArrayList<Lead> leads;
    LeadAdapter leadAdapter;
    Dialog mNoConnectionDialog;
    RequestQueue mVolleyRequest;
    View view;

    //empty data layout
    public static LinearLayout mEmptyLinear;
    @BindView(R.id.txt_no_data)
    TextView mTxtNoData;
    @BindView(R.id.img_no_data)
    ImageView mImgNoData;

    public LiveLeadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LiveLeadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LiveLeadFragment newInstance(String param1, String param2) {
        LiveLeadFragment fragment = new LiveLeadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_live_lead, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        ButterKnife.bind(this,view);
        setLeadRecycler(new LinearLayoutManager(getContext()));
        mVolleyRequest = Volley.newRequestQueue(getContext());
        mNoConnectionDialog = new Dialog(getContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        mEmptyLinear=view.findViewById(R.id.empty_linear);
        mTxtNoData.setText("No active leads yet");
        mImgNoData.setImageResource(R.drawable.ic_working_team);

        GetLiveLeads();
    }

    public void setLeadRecycler(RecyclerView.LayoutManager mLayoutManager){
        leads=new ArrayList<>();

        //leads.add(new Lead("1234","Trading name","Vrushali varne","8928224217","Pune,Maharashtra"));
        leadAdapter = new LeadAdapter(getContext(),leads,Endpoints.OPEN);
        mRecyclerLead.setLayoutManager(mLayoutManager);
        mRecyclerLead.setItemAnimator(new DefaultItemAnimator());
        mRecyclerLead.setAdapter(leadAdapter);
        mRecyclerLead.setNestedScrollingEnabled(false);
        mRecyclerLead.setFocusable(false);

        leadAdapter.notifyDataSetChanged();

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void GetLiveLeads() {
        HashMap<String,String> param = new HashMap();
        param.put(Endpoints.USER_ID, SharedPreferencesHelper.getUserId(getContext()));

        String url= SupportFunctions.appendParam(Endpoints.GET_LIVE_LEADS_URL,param);
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage("Loading leads...");
        dialog.setTitle("Please wait");
        if(!dialog.isShowing()) dialog.show();
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {

                        try
                        {

                            Log.e("Open json",response.toString());
                            Gson gson=new Gson();
                            LeadResponse leadResponse=gson.fromJson(response.toString(),LeadResponse.class);
                            Lead[] lead=leadResponse.getData();
                            for (Lead item:lead)
                            {
                                leads.add(item);
                            }
                            leadAdapter.notifyDataSetChanged();

                            if(mNoConnectionDialog.isShowing()==true)
                                mNoConnectionDialog.dismiss();

                            if(dialog.isShowing()) dialog.dismiss();
                        } catch (Exception e) {

                            e.printStackTrace();
                            if(dialog.isShowing()) dialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        MyApplication.mt("Server not reachable", getContext());
                        showNoConnectionDialog();
                        if(dialog.isShowing()) dialog.dismiss();
                    }
                }
        );
        mVolleyRequest.add(obreq);
    }
    private void showNoConnectionDialog() {
        mNoConnectionDialog.setContentView(R.layout.dialog_no_internet);
        mNoConnectionDialog.setCancelable(false);
        mNoConnectionDialog.show();

        Button tryAgain = mNoConnectionDialog.findViewById(R.id.txt_retry);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    GetLiveLeads();
            }
        });
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }*/
}
