package com.hassan.android.fyp_app_final;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class ExtraRequestFormDialog extends DialogFragment {

    private View formView;
    private Spinner courseSelectionSpinner;
    private RadioGroup requestTypeSelectionRGroup;
    private ConstraintLayout extraClassScheduleConstraint;
    private Spinner roomSelectionSpinner;
    private RoomSelectionSpinnerAdapter roomSelectionSpinnerAdapter;
    private Spinner daySelectionSpinner;
    private Spinner slotSelectionSpinner;
    private NumberPicker lengthSelectionNumberPicker;
    private ConstraintLayout classesSelectionConstraint;
    private Spinner classesSelectionSpinner;
    private Spinner generalReasonSelectionSpinner;
    private EditText messageEditText;
    private Button submitbutton;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.extra_request_form_title);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        formView = inflater.inflate(R.layout.extra_request_form_dialog, null);

        //Creating functionality for dialog objects
        setupViews();


        return super.onCreateDialog(savedInstanceState);
    }
    public void setupViews () {
        courseSelectionSpinner= formView.findViewById(R.id.extra_request_course_spinner);
        requestTypeSelectionRGroup= formView.findViewById(R.id.extra_request_type_radio_group);
        extraClassScheduleConstraint= formView.findViewById(R.id.extra_request_class_schedule_constraint_layout);
        roomSelectionSpinner= formView.findViewById(R.id.extra_request_room_spinner);
        daySelectionSpinner= formView.findViewById(R.id.extra_request_day_spinner);
        slotSelectionSpinner= formView.findViewById(R.id.extra_request_slot_spinner);
        lengthSelectionNumberPicker= formView.findViewById(R.id.extra_request_length_number_picker);
        classesSelectionConstraint= formView.findViewById(R.id.extra_request_cancel_class_constraint_layout);
        classesSelectionSpinner= formView.findViewById(R.id.extra_request_class_spinner);
        generalReasonSelectionSpinner= formView.findViewById(R.id.extra_request_general_reason_spinner);
        messageEditText= formView.findViewById(R.id.extra_request_general_reason_spinner);
        submitbutton = formView.findViewById(R.id.extra_request_form_submit);

        //Create functionality for elements
        setupRoomSelectionSpinner();
        setupRequestTypeSelectionRGroup();
    }

    
    public void setupRoomSelectionSpinner () {

        roomSelectionSpinnerAdapter = new RoomSelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        roomSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSelectionSpinner.setAdapter(roomSelectionSpinnerAdapter);
    }

    public void setupRequestTypeSelectionRGroup () {
        requestTypeSelectionRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.extra_request_rb_extra_class) {
                    extraClassScheduleConstraint.setVisibility(View.VISIBLE);
                    extraClassScheduleConstraint.setActivated(true);
                    classesSelectionConstraint.setVisibility(View.GONE);
                    classesSelectionConstraint.setActivated(false);
                }
                else if (checkedId == R.id.extra_request_rb_cancel_class) {
                    extraClassScheduleConstraint.setVisibility(View.GONE);
                    extraClassScheduleConstraint.setActivated(false);
                    classesSelectionConstraint.setVisibility(View.VISIBLE);
                    classesSelectionConstraint.setActivated(true);
                }
            }
        });
    }

}
