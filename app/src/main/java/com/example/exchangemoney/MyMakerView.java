package com.example.exchangemoney;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;

public class MyMakerView extends MarkerView {

    private final TextView tvContent;
    private final ArrayList<String> labels;

    public MyMakerView(Context context, ArrayList<String> labels) {
        super(context, R.layout.custom_marker_view); // custom_marker_view.xml là layout hiển thị

        this.labels = labels;
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int xIndex = (int) e.getX();
        float rate = e.getY();

        String label = labels.get(xIndex);

        tvContent.setText("Mã tiền: " + label + "\nTỷ giá: " + rate);

        super.refreshContent(e, highlight);
    }
}

