package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.entries;
import static android.R.attr.x;
import static android.R.id.message;

public class StockDetails extends AppCompatActivity {

    private static final String[] DETAIL_COLUMNS = {
            Contract.Quote.COLUMN_HISTORY,
     };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        Intent intent = getIntent();
        String symbol = intent.getStringExtra(String.valueOf(R.string.stock_detail_key));
        // Get data from ContentProvider
        Cursor cur = getContentResolver().query(Contract.Quote.makeUriForStock(symbol),
                DETAIL_COLUMNS, symbol, null, null);

        cur.moveToFirst();
        if (cur.getCount() > 0) {
            HashMap<Long, Float> quotes = new HashMap<>();
            LineChart chart = (LineChart) findViewById(R.id.stock_chart);
            List<Entry> entries = new ArrayList<Entry>();
            int columnIndex = cur.getColumnIndexOrThrow(Contract.Quote.COLUMN_HISTORY);
            do {
                String stringQuotes = cur.getString(columnIndex);
                String[] stringQuote = stringQuotes.split("\n");
                for (String data : stringQuote) {
                    String[] prices = data.split(",");
                    Long timestamp = new Long(prices[0]);
                    Float value = new Float(prices[1]);
                    quotes.put(timestamp, value);
                }
            }
            while (cur.moveToNext());

            for (Map.Entry<Long, Float> data : quotes.entrySet()) {
                Date date=new Date(data.getKey());
                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                entries.add(new Entry(data.getKey(), data.getValue()));

            }
            Collections.sort(entries, new EntryXComparator());
            LineDataSet dataSet = new LineDataSet(entries, "Prices"); // add entries to dataset


   /*         // the labels that should be drawn on the XAxis
            final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4" };

            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return quarters[(int) value];
                }

                // we don't draw numbers, so no decimal digits needed
                @Override
                public int getDecimalDigits() {  return 0; }
            };

            XAxis xAxis = mLineChart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);

*/
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            XAxis xAxis = chart.getXAxis();
            xAxis.setLabelRotationAngle(45);
            xAxis.setLabelCount(5, true);
            chart.invalidate(); // refresh
        }
    }
}
