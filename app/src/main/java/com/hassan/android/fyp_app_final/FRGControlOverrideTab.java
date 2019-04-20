package com.hassan.android.fyp_app_final;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class FRGControlOverrideTab extends Fragment {

    private Spinner roomSelectionSpinner;
    private RoomSelectionSpinnerAdapter roomSelectionSpinnerAdapter;
    private RadioGroup scheduleSelection;
    private ConstraintLayout scheduleConstraintGroup;
    private final Calendar myCalendar = Calendar.getInstance();
    private final Calendar allowedDate = Calendar.getInstance();
    private long maxAllowedDate;
    private long minAllowedDate;
    private int daysToAdd;
    private EditText dateSelector;
    private DatePickerDialog datePickerDialog;
    private Spinner slotSelectionSpinner;
    private ArrayAdapter<String> slotSelectionSpinnerAdapter;
    private String [] slots;

    //Date picker listener
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            upDateDateSelector();
        }
    };



    public FRGControlOverrideTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_frgcontrol_override_tab, container, false);

        //Room selection setup
        roomSelectionSpinnerAdapter = new RoomSelectionSpinnerAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        roomSelectionSpinner = view.findViewById(R.id.override_spinner_select_room);
        roomSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSelectionSpinner.setAdapter(roomSelectionSpinnerAdapter);

        //Schedule type selection setup
        scheduleSelection = view.findViewById(R.id.override_rb_shcedule);
        scheduleConstraintGroup = view.findViewById(R.id.override_schedule_constraint);

        //Schedule date selection setup
        dateSelector = view.findViewById(R.id.override_schedule_date);
        datePickerDialog = new DatePickerDialog(getActivity(), myDateListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        minAllowedDate = allowedDate.getTimeInMillis();
        daysToAdd = 7 - allowedDate.get(Calendar.DAY_OF_WEEK);
        allowedDate.add(Calendar.DATE, daysToAdd);
        maxAllowedDate = allowedDate.getTimeInMillis();
        datePickerDialog.getDatePicker().setMinDate(minAllowedDate);
        datePickerDialog.getDatePicker().setMaxDate(maxAllowedDate);

        //Slot slection setup
        slotSelectionSpinner = view.findViewById(R.id.override_spinner_select_slot);
        slots = getContext().getResources().getStringArray(R.array.Slots);
        slotSelectionSpinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, slots);
        slotSelectionSpinner.setAdapter(slotSelectionSpinnerAdapter);

        //Length selection setup








        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        scheduleSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.override_rb_later) {
                    scheduleConstraintGroup.setVisibility(View.VISIBLE);
                } else {
                    scheduleConstraintGroup.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }
    public void upDateDateSelector () {
        SimpleDateFormat format = new SimpleDateFormat("mm/dd/yy", Locale.US);
        dateSelector.setText(format.format(myCalendar.getTime()));
    }
}
