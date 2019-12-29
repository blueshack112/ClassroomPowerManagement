package com.hassan.android.fyp_app_final;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FRGPendingRequestTab extends Fragment {

    private RecyclerView                  recycler;
    private PendingRequestRecyclerAdapter adapter;

    public FRGPendingRequestTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frgpending_request_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recycler = view.findViewById(R.id.pending_requests_recycler_list);
        adapter = new PendingRequestRecyclerAdapter(getActivity());
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);
    }

}
