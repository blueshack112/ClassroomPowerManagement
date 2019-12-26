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
 * - Only the remaining days in the week will be shown. For instance, if today is tuesday, then the spinner will
 * show days from Wednesday to Friday
 * - The current day cannot be selected for extra class since it will be too late to handle for the HOD
 * - Since extra request can only be made for the current week, there is no date selector. This means that the
 * date will be calculated programmatically by the app
 * <p>
 * - Slot Selection Spinner
 * - The slot selection spinner will be allotted with 1-7 (slots available each day)
 * <p>
 * - Length Number Picker:
 * - The length number selector will have three numbers initially, 1, 2, and 3.
 * - If the selected slot is close to break or end of day, then the max possible length will decrease
 * accordingly. For instance, if the slot is 3, lengths 1 and 2 will b show as break occurs after 4th session
 * and class can't continue to the 3rd hour.
 * <p>
 * - Class Selection Spinner:
 * - The class selection spinner is for cancelling a class
 * - It will be allotted by the classes that the teacher has this week (in week schedule table)
 * - If the class has been occurred, it will be removed from the list as the classes that are already conducted
 * can't be cancelled. This will be achieved by comparing the current day of week to the day of week of the course
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
 * - The send button is where all the verifications will be made the API to load the data in the
 * extra_schedule_table will be added.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ExtraRequestFormDialog extends DialogFragment {

    // Views
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

    // Data
    private String userID;
    private ArrayList<String> courseIDs;
    private ArrayList<String> courseNames;
    private ArrayList<String> courseTimes;


    /**
     * This function is how the activity will send arguments to the object.
     * Mainly, we need this function to get the userID from the outside activity.
     *
     * @param args: Bundle: must contain a string by the name "userID"
     */
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        userID = args.getString("userID", "None");
        courseIDs = args.getStringArrayList("courseIDs");
        courseNames = new ArrayList<>();
        courseTimes = new ArrayList<>();

        ArrayList<String> courseNamesTemp = args.getStringArrayList("courseNames");
        ArrayList<String> courseTimesTemp = args.getStringArrayList("courseTimes");

        courseNames.add(courseIDs.get(0) + " | " + courseNamesTemp.get(0));
        for (int i = 0; i < courseIDs.size(); i++) {
            for (int j = 0; j < courseNames.size(); j++) {
                // Remove duplication of names
                if (courseNames.get(j).endsWith(courseNamesTemp.get(i))) {
                    break;
                } else if (j == courseNames.size() - 1) {
                    courseNames.add(courseIDs.get(i) + " | " + courseNamesTemp.get(i));
                }
            }
            courseTimes.add(courseIDs.get(i) + " | " + courseTimesTemp.get(i));
        }
        Log.d("debugdialog", courseNames.toString());
        Log.d("debugdialog", courseIDs.toString());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        formView = inflater.inflate(R.layout.dialog_extra_request_form, null);

        //Creating functionality for dialog objects
        setupViews();

        // Set the builder's view to your view that has been setup now
        builder.setView(formView);

        // Set null positive button and create dialog
        builder.setPositiveButton("SUBMIT", null);
        AlertDialog returnDialog = builder.create();

        //Manually override dialog's positive button
        returnDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitRequest(dialog, getContext());
                    }
                });
            }
        });

        String debugMessage = "a\n";
        for (int i = 0; i < courseNames.size(); i++) {
            debugMessage += "Name: " + courseNames.get(i) + "   ID: " + courseIDs.get(i) + "   Day/Slot/Length: " +
                            courseTimes.get(i) + "\n";
        }
        Log.d("extraDebug", debugMessage);

        return returnDialog;
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
    }

    /**
     * Function that will assign the adapter and data to the spinner
     * The data will be loaded form the database
     */
    public void setupRoomSelectionSpinner() {
        roomSelectionSpinnerAdapter =
                new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        roomSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSelectionSpinner.setAdapter(roomSelectionSpinnerAdapter);
    }

    /**
     * Function that will setup te functionality of the request type selection section.
     * THe function will turn different constraint groups visible or invisible based on the option that was
     * selected.
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

                    // Re initializing the spinner data
                    ArrayList<String> data = new ArrayList<>();
                    data.addAll(Arrays.asList(getContext().getResources().getStringArray(
                            R.array.general_reason_spinner_extra_class)));
                    generalReasonSelectionSpinnerAdapter.clear();
                    generalReasonSelectionSpinnerAdapter.addAll(data);
                    generalReasonSelectionSpinnerAdapter.notifyDataSetChanged();
                } else if (checkedId == R.id.extra_request_rb_cancel_class) {
                    // Or do vice versa if cancel class button was selected
                    extraClassScheduleConstraint.setVisibility(View.GONE);
                    extraClassScheduleConstraint.setActivated(false);
                    classesSelectionConstraint.setVisibility(View.VISIBLE);
                    classesSelectionConstraint.setActivated(true);

                    // Re initializing the spinner data
                    ArrayList<String> data = new ArrayList<>();
                    data.addAll(Arrays.asList(getContext().getResources().getStringArray(
                            R.array.general_reason_spinner_cancel_class)));
                    generalReasonSelectionSpinnerAdapter.clear();
                    generalReasonSelectionSpinnerAdapter.addAll(data);
                    generalReasonSelectionSpinnerAdapter.notifyDataSetChanged();
                }
            }
        });

        // Set the default button (cancel class button) to checked when the form is loaded.
        RadioButton defaultButton = formView.findViewById(R.id.extra_request_rb_cancel_class);
        defaultButton.setChecked(true);
    }

    /**
     * Function that loads up the data for the course course selection spinner
     */
    public void setupCourseSelectionSpinner() {
        courseSelectionSpinnerAdapter =
                new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                                            courseNames, getContext().getResources().getString(
                        R.string.default_text_course_spinner));
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
        // Note that Friday cannot be shown alone. Because on Friday, there is only one day left and since we
        // cannot select the same day for extra class, no one can request for extra class on friday. So we will
        // show something like "Sorry, no days available for extra class this week".
        int currentDay = MainActivity.getCurrentDayOfWeekAsIndex();

        // if today is friday...
        if (currentDay == 4 || currentDay == 5 || currentDay == 6) {
            // Initialize the array as the error message
            daySelectionSpinnerAdapter =
                    new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                                                getContext().getResources()
                                                            .getStringArray(R.array.no_days_available_array),
                                                getContext().getResources()
                                                            .getString(R.string.default_text_no_days_available));
            daySelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            daySelectionSpinner.setAdapter(daySelectionSpinnerAdapter);
            return;
        }

        // Slice the days array from the day after current day all the way to friday
        String[] finalDays = Arrays.copyOfRange(days, currentDay + 1, 5);
        daySelectionSpinnerAdapter =
                new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                                            finalDays, finalDays[0]);
        daySelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySelectionSpinner.setAdapter(daySelectionSpinnerAdapter);
    }

    /**
     * Function that will assign 1-7 to the slot selection spinner
     */
    public void setupSlotSelectionSpinner() {
        String[] slots = {"1", "2", "3", "4", "5", "6", "7"};
        slotSelectionSpinnerAdapter =
                new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, slots,
                                            slots[0]);
        slotSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slotSelectionSpinner.setAdapter(slotSelectionSpinnerAdapter);
    }

    /**
     * Function that will calculate the slot selected by the user and alot the numbers 1,2, and 3 appropriately
     * This function will also setup the slot selection spinner's on change function. The function is for length
     * picker but since length picker's functionality depends on it, we are setting it up here
     */
    public void setupLengthNumberPicker() {
        int length = getAvailableLength();

        // set minimum value of the number picker as 1
        lengthSelectionNumberPicker.setMinValue(1);

        // set maximum value of the number picker as the length variable
        lengthSelectionNumberPicker.setMaxValue(length);

        // Now, setup slot selection spinner's onchange method so that the number picker updates itself
        // automatically when a new slot is chosen
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
     * 1 is added to the answer because the getSelectedItemPosition returns an index (0-6) while we need real
     * numbers (1-7)
     *
     * @return
     */
    public int getSelectedSlot() {
        return slotSelectionSpinner.getSelectedItemPosition() + 1;
    }

    /**
     * Function that will calculate how long can a class be based on the slot chosen by the user.
     *
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
     *
     * @param chosenSlot: the slot against which the length needs to be calculated
     */
    public int getAvailableLength(int chosenSlot) {
        chosenSlot++;
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
     * Function that will bring the scheduled classes of a user from the database's week schedule table and alot
     * then in the class selection spinner
     * Note that this function will only bring classes of the course that is selected by the user
     * This function will also setup the on change listener of the course selector.
     * TODO: what if user wanted to schedule a cancelled class?
     */
    public void setupClassSelectionSpinner() {
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < courseIDs.size(); i++) {
            if (courseNames.get(0).startsWith(courseIDs.get(i))) {
                data.add(courseTimes.get(i));
            }
        }
        classSelectionSpinnerAdapter =
                new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, data,
                                            getContext().getResources()
                                                        .getString(R.string.default_text_class_spinner));
        classSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSelectionSpinner.setAdapter(classSelectionSpinnerAdapter);

        // On change listener of the course selector
        courseSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> data = new ArrayList<>();
                for (int i = 0; i < courseIDs.size(); i++) {
                    if (courseNames.get(position).startsWith(courseIDs.get(i))) {
                        data.add(courseTimes.get(i));
                    }
                }
                classSelectionSpinnerAdapter.clear();
                classSelectionSpinnerAdapter.addAll(data);
                classSelectionSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                courseSelectionSpinner.setSelection(0);
            }
        });
    }

    /**
     * Function that will alot the data into the general reason spinner
     */
    public void setupGeneralReasonSpinner() {
        // Setup with cancel class adapter
        // This will change as the request type changes
        ArrayList<String> data = new ArrayList<>();
        data.addAll(Arrays.asList(
                getContext().getResources().getStringArray(R.array.general_reason_spinner_cancel_class)));
        generalReasonSelectionSpinnerAdapter =
                new SelectionSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, data,
                                            getContext().getResources()
                                                        .getString(R.string.default_text_general_reason_spinner));
        generalReasonSelectionSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generalReasonSelectionSpinner.setAdapter(generalReasonSelectionSpinnerAdapter);
    }

    /**
     * Function that will setup the main message text field. Basically it just adds a placeholder there
     */
    public void setupMessageEditText() {
        messageEditText.setHint(getContext().getResources().getString(R.string.default_text_message_editbox));
    }

    /**
     * This is where all the necessary validations will be done and the API will be called and data will be send
     * The API will check if the date and slot that is being asked by the user is available or not all other
     * verifications will be done here
     */
    public boolean submitRequest(final DialogInterface dialogFragment, final Context context) {
        // Get the course name
        final String course = courseSelectionSpinner.getSelectedItem().toString();
        // Check if the selected item is default or not
        if (course.equals(getContext().getResources().getString(R.string.default_text_course_spinner))) {
            Toast.makeText(getContext(), "Please select a course!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get request type
        final int requestType = requestTypeSelectionRGroup.getCheckedRadioButtonId();

        // Get the request general reason
        final String generalReason = generalReasonSelectionSpinner.getSelectedItem().toString();

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
            final String type = "CANCEL";

            // Get the class that the user wants to cancel
            chosenClass = classSelectionSpinner.getSelectedItem().toString();
            if (chosenClass.equals(getContext().getResources().getString(R.string.default_text_class_spinner))) {
                Toast.makeText(getContext(), "Please select the class that you want to cancel!",
                               Toast.LENGTH_SHORT).show();
                return false;
            }

            // ChosenClass original format: 1001 | Monday | 1st slot | 2 sessions
            // Get course id
            final int courseID = Integer.parseInt(chosenClass.substring(0, 4));

            // Get day
            chosenClass = chosenClass.substring(7);
            String day = chosenClass.substring(0, chosenClass.indexOf(" | "));
            // Convert day into respective integer
            if (day.equals("Monday")) {
                dayOfWeek = 1;
            } else if (day.equals("Tuesday")) {
                dayOfWeek = 2;
            } else if (day.equals("Wednesday")) {
                dayOfWeek = 3;
            } else if (day.equals("Thursday")) {
                dayOfWeek = 4;
            } else if (day.equals("Friday")) {
                dayOfWeek = 5;
            }

            // Get slot
            chosenClass = chosenClass.substring(chosenClass.indexOf("|") + 2);
            slot = Integer.parseInt(Character.toString(chosenClass.charAt(0)));

            // Get class length
            chosenClass = chosenClass.substring(chosenClass.indexOf("|") + 2);
            length = Integer.parseInt(Character.toString(chosenClass.charAt(0)));

            //Start calling extraRequest.php and check if the reuest could be submitted
            // URL of the API
            String url = "http://" + MainActivity.URL + "/AreebaFYP/extraRequest.php";

            //Setting up response handler
            Response.Listener listener = new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    try {
                        // Converting the response into JSON object
                        JSONObject extraResponse = new JSONObject(response.toString());

                        // If even a single course item arrived in the response
                        if (extraResponse.getBoolean("successful")) {
                            Toast.makeText(getContext(), "Your cancel request has been successfully submitted!",
                                           Toast.LENGTH_SHORT).show();
                            dialogFragment.dismiss();
                            return;
                        } else {
                            String error = extraResponse.getString("errorCode");
                            if (error.equals("notexist")) {
                                Toast.makeText(context, "The class you requested to cancel does not exist!",
                                               Toast.LENGTH_SHORT).show();
                                return;
                            } else if (error.equals("alreadycancelled")) {
                                Toast.makeText(context, "The same request has been submitted earlier!",
                                               Toast.LENGTH_SHORT).show();
                                return;
                            } else if (error.equals("unknown")) {
                                Toast.makeText(context,
                                               "Could not submit your request at this time. Try again later...",
                                               Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("XXXXXXXXXXXXXXXXXX", error.getLocalizedMessage());
                }
            };

            //Initialize request string with POST method
            final int finalDayOfWeek = dayOfWeek;
            final int finalSlot = slot;
            final int finalLength = length;
            final String finalMessage = message;
            StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    // Put request details in the data set
                    param.put("userID", userID);
                    param.put("courseID", Integer.toString(courseID));
                    param.put("dayOfWeek", Integer.toString(finalDayOfWeek));
                    param.put("slot", Integer.toString(finalSlot));
                    param.put("length", Integer.toString(finalLength));
                    param.put("requestType", type);
                    param.put("generalReason", generalReason);
                    param.put("message", finalMessage);
                    return param;
                }
            };

            //Execute request
            Volleyton.getInstance(getContext()).addToRequestQueue(request);

        } else {
            final String type = "EXTRA";

            // Get the room id
            final String roomSelection = roomSelectionSpinner.getSelectedItem().toString();
            char roomChar = roomSelection.charAt(roomSelection.length() - 1);
            room = Integer.toString(1000 + (roomChar - 64));

            // Get the class that the user wants to request extra of
            chosenClass = classSelectionSpinner.getSelectedItem().toString();
            if (chosenClass.equals(getContext().getResources().getString(R.string.default_text_class_spinner))) {
                Toast.makeText(getContext(), "Please select the class that you want to cancel!",
                               Toast.LENGTH_SHORT).show();
                return false;
            }

            // ChosenClass original format: 1001 | Monday | 1st slot | 2 sessions
            // Get course id
            final int courseID = Integer.parseInt(chosenClass.substring(0, 4));

            // Calculate the day that was selected in this pattern: Moday-1, Friday-5
            String day = daySelectionSpinner.getSelectedItem().toString();
            if (day.equals("Monday")) {
                dayOfWeek = 1;
            } else if (day.equals("Tuesday")) {
                dayOfWeek = 2;
            } else if (day.equals("Wednesday")) {
                dayOfWeek = 3;
            } else if (day.equals("Thursday")) {
                dayOfWeek = 4;
            } else if (day.equals("Friday")) {
                dayOfWeek = 5;
            }

            // Check if today is Friday
            if (MainActivity.getCurrentDayOfWeekAsIndex() == 4) {
                Toast.makeText(getContext(), "Today is Friday. You cannot request for extra classes on a Friday.",
                               Toast.LENGTH_SHORT).show();
                return false;
            }

            // The slots are allotted from 1-7. So we can get item position and add 1 to it.
            slot = slotSelectionSpinner.getSelectedItemPosition() + 1;

            // Get the length of the class
            length = lengthSelectionNumberPicker.getValue();

            //Start calling extraRequest.php and check if the reuest could be submitted
            // URL of the API
            String url = "http://" + MainActivity.URL + "/AreebaFYP/extraRequest.php";
            //Setting up response handler
            Response.Listener listener = new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    try {
                        // Converting the response into JSON object
                        JSONObject extraResponse = new JSONObject(response.toString());

                        // If even a single course item arrived in the response
                        if (extraResponse.getBoolean("successful")) {
                            Toast.makeText(getContext(), "Your extra request has been successfully submitted!",
                                           Toast.LENGTH_SHORT).show();
                            dialogFragment.dismiss();
                            return;
                        } else {
                            String error = extraResponse.getString("errorCode");
                            if (error.equals("slotbusy")) {
                                Toast.makeText(context, "The time you selected was not available!",
                                               Toast.LENGTH_SHORT).show();
                                return;
                            } else if (error.equals("alreadyrequested")) {
                                Toast.makeText(context, "The same request has been submitted earlier!",
                                               Toast.LENGTH_SHORT).show();
                                return;
                            } else if (error.equals("unknown")) {
                                Toast.makeText(context,
                                               "Could not submit your request at this time. Try again later...",
                                               Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("XXXXXXXXXXXXXXXXXX", error.getLocalizedMessage());
                }
            };

            //Initialize request string with POST method
            final int finalDayOfWeek = dayOfWeek;
            final int finalSlot = slot;
            final int finalLength = length;
            final String finalMessage = message;
            final String finalRoom = room;
            StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    // Put request details in the data set
                    param.put("userID", userID);
                    param.put("courseID", Integer.toString(courseID));
                    param.put("roomID", finalRoom);
                    param.put("dayOfWeek", Integer.toString(finalDayOfWeek));
                    param.put("slot", Integer.toString(finalSlot));
                    param.put("length", Integer.toString(finalLength));
                    param.put("requestType", type);
                    param.put("generalReason", generalReason);
                    param.put("message", finalMessage);
                    return param;
                }
            };
            //Execute request
            Volleyton.getInstance(getContext()).addToRequestQueue(request);
        }
        return true;
    }
}
