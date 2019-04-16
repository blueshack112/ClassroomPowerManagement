package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class FRGRoomStatusTab extends Fragment {
    private Context context;
    private Spinner roomSpinner;
    private Button goButton;
    public FRGRoomStatusTab () {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frgroom_status_tab, container, false);
        context = container.getContext();
        RoomSelectionSpinnerAdapter spinnerAdapter = new RoomSelectionSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item);
        roomSpinner = view.findViewById(R.id.spinner_select_room);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(spinnerAdapter);
        goButton = view.findViewById(R.id.button_room_selected);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selection = roomSpinner.getSelectedItem().toString();
                Toast.makeText(context, selection, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
