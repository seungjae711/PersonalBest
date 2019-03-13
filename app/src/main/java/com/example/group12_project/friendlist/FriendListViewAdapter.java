package com.example.group12_project.friendlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.group12_project.R;

import java.util.ArrayList;

public class FriendListViewAdapter extends ArrayAdapter<Friend> {

    private Context context;

    int resourseLayout;

    /**
     * Default constructor
     * @param context
     * @param resource
     * @param friends
     */
    public FriendListViewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Friend> friends) {
        super(context, resource, friends);
        this.context = context;
        this.resourseLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        int goal = getItem(position).getGoal();
        long currentStep = getItem(position).getCurrentStep();

        Friend friend = new Friend(name,goal,currentStep);

        LayoutInflater inflater = LayoutInflater.from(context);

        //Not optimal design, might slow down mainthread with too many friends
        convertView = inflater.inflate(resourseLayout,parent,false);

        TextView tvName = (TextView) convertView.findViewById(R.id.friend_name);
        TextView tvGoal = (TextView) convertView.findViewById(R.id.friend_goal);
        TextView tvCurrentStep = (TextView) convertView.findViewById(R.id.friend_current_step);

        tvName.setText(name);
        tvGoal.setText(String.valueOf(goal));
        tvCurrentStep.setText(String.valueOf(currentStep));

        return convertView;

    }
}
