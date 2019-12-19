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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

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

    private Spinner classSelectionSpinner;
    private SelectionSpinnerAdapter classSelectionSpinnerAdapter;

    private Spinner generalReasonSelectionSpinner;
    private SelectionSpinnerAdapter generalReasonSelectionSpinnerAdapter;

    private NumberPicker lengthSelectionNumberPicker;

    private EditText messageEditText;
    private Button submitbutton;

    //TODO: get user ID here somehow

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

    /**
     * Fucntion that will assign all the ids to the UI objects and call the setup funciton of each UI object
     */
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
        classSelectionSpinner = formView.findViewById(R.id.extra_request_class_spinner);
        generalReasonSelectionSpinner = formView.findViewById(R.id.extra_request_general_reason_spinner);
        messageEditText = formView.findViewById(R.id.extra_request_message_edit);
        submitbutton = formView.findViewById(R.id.extra_request_form_submit);

        //Create functionality for elements
        setupCourseSelectionSpinner();
        setupGeneralReasonSpinner();
        setupRequestTypeSelectionRGroup();
        setupRoomSelectionSpinner();
        setupDaySelectionSpinner();
        setupSlotSelectionSpinner();
        setupLengthNumberPicker();
        setupClassSelectionSpinner();
        setupMessageEditText();
        setupSubmitButton();
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
                    generalReasonSelectionSpinnerAdapter.newOptions(getContext().getResources().getStringArray(R.array.general_reason_spinner_extra_class), getContext().getResources().getString(R.string.default_text_general_reason_spinner));
                } else if (checkedId == R.id.extra_request_rb_cancel_class) {
                    // Or do vice versa if cancel class button was selected
                    extraClassScheduleConstraint.setVisibility(View.GONE);
                    extraClassScheduleConstraint.setActivated(false);
                    classesSelectionConstraint.setVisibility(View.VISIBLE);
                    classesSelectionConstraint.setActivated(true);
                    generalReasonSelectionSpinnerAdapter.newOptions(getContext().getResources().getStringArray(R.array.general_reason_spinner_cancel_class), getContext().getResources().getString(R.string.default_text_general_reason_spinner));
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
        int currentDay = MainActivity.getCurrentDayOfWeek();

        // if today is friday...
        if (currentDay == 4) {
            // Initialize the array as the error message
            // TODO: make sure that the submit button checks for this error and doesnt let the request go if today is friday
            daySelectionSpinnerAdapter = new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, getContext().getResources().getStringArray(R.array.no_days_available_array), getContext().getResources().getString(R.string.default_text_no_days_available));
            daySelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            daySelectionSpinner.setAdapter(daySelectionSpinnerAdapter);
            return;
        }

        // Slice the days array from the day after current day all the way to friday
        String [] finalDays = Arrays.copyOfRange(days, currentDay+1, 4);
        daySelectionSpinnerAdapter = new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, finalDays, finalDays[0]);
        daySelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySelectionSpinner.setAdapter(daySelectionSpinnerAdapter);
    }

    /**
     * Function that will assign 1-7 to the slot selection spinner
     */
    public void setupSlotSelectionSpinner() {
        String [] slots = {"1", "2", "3", "4", "5", "6", "7"};
        slotSelectionSpinnerAdapter = new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, slots, slots[0]);
        slotSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slotSelectionSpinner.setAdapter(slotSelectionSpinnerAdapter);
    }

    /**
     * Function that will calculate the slot selected by the user and alot the numbers 1,2, and 3 appropriately
     * This function will also setup the slot selection spinner's on change function. The function is for length picker but since length picker's functionality depends on it, we are setting it up here
     */
    public void setupLengthNumberPicker() {
        int length = getAvailableLength();

        // set minimum value of the number picker as 1
        lengthSelectionNumberPicker.setMinValue(1);

        // set maximum value of the number picker as the length variable
        lengthSelectionNumberPicker.setMaxValue(length);

        // Now, setup slot selection spinner's onchange method so that the number picker updates itself automatically when a new slot is chosen
        slotSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int length = getAvailableLength(i);
                // set minimum value of the number picker as 1
                lengthSelectionNumberPicker.setMinValue(1);

                // set maximum value of the number picker as the length variable
                lengthSelectionNumberPicker.setMaxValue(length);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                slotSelectionSpinner.setSelection(0);
                int length = getAvailableLength(0);
                // set minimum value of the number picker as 1
                lengthSelectionNumberPicker.setMinValue(1);

                // set maximum value of the number picker as the length variable
                lengthSelectionNumberPicker.setMaxValue(length);
            }
        });
    }

    /**
     * Function that returns the slot that is currently selected by the slot selection spinner
     * 1 is added to the answer because the getSelectedItemPosition returns an index (0-6) while we need real numbers (1-7)
     * @return
     */
    public int getSelectedSlot() {
        return slotSelectionSpinner.getSelectedItemPosition()+1;
    }

    /**
     * Function that will calculate how long can a class be based on the slot chosen by the user.
     * @return int: length available to choose from. could be 1, 2 or 3
     */
    public int getAvailableLength() {
        int chosenSlot = getSelectedSlot();

        // if the slot is right before break or end of day...
        if (chosenSlot == 4 || chosenSlot == 7) {
            // Only one credit hour can be conducted
            return 1;
        }
        // if the slot is second last before a break or end of day...
        if (chosenSlot == 3 || chosenSlot == 6) {
            // Only two credit hours can be conducted
            return 2;
        }
        // If the above two conditions aren't true, then obviously a 3-hour class can be conducted
        return 3;
    }

    /**
     * Override of te same function, but the slot is provided here
     * @param chosenSlot: the slot against which the length needs to be calculated
     */
    public int getAvailableLength(int chosenSlot) {

        // if the slot is right before break or end of day...
        if (chosenSlot == 4 || chosenSlot == 7) {
            // Only one credit hour can be conducted
            return 1;
        }
        // if the slot is second last before a break or end of day...
        if (chosenSlot == 3 || chosenSlot == 6) {
            // Only two credit hours can be conducted
            return 2;
        }
        // If the above two conditions aren't true, then obviously a 3-hour class can be conducted
        return 3;
    }

    /**
     * Function that will bring the scheduled classes of a user from the database's week schedule table and alot then in the class selection spinner
     * Note that this function will only bring classes of the course that is selected by the user
     */
    // TODO: create functionality so that you can keep track of which class is being selected
    // TODO: Thing about creating a custom class where week_schedule table ids can be stored and the selected item position can be used. Think...
    public void setupClassSelectionSpinner() {
        String[] data = {"AB", "CD"};
        //TODO: code to get data from the database
        classSelectionSpinnerAdapter = new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, data, getContext().getResources().getString(R.string.default_text_class_spinner));
        classSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSelectionSpinner.setAdapter(classSelectionSpinnerAdapter);
    }

    /**
     * Function that will alot the data into the general reason spinner
     * TODO: Add functionality to change these reasons when the request type is changed
     */
    public void setupGeneralReasonSpinner() {
        // Setup with cancel class adapter
        // This will change as the request type changes
        generalReasonSelectionSpinnerAdapter = new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, getContext().getResources().getStringArray(R.array.general_reason_spinner_cancel_class), getContext().getResources().getString(R.string.default_text_general_reason_spinner));
        generalReasonSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generalReasonSelectionSpinner.setAdapter(generalReasonSelectionSpinnerAdapter);
    }

    /**
     * Function that will setup the main message text field. Basically it just adds a placeholder there
     */
    public void setupMessageEditText () {
        messageEditText.setHint(getContext().getResources().getString(R.string.default_text_message_editbox));
    }

    /**
     * This is where all the necessary validations will be done and the API will be called and data will be send
     * The API will check if the date and slot that is being asked by the user is available or not all other verifications will be done here
     */
    public void setupSubmitButton() {
        // Set user ID
        String userID = "";

        // Get the course name
        String course = courseSelectionSpinner.getSelectedItem().toString();
        // Check if the selected item is default or not
        if (course.equals(getContext().getResources().getString(R.string.default_text_course_spinner))) {
            Toast.makeText(getContext(), "Please select a course!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get request type
        int requestType = requestTypeSelectionRGroup.getCheckedRadioButtonId();

        // Get the request general reason
        String generalReason = generalReasonSelectionSpinner.getSelectedItem().toString();
        // Check if the general reason is set to default or not
        if (generalReason.equals(getContext().getResources().getString(R.string.default_text_general_reason_spinner))) {
            Toast.makeText(getContext(), "Please select a general reason!", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = messageEditText.getText().toString();
        // Check if the user didnt put anything in it
        if (message.isEmpty() || message == null) {
            message = "No message entered.";
        }

        String chosenClass = "";
        String room = "";
        int dayOfWeek = 1;
        int slot = 1;
        int length = 1;

        // If it is a cancel class request, deal accordingly
        // Else it is an extra class request, deal accordingly
        if (requestType == R.id.extra_request_rb_cancel_class) {
            // get the class that the user wants to cancel
            chosenClass = classSelectionSpinner.getSelectedItem().toString();
            if (chosenClass.equals(getContext().getResources().getString(R.string.default_text_class_spinner))){
                Toast.makeText(getContext(), "Please select the class that you want to cancel!", Toast.LENGTH_SHORT).show();
                return;
            }

            //TODO: send the data to the database
        }
        else {
            final String roomSelection = roomSelectionSpinner.getSelectedItem().toString();
            char roomChar = roomSelection.charAt(roomSelection.length()-1);
            room = Integer.toString(1000+(roomChar - 64));

            // Calculate the day that was selected in this pattern: Moday-1, Friday-5
            String day = daySelectionSpinner.getSelectedItem().toString();
            if (day.equals("Monday"))
                dayOfWeek = 1;
            else if (day.equals("Tuesday"))
                dayOfWeek = 2;
            else if (day.equals("Wednesday"))
                dayOfWeek = 3;
            else if (day.equals("Thursday"))
                dayOfWeek = 4;
            else if (day.equals("Friday"))
                dayOfWeek = 5;

            // The slots are alotted from 1-7. So we can get item position and add 1 to it.
            slot = slotSelectionSpinner.getSelectedItemPosition()+1;

            // Get the length of the class
            length = lengthSelectionNumberPicker.getValue();

            //TODO: send the data to the database

        }
    }
}
