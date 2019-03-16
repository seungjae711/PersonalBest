package com.example.group12_project;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BarGraph {

    //Corresponding with calendar's DAY_OF_WEEK field
    //Made first entry empty since there is no 0 value for DAY_OF_WEEK
    private String[] days = {"", "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};

    private BarChart barChart;
    private ArrayList<BarEntry> Entries;
    private Calendar startDate, endDate;
    private String First = "2016/05/05", Last = "2016/05/13";
    private int[] a = {2000, 1500, 4545, 3000, 3450, 4470, 2473};
    private int goals;
    private static final String TAG = "[BarGraph]";


    public BarGraph(Activity activity) {
        this.barChart = activity.findViewById(R.id.bargraph);
        SharedPreferences storedGoal
                = activity.getSharedPreferences("storedGoal", Context.MODE_PRIVATE);

        // check if goal is null
        String goalValue = storedGoal.getString("goal", "");
        if (goalValue != null) {
            goals = Integer.parseInt(goalValue);
            Log.i(TAG, String.format("Goals got from shared preferences is %s", goals));
        } else {
            Log.i(TAG, "Can't access goals");
            activity.finish();
        }
       // createRandomBarGraph(First, Last, goals, a);
    }

    public void createStepBarGraph(int[] seshSteps, int[] allSteps) {
        Calendar cal = Calendar.getInstance();
        String today = getDate(cal);
        cal.add(Calendar.DAY_OF_YEAR, -7);
        String lastWeek = getDate(cal);
        this.createStepBarGraph(seshSteps, allSteps, 7000, lastWeek, today);
    }

    public void createStepBarGraph(int[] seshSteps, int[] allSteps, int goal, String start, String end) {


      //  int size = Math.min(seshSteps.size(), allSteps.size());
        if (seshSteps == null) {
            Log.e(TAG, "empty array");
            return;
        }
        int size = seshSteps.length;
        Log.i(TAG, "Number of session bars: " + size);
        int max = 0;

        try {
            ArrayList<String> dates = new ArrayList<>();
       //     dates = getList(startDate, endDate);

            Entries = new ArrayList<>();

            int day = 0;
            for (int i = 0; i < size; i++) {
                Log.i(TAG, "Session steps: " + seshSteps[i]);
                //When ready for testing just delete the steps added to the array values
                Entries.add(new BarEntry(day++, seshSteps[i] + 1000));
                Entries.add(new BarEntry(day++, allSteps[i] + 5000));
                day++; //spacing between days
                max = Math.max( max, Math.max(seshSteps[i] +1000 , allSteps[i] + 5000) );
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
            e.printStackTrace();
        }

        if (Entries == null) {
            Log.e(TAG, "Entries was null!");
            return;
        }
        max = Math.max(max, goal);

        LimitLine upper_limit = new LimitLine(goal, "Your Goal!");
        upper_limit.setLineWidth(5f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(13f);
        upper_limit.setTextColor(android.graphics.Color.parseColor("#D81B60"));
        YAxis leftAxis = this.barChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(upper_limit);

        BarDataSet barDataSet = new BarDataSet(Entries, "Steps");

        barDataSet.setColor(android.graphics.Color.parseColor("#008577"));
        BarData barData = new BarData(barDataSet);
        this.barChart.setData(barData);
        this.barChart.setScaleEnabled(true);
        this.barChart.setFitBars(true);
        this.barChart.setVisibleYRange(1, max + 1000, YAxis.AxisDependency.RIGHT);
        this.barChart.setDescription("This shows the number of your steps(session-total) from " + start + " to " + end);
        this.barChart.setDescriptionTextSize(9f);


    }

    private void createRandomBarGraph(String Date1, String Date2, int goal, int[] steps) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

        try {
            Date date1 = simpleDateFormat.parse(Date1);
            Date date2 = simpleDateFormat.parse(Date2);

            Calendar mDate1 = Calendar.getInstance();
            Calendar mDate2 = Calendar.getInstance();

            mDate1.clear();
            mDate2.clear();

            mDate1.setTime(date1);
            mDate2.setTime(date2);

            ArrayList<String> dates = new ArrayList<>();
            dates = getList(mDate1, mDate2);

            Entries = new ArrayList<>();

            // needs steps for each day

            //float max = 0f;
            //float min = 0f;
            //float value = 0f;
            //random = new Random();
            int day = 1;

            for (int j : steps) {
                //max = 3000f;
                //min = 3000f;
                //value = random.nextFloat()*max + min;
                BarEntry Bar = new BarEntry(1, j);
                Entries.add(Bar);
                day++;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // needs value of goal
        LimitLine upper_limit = new LimitLine(goal, "Your Goal!");
        upper_limit.setLineWidth(5f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(13f);
        upper_limit.setTextColor(android.graphics.Color.parseColor("#D81B60"));
        YAxis leftAxis = this.barChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(upper_limit);

        BarDataSet barDataSet = new BarDataSet(Entries, "Steps");

        barDataSet.setColor(android.graphics.Color.parseColor("#008577"));
        BarData barData = new BarData(barDataSet);
        this.barChart.setData(barData);
        this.barChart.setScaleEnabled(true);
        this.barChart.setDescription("This shows the number of your steps from " + First + " to " + Last);
        this.barChart.setDescriptionTextSize(9f);
    }


    private ArrayList<String> getList(Calendar startDate, Calendar endDate) {
        ArrayList<String> list = new ArrayList<String>();
        while (startDate.compareTo(endDate) <= 0) {
            list.add(getDate(startDate));
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    private String getDate(Calendar cld) {
        String curDate = cld.get(Calendar.YEAR) + "/" + (cld.get(Calendar.MONTH) + 1) + "/"
                + cld.get(Calendar.DAY_OF_MONTH);
        try {
            Date date = new SimpleDateFormat("yyyy/MM/dd", Locale.US).parse(curDate);
            curDate = new SimpleDateFormat("yyy/MM/dd", Locale.US).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return curDate;
    }

}
