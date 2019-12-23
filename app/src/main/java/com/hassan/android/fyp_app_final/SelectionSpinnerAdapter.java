package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectionSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String [] options;
    private String defaultOption;
    private String firstOption;
    private boolean isFirstTime;
    private boolean isSecondTime;

    public SelectionSpinnerAdapter(Context context, int resource) {
        super(context, resource, context.getResources().getStringArray(R.array.Rooms));
        this.context = context;
        this.options = context.getResources().getStringArray(R.array.Rooms);
    }

    /**
     * Override for the default constructor. Call this one if you want custom options and pass data as string array
     * @param data: String []: custom set of data
     * @param defaultOption: String: text to show for the first time
     */
    public SelectionSpinnerAdapter(Context context, int resource, String [] data, String defaultOption) {
        super(context, resource, data);
        this.context = context;
        this.options = data;
    }

    /**
     * Override for the default constructor. Call this one if you want custom options
     * @param data: ArraList<String>: custom set of data
     * @param defaultOption: String: text to show for the first time
     */
    public SelectionSpinnerAdapter(Context context, int resource, ArrayList<String> data, String defaultOption) {
        super(context, resource, data);
        this.context = context;
        this.options = new String[data.size()];
        this.options = data.toArray(this.options);
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

}
