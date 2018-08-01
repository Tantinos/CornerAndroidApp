package com.example.evangelidis.cornerandroidapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private List<CVS_Model> boxing = new ArrayList<>();
    int []p = new int [13];
    int []punchTypesCount = new int[6];
    public int totalPunches;
    private List<Double> speed = new ArrayList<>();
    private List<Double> power = new ArrayList<>();
    private double avg_speed,avg_power;
    GraphView graph;

    HorizontalBarChart barChart1, barChart2, barChart3, barChart4, barChart5, barChart6;
    ArrayList<BarEntry> entries1, entries2, entries3, entries4, entries5, entries6;
    ArrayList<String> labels;

    boolean startNewTimer = false;

    TextView lj,lh,lu,rc,rh,ru;

    ImageView speed_icon, power_icon;

    private ProgressBar progressBar;
    TextView tv, punches_count, punches_speed,punches_power;
    int progressStatusCounter = 0;

    Handler progressHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        tv = (TextView)findViewById(R.id.time);
        punches_count = (TextView)findViewById(R.id.punch_number);
        punches_speed = (TextView)findViewById(R.id.punch_speed);
        punches_power = (TextView)findViewById(R.id.punch_power);

        speed_icon = (ImageView)findViewById(R.id.speed_icon);
        power_icon = (ImageView) findViewById(R.id.power_icon);

        lj = (TextView)findViewById(R.id.lj);
        lh = (TextView)findViewById(R.id.lh);
        lu = (TextView)findViewById(R.id.lu);
        rc = (TextView)findViewById(R.id.rc);
        rh = (TextView)findViewById(R.id.rh);
        ru = (TextView)findViewById(R.id.ru);

        barChart1 = (HorizontalBarChart) findViewById(R.id.bar1);
        barChart2 = (HorizontalBarChart) findViewById(R.id.bar2);
        barChart3 = (HorizontalBarChart) findViewById(R.id.bar3);
        barChart4 = (HorizontalBarChart) findViewById(R.id.bar4);
        barChart5 = (HorizontalBarChart) findViewById(R.id.bar5);
        barChart6 = (HorizontalBarChart) findViewById(R.id.bar6);

        graph = (GraphView) findViewById(R.id.graph);

        // Fill the punch types list with zero values
        for(int x=0; x<punchTypesCount.length; x++){
            punchTypesCount[x] = 0;
        }

        startTimer();
        readExcelFile();

        setRandomIcons();

    }

    // Method to set random icons
    private void setRandomIcons() {

        Random rand = new Random();

        int  p = rand.nextInt(3) + 1;
        int s = rand.nextInt(3) + 1;


        switch (p){
            case 1:
                power_icon.setImageResource(R.drawable.ic_up);
                break;
            case 2:
                power_icon.setImageResource(R.drawable.ic_down);
                break;
            case 3:
                power_icon.setImageResource(R.drawable.ic_same);
                break;
        }

        switch (s){
            case 1:
                speed_icon.setImageResource(R.drawable.ic_up);
                break;
            case 2:
                speed_icon.setImageResource(R.drawable.ic_down);
                break;
            case 3:
                speed_icon.setImageResource(R.drawable.ic_same);
                break;
        }

    }

    // Method about the progress bar countdown
    private void startTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatusCounter<60){
                    if(startNewTimer == false){
                        progressStatusCounter ++;
                        if(progressStatusCounter == 59){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayToast("Round two is about to start");
                                }
                            });
                        }
                        progressHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(progressStatusCounter);
                                int sec = 60-progressStatusCounter;
                                String x = String.valueOf(sec);
                                if(sec<=9){
                                    x = "0"+x;
                                }
                                tv.setText("0:"+(x));
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        startNewTimer = false;
                        progressBar.setProgress(0);
                        progressStatusCounter = 0;
                    }
                }
            }
        }).start();
    }

    // Method to display parameter message in toast
    private void displayToast(String message) {
        Toast.makeText(MainActivity.this, message,Toast.LENGTH_LONG).show();
    }

    // Method to read the cvs file
    private void readExcelFile() {

        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        int countLine = 0;
        int tableIndex;

        try{
            while ((line = reader.readLine()) != null){

                countLine++;
                String[] tokens = line.split(",");

                if((countLine == 1) || (tokens.length ==1) ){
                    continue;
                }

                //Read the data
                CVS_Model sample = new CVS_Model();
                sample.setTime(tokens[0]);
                sample.setPunch_ty(Integer.parseInt(tokens[1]));
                sample.setSpeed_m(Double.parseDouble(tokens[2]));
                sample.setPower_g(Double.parseDouble(tokens[3]));

                boxing.add(sample);
                speed.add(Double.parseDouble(tokens[2]));
                power.add(Double.parseDouble(tokens[3]));

                // Fill list of punches type
                punchTypesCount[sample.getPunch_ty()]++;

                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss"); // 12 hour format

                // Get time from cvs file
                java.util.Date d1 =(java.util.Date)format.parse(tokens[0]);
                java.sql.Time ppstime = new java.sql.Time(d1.getTime());

                // FIll the tble with number each 15 seconds
                tableIndex = getSeconds(ppstime)/15;
                p[tableIndex]++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get total number of punches
        totalPunches = boxing.size();

        // Get avg values of speed and power
        avg_power = calculateAverage(power);
        avg_speed = calculateAverage(speed);

        // Set textview strings
        punches_count.setText(String.valueOf(boxing.size()));
        punches_power.setText(String.valueOf(avg_power));
        punches_speed.setText(String.valueOf( avg_speed));

        lj.setText(String.valueOf(punchTypesCount[0]));
        lh.setText(String.valueOf(punchTypesCount[1]));
        lu.setText(String.valueOf(punchTypesCount[2]));
        rc.setText(String.valueOf(punchTypesCount[3]));
        rh.setText(String.valueOf(punchTypesCount[4]));
        ru.setText(String.valueOf(punchTypesCount[5]));

        // Plot the graphs
        plotGraph(p);
        plotBarGraphs();
    }

    // Methos to set the 6 bar graphs
    private void plotBarGraphs() {

        barChart1.setDescription("");
        barChart1.getLegend().setEnabled(false);
        barChart1.getAxisLeft().setDrawGridLines(false);
        barChart1.getAxisRight().setDrawGridLines(false);
        barChart1.setBorderColor(Color.BLACK);
        YAxis y1 = barChart1.getAxisLeft();
        y1.setAxisMinValue(0f);
        y1.setAxisMaxValue((totalPunches));

        barChart2.setDescription("");
        barChart2.getLegend().setEnabled(false);
        barChart2.getAxisLeft().setDrawGridLines(false);
        barChart2.getAxisRight().setDrawGridLines(false);
        YAxis y2 = barChart2.getAxisLeft();
        y2.setAxisMinValue(0f);
        y2.setAxisMaxValue(totalPunches);

        barChart3.setDescription("");
        barChart3.getLegend().setEnabled(false);
        barChart3.getAxisLeft().setDrawGridLines(false);
        barChart3.getAxisRight().setDrawGridLines(false);
        YAxis y3 = barChart3.getAxisLeft();
        y3.setAxisMinValue(0f);
        y3.setAxisMaxValue(totalPunches);

        barChart4.setDescription("");
        barChart4.getLegend().setEnabled(false);
        barChart4.getAxisLeft().setDrawGridLines(false);
        barChart4.getAxisRight().setDrawGridLines(false);
        YAxis y4 = barChart4.getAxisLeft();
        y4.setAxisMinValue(0f);
        y4.setAxisMaxValue(totalPunches);

        barChart5.setDescription("");
        barChart5.getLegend().setEnabled(false);
        barChart5.getAxisLeft().setDrawGridLines(false);
        barChart5.getAxisRight().setDrawGridLines(false);
        YAxis y5 = barChart5.getAxisLeft();
        y5.setAxisMinValue(0f);
        y5.setAxisMaxValue(totalPunches);

        barChart6.setDescription("");
        barChart6.getLegend().setEnabled(false);
        barChart6.getAxisLeft().setDrawGridLines(false);
        barChart6.getAxisRight().setDrawGridLines(false);
        YAxis y6 = barChart6.getAxisLeft();
        y6.setAxisMinValue(0f);
        y6.setAxisMaxValue(totalPunches);

        entries1 = new ArrayList<>();
        entries1.add(new BarEntry(punchTypesCount[0],0));

        entries2 = new ArrayList<>();
        entries2.add(new BarEntry(punchTypesCount[1],0));

        entries3 = new ArrayList<>();
        entries3.add(new BarEntry(punchTypesCount[2],0));

        entries4 = new ArrayList<>();
        entries4.add(new BarEntry(punchTypesCount[3],0));

        entries5 = new ArrayList<>();
        entries5.add(new BarEntry(punchTypesCount[4],0));

        entries6 = new ArrayList<>();
        entries6.add(new BarEntry(punchTypesCount[5],0));

        labels = new ArrayList<>();
        labels.add("");

        BarDataSet barDataSet1= new BarDataSet(entries1,"");
        BarDataSet barDataSet2 = new BarDataSet(entries2,"");
        BarDataSet barDataSet3 = new BarDataSet(entries3,"");
        BarDataSet barDataSet4 = new BarDataSet(entries4,"");
        BarDataSet barDataSet5= new BarDataSet(entries5,"");
        BarDataSet barDataSet6 = new BarDataSet(entries6,"");

        BarData data1 = new BarData(labels,barDataSet1);
        BarData data2 = new BarData(labels,barDataSet2);
        BarData data3 = new BarData(labels,barDataSet3);
        BarData data4 = new BarData(labels,barDataSet4);
        BarData data5 = new BarData(labels,barDataSet5);
        BarData data6 = new BarData(labels,barDataSet6);

        barChart1.setData(data1);
        barChart2.setData(data2);
        barChart3.setData(data3);
        barChart4.setData(data4);
        barChart5.setData(data5);
        barChart6.setData(data6);
    }

    // Method to convert the time from the cvs file to seconds
    private int getSeconds(Time ppstime) {

        int totalSeconds = 0;
        if(ppstime.getMinutes() >= 1 ){
            totalSeconds = ppstime.getSeconds();

            switch (ppstime.getMinutes()){
                case 1:
                    totalSeconds += 60;
                    break;
                case 2:
                    totalSeconds += 120;
                    break;
                case 3:
                    totalSeconds += 180;
                    break;
            }
            return totalSeconds;
        }
        return ppstime.getSeconds();
    }

    // Method to set the graph
    private void plotGraph(int[] p) {

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] { });

        int sec = 15;
        for(int x=0; x<=p.length-1; x++){
            series.appendData(new DataPoint(sec,p[x]),true,p.length);
            sec = sec+15;
        }

        series.setAnimated(true);
        graph.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"", "", "","1:00"," "," "," ","2:00","","","","3:00"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graph.addSeries(series);
    }

    // Method to calc the average value
    private double calculateAverage(List<Double> list) {

        double sum = 0;
        DecimalFormat df2 = new DecimalFormat(".#");
        if(!list.isEmpty()) {
            for (Double mark : list) {
                sum += mark;
            }
            return Double.parseDouble(df2.format(sum  / list.size()));
        }
        return Double.parseDouble(df2.format(sum));
    }

    // Method to reset the timer when user press 'reset textView'
    public void resetTimer(View view) {

        startNewTimer = true;

        if(progressStatusCounter == 60){
            progressHandler.removeCallbacks(null);
            progressBar.setProgress(0);
            progressStatusCounter = 0;
            startTimer();
        }
    }

    // Method to start next round
    public void startNextRound(View view) {
        progressStatusCounter = 59;
        displayToast("Next round started");
    }
}
