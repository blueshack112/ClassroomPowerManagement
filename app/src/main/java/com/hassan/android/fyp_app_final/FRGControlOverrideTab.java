package com.hassan.android.fyp_app_final;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
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
    private String[] slots;
    private NumberPicker lengthNumberPicker;
    private int lengthAllowed;
    private Button openAllButton, closeAllButton;
    private Switch[] switches;
    private Button submitButton;
    private boolean okayToSubmit;

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
        final View view = inflater.inflate(R.layout.fragment_frgcontrol_override_tab, container, false);

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

        //Setting max and min allowed dates to override
        if (myCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            allowedDate.add(Calendar.DATE, 2);
            minAllowedDate = allowedDate.getTimeInMillis();
        } else if (myCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            allowedDate.add(Calendar.DATE, 1);
            minAllowedDate = allowedDate.getTimeInMillis();
        }
        minAllowedDate = allowedDate.getTimeInMillis();
        daysToAdd = 6 - allowedDate.get(Calendar.DAY_OF_WEEK);
        allowedDate.add(Calendar.DATE, daysToAdd);
        maxAllowedDate = allowedDate.getTimeInMillis();

        //Setting allowed dates
        datePickerDialog.getDatePicker().setMinDate(minAllowedDate);
        datePickerDialog.getDatePicker().setMaxDate(maxAllowedDate);
        //Slot slection setup
        slotSelectionSpinner = view.findViewById(R.id.override_spinner_select_slot);
        slots = getContext().getResources().getStringArray(R.array.Slots);
        slotSelectionSpinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, slots);
        slotSelectionSpinner.setAdapter(slotSelectionSpinnerAdapter);

        //Length selection setup
        lengthAllowed = 8 - slotSelectionSpinner.getSelectedItemPosition() + 1;
        lengthNumberPicker = view.findViewById(R.id.override_schedule_length);
        lengthNumberPicker.setMinValue(1);
        lengthNumberPicker.setMaxValue(lengthAllowed);

        //OpenAll button and CloseAll button functionality
        switches = new Switch[5];
        switches[0] = view.findViewById(R.id.override_switch_relay_1);
        switches[1] = view.findViewById(R.id.override_switch_relay_2);
        switches[2] = view.findViewById(R.id.override_switch_relay_3);
        switches[3] = view.findViewById(R.id.override_switch_relay_4);
        switches[4] = view.findViewById(R.id.override_switch_relay_5);
        openAllButton = view.findViewById(R.id.override_bt_open_all);
        closeAllButton = view.findViewById(R.id.override_bt_close_all);
        openAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i <= 4; i++)
                    switches[i].setChecked(true);
            }
        });
        closeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i <= 4; i++)
                    switches[i].setChecked(false);
            }
        });

        //Submit button functionality
        submitButton = view.findViewById(R.id.override_bt_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okayToSubmit = false;
                if (scheduleSelection.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Please select a schedule type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (scheduleSelection.getCheckedRadioButtonId() == R.id.override_rb_later) {
                    if (dateSelector.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Please select the date for schedule", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                okayToSubmit = true;
                if (okayToSubmit) {
                    String msg = "";
                    RadioButton rb = view.findViewById(scheduleSelection.getCheckedRadioButtonId());
                    msg += roomSelectionSpinner.getSelectedItem().toString() + " | ";
                    msg += rb.getText() + " | ";
                    if (rb.getId() == R.id.override_rb_later) {
                        msg += dateSelector.getText() + " | ";
                        msg += slotSelectionSpinner.getSelectedItem().toString() + " | ";
                        msg += Integer.toString(lengthNumberPicker.getValue()) + " | ";
                    }
                    if (switches[0].isChecked())
                        msg += "ON ";
                    else
                        msg += "OFF ";
                    if (switches[1].isChecked())
                        msg += "ON ";
                    else
                        msg += "OFF ";
                    if (switches[2].isChecked())
                        msg += "ON ";
                    else
                        msg += "OFF ";
                    if (switches[3].isChecked())
                        msg += "ON ";
                    else
                        msg += "OFF ";
                    if (switches[4].isChecked())
                        msg += "ON.";
                    else
                        msg += "OFF.";

                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }

            }
        });


        slotSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lengthAllowed = 8 - slotSelectionSpinner.getSelectedItemPosition();
                lengthNumberPicker.setMinValue(1);
                lengthNumberPicker.setMaxValue(lengthAllowed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    public void upDateDateSelector() {
        SimpleDateFormat format = new SimpleDateFormat("mm/dd/yy", Locale.US);
        dateSelector.setText(format.format(myCalendar.getTime()));
    }
}
