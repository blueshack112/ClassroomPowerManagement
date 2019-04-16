package com.hassan.android.fyp_app_final;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RoomSelectionSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private String [] options;
    private String defaultOption;
    private String firstOption;
    private boolean isFirstTime;

    public RoomSelectionSpinnerAdapter(Context context, int resource) {
        super(context, resource, context.getResources().getStringArray(R.array.Rooms));
        this.context = context;
        this.options = context.getResources().getStringArray(R.array.Rooms);
        firstOption = options[0];
        options[0] = defaultOption;
        defaultOption = context.getResources().getString(R.string.default_text_room_spinner);
        isFirstTime = true;
    }



    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (isFirstTime) {
            options[0] = firstOption;
            isFirstTime = false;
        }
        Log.d("imp", options[0]);

        return super.getDropDownView(position, convertView, parent);
    }
}
