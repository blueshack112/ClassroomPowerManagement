package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "StringConcatenationInLoop", "ConstantConditions"})
public class FRGControlOverrideTab extends Fragment {

    // Views
    private View view;

    // Spinners
    private Spinner                 roomSelectionSpinner;
    private SelectionSpinnerAdapter roomSelectionSpinnerAdapter;

    private Spinner                 daySelectionSpinner;
    private SelectionSpinnerAdapter daySelectionSpinnerAdapter;

    private Spinner                 slotSelectionSpinner;
    private SelectionSpinnerAdapter slotSelectionSpinnerAdapter;

    // Radio groups and constraints
    private RadioGroup       scheduleSelectionRadioGroup;
    private ConstraintLayout scheduleSelectionConstraint;

    // Number Pickers
    private int          lengthAllowed;
    private NumberPicker lengthSelectionNumberPicker;

    // Buttons
    private Button openAllButton, closeAllButton;
    private Button submitButton;

    // Data
    private ArrayList<Switch> switches;
    private String            userID;
    private Context           context;

    /**
     * Empty constructor is required
     */
    public FRGControlOverrideTab() {
        // Required empty public constructor
    }

    /**
     * Overridden function where everything will be setup
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_frgcontrol_override_tab, container, false);

        //Room selection setup
        roomSelectionSpinner = view.findViewById(R.id.override_spinner_select_room);
        setupRoomSelectionSpinner();

        //Schedule type selection setup
        scheduleSelectionRadioGroup = view.findViewById(R.id.override_rb_shcedule);
        scheduleSelectionConstraint = view.findViewById(R.id.override_schedule_constraint);
        setupScheduleSelectionRadioGroup();

        // Day selection spinner setup
        daySelectionSpinner = view.findViewById(R.id.override_spinner_select_day);
        setupDaySelectionSpinner();

        // Slot selection setup
        // TODO: No later option on friday
        slotSelectionSpinner = view.findViewById(R.id.override_spinner_select_slot);
        setupSlotSelectionSpinner();
        setupSlotSelectionSpinnerOnItemSelected();

        // Length selection setup
        lengthSelectionNumberPicker = view.findViewById(R.id.override_schedule_length);
        setLengthNumberPickerExtents();

        // Setup Switches
        switches = new ArrayList<>();
        switches.add((Switch) view.findViewById(R.id.override_switch_relay_1));
        switches.add((Switch) view.findViewById(R.id.override_switch_relay_2));
        switches.add((Switch) view.findViewById(R.id.override_switch_relay_3));
        switches.add((Switch) view.findViewById(R.id.override_switch_relay_4));
        switches.add((Switch) view.findViewById(R.id.override_switch_relay_5));
        switches.add((Switch) view.findViewById(R.id.override_switch_relay_6));
        switches.add((Switch) view.findViewById(R.id.override_switch_relay_7));
        switches.add((Switch) view.findViewById(R.id.override_switch_relay_8));
        setupSwitches();

        // Setup open all button
        openAllButton = view.findViewById(R.id.override_bt_open_all);
        setupOpenAllButton();

        // Setup close all button
        closeAllButton = view.findViewById(R.id.override_bt_close_all);
        setupCloseAllButton();

        // setup submit button
        submitButton = view.findViewById(R.id.override_bt_submit);
        setupSubmitButton();
        context = getContext();

        return view;
    }

    /**
     * Function that will load the data into the room selection spinner
     */
    private void setupRoomSelectionSpinner() {
        roomSelectionSpinnerAdapter =
                new SelectionSpinnerAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        roomSelectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSelectionSpinner.setAdapter(roomSelectionSpinnerAdapter);
    }

    /**
     * Function to load the available days in the day selection spinner
     */
    private void setupDaySelectionSpinner() {
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
     * Function that will load the data into the slot selection spinner
     */
    private void setupSlotSelectionSpinner() {
        // TODO: add functionality so that the slots that have passed don't show up
        String[] slots = getContext().getResources().getStringArray(R.array.Slots);
        slotSelectionSpinnerAdapter =
                new SelectionSpinnerAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, slots,
                                            slots[0]);
        slotSelectionSpinner.setAdapter(slotSelectionSpinnerAdapter);
    }

    /**
     * Function that will calculate the maximum length that can be selected based on the slot
     * selected
     */
    private void setLengthNumberPickerExtents() {
        lengthAllowed = slotSelectionSpinner.getCount() - slotSelectionSpinner.getSelectedItemPosition();
        lengthSelectionNumberPicker.setMinValue(1);
        lengthSelectionNumberPicker.setMaxValue(lengthAllowed);
    }

