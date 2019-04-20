package com.hassan.android.fyp_app_final;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FRGControlOverrideTab extends Fragment {

    public FRGControlOverrideTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_frgcontrol_override_tab, container, false);

        return view;
    }

}
