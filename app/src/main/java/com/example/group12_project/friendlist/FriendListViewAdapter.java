package com.example.group12_project.friendlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.group12_project.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FriendListViewAdapter extends ArrayAdapter<SelfData> {

    private Context context;

    private int resourceLayout;

    FriendListViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<SelfData> friends) {
        super(context, resource, friends);
        this.context = context;
        this.resourceLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getId();
        String goal = getItem(position).getGoal();
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String strDate = dateFormat.format(date);
        String currentStep = getItem(position).getDaily_steps(strDate);

        LayoutInflater inflater = LayoutInflater.from(context);

        //Not optimal design, might slow down mainthread with too many friends
        convertView = inflater.inflate(resourceLayout, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.friend_name);
        TextView tvGoal = (TextView) convertView.findViewById(R.id.friend_goal);
        TextView tvCurrentStep = (TextView) convertView.findViewById(R.id.friend_current_step);

        tvName.setText(name);
        tvGoal.setText(goal);
        tvCurrentStep.setText(currentStep);

        return convertView;
    }
}
