package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SelectionSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String [] options;
    private String defaultOption;
    private String firstOption;
    private boolean isFirstTime;

    public SelectionSpinnerAdapter(Context context, int resource) {
        super(context, resource, context.getResources().getStringArray(R.array.Rooms));
        this.context = context;
        this.options = context.getResources().getStringArray(R.array.Rooms);
        firstOption = options[0];
        defaultOption = context.getResources().getString(R.string.default_text_room_spinner);
        options[0] = defaultOption;
        isFirstTime = true;
    }

    /**
     * Override for the default constructor. Call this one if you want custom options
     * @param data: String array: custom set of data
     * @param defaultOption: String: text to show for the first time
     */
    public SelectionSpinnerAdapter(Context context, int resource, String [] data, String defaultOption) {
        super(context, resource, context.getResources().getStringArray(R.array.Rooms));
        this.context = context;
        this.options = data;
        firstOption = options[0];
        this.defaultOption = defaultOption;
        options[0] = this.defaultOption;
        isFirstTime = true;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (isFirstTime) {
            options[0] = firstOption;
            isFirstTime = false;
        }
        return super.getDropDownView(position, convertView, parent);
    }
}
