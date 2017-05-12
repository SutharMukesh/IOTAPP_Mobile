package com.example.mukesh.iotapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Reports extends AppCompatActivity implements OnChartValueSelectedListener {
    private ArrayList<BarEntry> entries;
    private BarChart barChart,barChartMth;
    private ArrayList<String> TheDates,TheMths;//,dateKey,mthKey;
    private BarDataSet barDataSet;
    private int i=0;
    private BarData theData;
    private String date,mth;
    private RadioButton mdays,mMonths;
    private HashMap hdays = new HashMap();
    private HashMap hmths = new HashMap();
    private HashMap hnew = new HashMap();
    private SimpleDateFormat mthformat,dayformat;
    private int totalSecs = 0;
    private float totalamt = 0;
    private TextView amt;
    private int totalKW,volts,amp,rsPerKW=4,totalrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref=database.getReference("usage/Light");
        mdays=(RadioButton)findViewById(R.id.rbDays);
        mMonths=(RadioButton)findViewById(R.id.rbMonths);
        volts=5;
        amp=2;
        totalKW=(volts*amp)/1000;
        totalrs=totalKW*rsPerKW;
        barChart=(BarChart) findViewById(R.id.chart);
        barChartMth=(BarChart) findViewById(R.id.chart);
       amt = (TextView)findViewById(R.id.amt);

        //dateKey=new ArrayList<>();
        //mthKey=new ArrayList<>();
        dayformat = new SimpleDateFormat("dd-MMM-yyyy");
        mthformat = new SimpleDateFormat("MMM-yyyy");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                //<String> post = postSnapshot.getValue(<String>.class);
                    String post=postSnapshot.getValue(String.class);
                    date=post.split(" ")[1];
                    int usage=Integer.parseInt(post.split(" ")[0]);

                    //datewise
                    if(hdays.containsKey(date)) {
                        hdays.put(date,Integer.parseInt(hdays.get(date).toString())+usage);
                        Log.e("Message", "from if loop  is contains "+date+" day");
                    }
                    else
                    {
                        hdays.put(date,usage);
                        Log.e("Message", "Total dates from else loop "+TheDates+" is "+date+" (secs).");
                    }

                    //monthwise
                    try {
                        mth = mthformat.format(dayformat.parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(hmths.containsKey(mth)) {
                        hmths.put(mth,Integer.parseInt(hmths.get(mth).toString())+usage);
                        Log.e("Message", "from if loop  is contains "+mth+" month");
                    }
                    else
                    {
                        hmths.put(mth,usage);
                        Log.e("Message", "Total dates from else loop "+TheDates+" is "+date+" (secs).");
                    }
                    Log.e("Message", "Total usage for "+date+" is "+hdays.get(date).toString()+" (secs).");
                    Log.e("Get Data ", post);
                    totalSecs=totalSecs+usage;
                }
                Log.e("totalSecs : ", ""+totalSecs);
                showDays();
                RadioButton rbu1 =(RadioButton)findViewById(R.id.rbDays);
                rbu1.setChecked(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbDays:
                if (checked){
                    showDays();
                }
                break;
            case R.id.rbMonths:
                if (checked) {
                    showMths();
                }
                break;
        }
    }

    public void showDays(){
        entries= new ArrayList<BarEntry>();
        TheDates=new ArrayList<>();
        i =0;
        Map<Date, String> treeMap = new TreeMap<Date, String>(hdays);
        Set s = treeMap.entrySet();
        Iterator it = s.iterator();
        while(it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            int value = Integer.parseInt(entry.getValue().toString());
            Log.e("Reports","Key = " + entry.getKey().toString() + ", Value = " + value);
            TheDates.add(entry.getKey().toString());
            entries.add(new BarEntry(value,i++));
        }
        barDataSet =new BarDataSet(entries , "time in sec");
        theData=new BarData(TheDates, barDataSet);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setScaleXEnabled(true);
        barChart.setOnChartValueSelectedListener(this);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setScaleYEnabled(false);
        barChart.setData(theData);
        barChart.invalidate();
        barChart.refreshDrawableState();
    }

    public void showMths(){
        entries= new ArrayList<BarEntry>();
        TheMths=new ArrayList<>();
        i =0;
        Map<Date, String> treeMap = new TreeMap<Date, String>(hmths);
        Set s = treeMap.entrySet();
        Iterator it = s.iterator();
        while(it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            int value = Integer.parseInt(entry.getValue().toString());
            Log.e("Reports","Key = " + entry.getKey().toString() + ", Value = " + value);
            TheMths.add(entry.getKey().toString());
            entries.add(new BarEntry(value,i++));
        }
        barDataSet =new BarDataSet(entries , "time in sec");
        theData=new BarData(TheMths, barDataSet);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setScaleXEnabled(true);
        barChart.setOnChartValueSelectedListener(this);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setScaleYEnabled(false);
        barChart.setData(theData);
        barChart.invalidate();
        barChart.refreshDrawableState();
    }

    public float calAmt(float totalSecs)
    {
        Log.e("Amount Calculation : ","Secs => "+totalSecs);
        float hrs = totalSecs/60;
        float bulbWatt = (1*hrs)*0.06f;
        Log.e("Amount Calculation : ","Hours => "+hrs);
        Log.e("Amount Calculation : ","Comsumption => "+bulbWatt);
        return bulbWatt;
    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.e("from Tap "," enrries "+entries.indexOf(e)+" value "+entries.get(entries.indexOf(e)));
        String Value=entries.get(entries.indexOf(e)).toString();
        String Val=Value.split(": ")[2];
        Double seconds=(Double.parseDouble(Val));
        double rs=(seconds/3600)*4;
        Log.d("the value","VAlue is "+(seconds/3600));
        amt.setText("Rs. "+Math.round(rs)/1.0);
    }

    @Override
    public void onNothingSelected() {
    }
}