    /**
     * Function that will setup the functionality of open all button
     */
    private void setupOpenAllButton() {
        openAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAllSwitches();
            }
        });
    }

    /**
     * To switch all the switches to on
     */
    private void openAllSwitches() {
        // Loop through all the switches and turn them on
        for (int i = 0; i < switches.size(); i++)
            switches.get(i).setChecked(true);
    }

    /**
     * Function that will setup the functionality of close all button
     */
    private void setupCloseAllButton() {
        closeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllSwitches();
            }
        });
    }

    /**
     * To switch all the switches to off
     */
    private void closeAllSwitches() {
        // Loop through all the switches and turn them on
        for (int i = 0; i < switches.size(); i++)
            switches.get(i).setChecked(false);
    }

    /**
     * Function that will setup the radio group
     */
    private void setupScheduleSelectionRadioGroup() {
        // if today is friday. Turn off the later functionality
        if (MainActivity.getCurrentDayOfWeek() == 5) {
            view.findViewById(R.id.override_rb_later).setVisibility(View.GONE);
        }
        if (MainActivity.getCurrentDayOfWeek() == 6 || MainActivity.getCurrentDayOfWeek() == 7) {
            view.findViewById(R.id.override_rb_later).setVisibility(View.GONE);
            view.findViewById(R.id.override_rb_right_now).setVisibility(View.GONE);
        }
        if (MainActivity.getCurrentSlot() == -1) {
            view.findViewById(R.id.override_rb_right_now).setVisibility(View.GONE);
        }
        if (view.findViewById(R.id.override_rb_right_now).getVisibility() == View.GONE &&
            view.findViewById(R.id.override_rb_later).getVisibility() == View.GONE) {
            view.findViewById(R.id.override_rb_cant).setVisibility(View.VISIBLE);
            view.findViewById(R.id.override_bt_submit).setVisibility(View.GONE);
        }
    }

    /**
     * Function that will setup the slotSelectionSpinner's on item selected listener
     * Basically it will adjust the maximum extents of the length number picker
     */
    private void setupSlotSelectionSpinnerOnItemSelected() {
        slotSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Re-adjust the lengths
                setLengthNumberPickerExtents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                slotSelectionSpinner.setSelection(1);
                setLengthNumberPickerExtents();
            }
        });
    }

    /**
     * Function that sets the userID when this fragment is being loaded
     *
     * @param x
     */
    public void setUserID(String x) {
        userID = x;
    }

    /**
     * Function that will call the php script that will get the current relays' state and set it to the switches.
     * <p>
     * This function will also setup the listener that will set all switches to off if the 'later' radio button
     * is selected and again call the update function when 'right now' radio button is selected.
     * <p>
     * The same will be done for the roomSelector's on item selected listener in this function
     */
    private void setupSwitches() {
        scheduleSelectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int newSelectionID = i;
                // If new selection is 'later' turn off all switches
                if (newSelectionID == R.id.override_rb_later) {
                    closeAllSwitches();
                } else {
                    updateSwitches();
                }
                if (i == R.id.override_rb_later) {
                    scheduleSelectionConstraint.setVisibility(View.VISIBLE);
                } else {
                    scheduleSelectionConstraint.setVisibility(View.GONE);
                }
            }
        });

        roomSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (scheduleSelectionRadioGroup.getCheckedRadioButtonId() == R.id.override_rb_right_now) {
                    updateSwitches();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                roomSelectionSpinner.setSelection(0);
                updateSwitches();
            }
        });
    }

    /**
     * Function that calls the relayStatus.php script and adjusts the switches accordingly if 'right now' was
     * selected
     */
    private void updateSwitches() {
        // Get the room id
        final String roomSelection = roomSelectionSpinner.getSelectedItem().toString();
        char roomChar = roomSelection.charAt(roomSelection.length() - 1);
        final String room = Integer.toString(1000 + (roomChar - 64));

        // Call the relayStatus.php script and get relays if present
        String url = "http://" + MainActivity.URL + "/AreebaFYP/relayStatus.php";
        Response.Listener listener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject relayResponse = new JSONObject(response.toString());
                    if (relayResponse.getBoolean("roomActive")) {
                        // Read every entry from the array and turn on that switch
                        JSONArray relays = relayResponse.getJSONArray("relaysOn");
                        closeAllSwitches();
                        for (int i = 0; i < relays.length(); i++) {
                            String relayTemp = relays.getString(i);
                            int temp = Integer.parseInt(
                                    Character.toString(relayTemp.charAt(relayTemp.length() - 1))) - 1;
                            switches.get(temp).setChecked(true);
                        }
                    } else {
                        closeAllSwitches();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        //Initialize request string with POST method
        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                //Put user ID and password in data set
                param.put("roomID", room);
                return param;
            }
        };
        //Execute request
        Volleyton.getInstance(getContext()).addToRequestQueue(request);
    }

    /**
     * Function that will setup the submit button functionality.
     * It will perform all the necessary validations and then initiate the API call if all is good
     */
    private void setupSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                int dayOfWeek = -1;
                String room;
                int slot;
                int length;

                // If a selection type is not selected, send error and end function
                if (scheduleSelectionRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Please select a schedule type", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get room ID
                final String roomSelection = roomSelectionSpinner.getSelectedItem().toString();
                char roomChar = roomSelection.charAt(roomSelection.length() - 1);
                room = Integer.toString(1000 + (roomChar - 64));

                // Get dayOfWeek
                // If 'later' is selected, get the user-selected day. Else, get current day
                if (scheduleSelectionRadioGroup.getCheckedRadioButtonId() == R.id.override_rb_later) {
                    String day = daySelectionSpinner.getSelectedItem().toString();
                    switch (day) {
                        case "Monday":
                            dayOfWeek = 1;
                            break;
                        case "Tuesday":
                            dayOfWeek = 2;
                            break;
                        case "Wednesday":
                            dayOfWeek = 3;
                            break;
                        case "Thursday":
                            dayOfWeek = 4;
                            break;
                        case "Friday":
                            dayOfWeek = 5;
                            break;
                    }

                    // Check if today is Friday
                    if (MainActivity.getCurrentDayOfWeekAsIndex() == 4) {
                        Toast.makeText(getContext(), "Today is Friday. Please use the \"Right " + "Now\" option.",
                                       Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    // If the 'right now' option is selected. Get the current day
                    dayOfWeek = MainActivity.getCurrentDayOfWeek();
                }

                // Get Slot
                // If 'later' is selected, get the user-selected slot. Else, get current slot
                if (scheduleSelectionRadioGroup.getCheckedRadioButtonId() == R.id.override_rb_later) {
                    slot = slotSelectionSpinner.getSelectedItemPosition() + 1;
                } else {
                    slot = MainActivity.getCurrentSlot();
                }

                // Get Length
                // If 'later' is selected, get user-selected length. Else, get the length that
                // will take from current slot to the end of the day
                if (scheduleSelectionRadioGroup.getCheckedRadioButtonId() == R.id.override_rb_later) {
                    length = lengthSelectionNumberPicker.getValue();
                } else {
                    length = slotSelectionSpinner.getCount() + 1 - MainActivity.getCurrentSlot();
                }

                // This statement will only be reached if everything was validated and loaded
                // properly
                // TODO: configure this request properly and create its respective php receiver
                // TODO: configure room status tab for HOD override too
                String msg = "";
                RadioButton rb = view.findViewById(scheduleSelectionRadioGroup.getCheckedRadioButtonId());
                msg += roomSelectionSpinner.getSelectedItem().toString() + " | ";
                msg += rb.getText() + " | ";
                if (rb.getId() == R.id.override_rb_later) {
                    msg += dayOfWeek + " | ";
                    msg += slot + " | ";
                    msg += length + " | ";
                }

                // Switch message
                for (int i = 0; i < switches.size(); i++) {
                    if (switches.get(i).isChecked()) {
                        msg += "ON ";
                    } else {
                        msg += "OFF ";
                    }
                }

                //Start preparing data to send to server
                //TODO: Create the php receiver for this request!
                String url = "http://" + MainActivity.URL + "/AreebaFYP/scheduleOverride.php";

                //Setting up response handler
                Response.Listener listener = new Response.Listener() {
                    @Override
                    public void onResponse(@NonNull Object response) {
                        try {
                            JSONObject overrideResponse = new JSONObject(response.toString());
                            AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                            if (overrideResponse.getBoolean("successful")) {
                                dialog.setTitle("Successfully Scheduled!");
                                dialog.setMessage("The schedule was overridden successfully.");
                            } else {
                                dialog.setTitle("Successfully Scheduled!");
                                dialog.setMessage("The schedule could not overridden. Try again some other time.");
                            }
                            dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }; Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull VolleyError error) {
                        Log.v("XXXXXXXXXXXXXXXXXX", error.getLocalizedMessage());
                    }
                };

                //Initialize request string with POST method
                final int finalSlot = slot;
                final int finalLength = length;
                final String finalRoom = room;
                final int finalDayOfWeek = dayOfWeek;
                StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> param = new HashMap<>();
                        //Put room id and user id in dataset
                        param.put("roomID", finalRoom);
                        param.put("userID", userID);

                        // Put day, slot, and length
                        param.put("day", Integer.toString(finalDayOfWeek));
                        param.put("slot", Integer.toString(finalSlot));
                        param.put("length", Integer.toString(finalLength));

                        //Put relay controls selected by user
                        for (int i = 0; i < switches.size(); i++)
                            param.put("relay" + (i + 1), Boolean.toString(switches.get(i).isChecked()));
                        return param;
                    }
                };
                //Execute request
                Volleyton.getInstance(getContext()).addToRequestQueue(request);
            }
        });
    }
}
