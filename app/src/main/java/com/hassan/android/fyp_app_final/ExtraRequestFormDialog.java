package com.hassan.android.fyp_app_final;

/**
 * !!HOW THE EXTRA REQUEST FORM DIALOG IS GOING TO WORK!!
 * - Course Selection Spinner:
 * - The course selector will be loaded up first (from the database) with all the courses taught by the teacher
 * - The Teacher will be able to select the course
 * <p>
 * - Reuest Type Radio Group:
 * - The request type will decide what to show and what not to show
 * - Choosing "Extra Class" will show all the options
 * - Choosing "Cancel Class" will only show the following options:
 * 1. Class Selection Spinner (See description below)
 * 2. General Reason Selection Spinner (See description below)
 * 3. Message TextView (See description below)
 * 4. Submit Button (See description below)
 * <p>
 * - Room Selection Spinner:
 * - The room selector will be allotted with all the available rooms in the array present in Strings.xml
 * <p>
 * - Dat Selection Spinner:
 * - The day selector will be allotted with days Monday to Friday
 * - When the request is made, the day of week string will be converted to appropriate numbers
 * - Only the remaining days in the week will be shown. For instance, if today is tuesday, then the spinner will show days from Wednesday to Friday
 * - The current day cannot be selected for extra class since it will be too late to handle for the HOD
 * - Since extra request can only be made for the current week, there is no date selector. This means that the date will be calculated programmatically by the app
 * <p>
 * - Slot Selection Spinner
 * - The slot selection spinner will be allotted with 1-7 (slots available each day)
 * <p>
 * - Length Number Picker:
 * - The length number selector will have three numbers initially, 1, 2, and 3.
 * - If the selected slot is close to break or end of day, then the max possible length will decrease accordingly. For instance, if the slot is 3, lengths 1 and 2 will b show as break occurs after 4th session and class can't continue to the 3rd hour.
 * <p>
 * - Class Selection Spinner:
 * - The class selection spinner is for cancelling a class
 * - It will be allotted by the classes that the teacher has this week (in week schedule table)
 * - If the class has been occurred, it will be removed from the list as the classes that are already conducted can't be cancelled. This will be achieved by comparing the current day of week to the day of week of the course
 * <p>
 * - General Reason Spinner:
 * - An array of general reasons will be hard coded inside the the Strings.xml file
 * - This will be for the HOD's extra request panel
 * <p>
 * - Message TextView:
 * - The message text box will be where the detailed reason can be entered by the user.
 * - It will be optional
 * <p>
 * - Send Button:
 * - The send button is where all the verifications will be made the API to load the data in the extra_schedule_table will be added.
 * TODO: add cancel button option in case the user doesn't want. See if the cancel button can be set itself
 * TODO-Continued: have added negative button listener check to see if it works properly.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.Arrays;


public class ExtraRequestFormDialog extends DialogFragment {

    private View formView;

    private RadioGroup requestTypeSelectionRGroup;

    private ConstraintLayout extraClassScheduleConstraint;
    private ConstraintLayout classesSelectionConstraint;

    private Spinner roomSelectionSpinner;
    private SelectionSpinnerAdapter roomSelectionSpinnerAdapter;

    private Spinner courseSelectionSpinner;
    private SelectionSpinnerAdapter courseSelectionSpinnerAdapter;

    private Spinner daySelectionSpinner;
    private SelectionSpinnerAdapter daySelectionSpinnerAdapter;

    private Spinner slotSelectionSpinner;
    private SelectionSpinnerAdapter slotSelectionSpinnerAdapter;

    private Spinner classesSelectionSpinner;
    private SelectionSpinnerAdapter classesSelectionSpinnerAdapter;

    private Spinner generalReasonSelectionSpinner;
    private SelectionSpinnerAdapter generalReasonSelectionSpinnerAdapter;

    private NumberPicker lengthSelectionNumberPicker;


    private EditText messageEditText;
    private Button submitbutton;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.extra_request_form_title);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        formView = inflater.inflate(R.layout.dialog_extra_request_form, null);

        //Creating functionality for dialog objects
        setupViews();

        return super.onCreateDialog(savedInstanceState);
    }

    public void setupViews() {
        // Assign layout ids to the corresponding element so that it can be manipulated
        courseSelectionSpinner = formView.findViewById(R.id.extra_request_course_spinner);
        requestTypeSelectionRGroup = formView.findViewById(R.id.extra_request_type_radio_group);
        extraClassScheduleConstraint = formView.findViewById(R.id.extra_request_class_schedule_constraint_layout);
        roomSelectionSpinner = formView.findViewById(R.id.extra_request_room_spinner);
        daySelectionSpinner = formView.findViewById(R.id.extra_request_day_spinner);
        slotSelectionSpinner = formView.findViewById(R.id.extra_request_slot_spinner);
        lengthSelectionNumberPicker = formView.findViewById(R.id.extra_request_length_number_picker);
        classesSelectionConstraint = formView.findViewById(R.id.extra_request_cancel_class_constraint_layout);
        classesSelectionSpinner = formView.findViewById(R.id.extra_request_class_spinner);
        generalReasonSelectionSpinner = formView.findViewById(R.id.extra_request_general_reason_spinner);
        messageEditText = formView.findViewById(R.id.extra_request_general_reason_spinner);
        submitbutton = formView.findViewById(R.id.extra_request_form_submit);

        //Create functionality for elements
        setupCourseSelectionSpinner();
        setupRequestTypeSelectionRGroup();
        setupRoomSelectionSpinner();
        setupDaySelectionSpinner();

    }


    /**
     * Function that will assign the adapter and data to the spinner
     * The data will be loaded form the database
     */
    public void setupRoomSelectionSpinner() {
        roomSelectionSpinnerAdapter = new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        roomSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSelectionSpinner.setAdapter(roomSelectionSpinnerAdapter);
    }

    /**
     * Function that will setup te functionality of the request type selection section.
     * THe function will turn different constraint groups visible or invisible based on the option that was selected.
     */
    public void setupRequestTypeSelectionRGroup() {
        // Create the listener for change in radio button group
        requestTypeSelectionRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // If extra class button is selected...
                if (checkedId == R.id.extra_request_rb_extra_class) {
                    // Set the class schedule constraint to visible and the cancel class constraint to invisible
                    extraClassScheduleConstraint.setVisibility(View.VISIBLE);
                    extraClassScheduleConstraint.setActivated(true);
                    classesSelectionConstraint.setVisibility(View.GONE);
                    classesSelectionConstraint.setActivated(false);
                } else if (checkedId == R.id.extra_request_rb_cancel_class) {
                    // Or do vice versa if cancel class button was selected
                    extraClassScheduleConstraint.setVisibility(View.GONE);
                    extraClassScheduleConstraint.setActivated(false);
                    classesSelectionConstraint.setVisibility(View.VISIBLE);
                    classesSelectionConstraint.setActivated(true);
                }
            }
        });

        // Set the default button (cancel class button) to checked when the form is loaded.
        RadioButton defaultButton = formView.findViewById(R.id.extra_request_rb_cancel_class);
        defaultButton.setChecked(true);
    }

    /**
     * Function that loads up the data for the course course selection spinner
     * TODO: add code to select data from the database
     */
    public void setupCourseSelectionSpinner() {
        String[] data = {"AB", "CD"};
        //TODO: code to get data from the database
        courseSelectionSpinnerAdapter = new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, data, getContext().getResources().getString(R.string.default_text_course_spinner));
        courseSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSelectionSpinner.setAdapter(courseSelectionSpinnerAdapter);
    }

    /**
     * Function to load the available days in the day selection spinner
     */
    public void setupDaySelectionSpinner() {
        // The array of all available days. This will be sliced later based on the current day
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        // Calculate the point from where the days should be shown
        // Note that Friday cannot be shown alone. Because on Friday, there is only one day left and since we cannot select the same day for extra class, no one can request for extra class on friday. So we will show something like "Sorry, no days available for extra class this week".
        // TODO: Check how the current day is coming (as an index or not. if not, adjust so that monday is 0 and friday is 4) and make adjustments if needed
        int currentDay = MainActivity.getCurrentDayOfWeek();

        // if today is friday...
        if (currentDay == 4) {
            // Initialize the array as the error message
            // TODO: make sure that the submit button checks for this error and doesnt let the request go if today is friday
            daySelectionSpinnerAdapter = new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, getContext().getResources().getStringArray(R.array.no_days_available_array), getContext().getResources().getString(R.string.default_text_no_days_available));
            return;
        }

        // Slice the days array from the day after current day all the way to friday
        // TODO:Continue from here
    }
}
