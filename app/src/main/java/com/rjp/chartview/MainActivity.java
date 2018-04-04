package com.rjp.chartview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rjp.chartview.chartview.Cell;
import com.rjp.chartview.chartview.CellGroup;
import com.rjp.chartview.chartview.ChartView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChartView chartView = (ChartView) findViewById(R.id.chart_view);

        Cell preCeil = null;
        Random random = new Random();
        ArrayList<CellGroup> blueCeilsGroup = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            CellGroup e = new CellGroup();
            ArrayList<Cell> ceils = new ArrayList<>();
            int selectedIndex = random.nextInt(10);
            for (int j = 0; j < 10; j++) {
                Cell e1 = new Cell();
                e1.setNumber(String.valueOf(j));
                if (j == selectedIndex) {
                    e1.setSelected(true);
                    e1.setColor(Color.BLUE);
                }
                ceils.add(e1);
                if (preCeil != null && e1.isSelected()) {
                    e1.setNextCell(i == 0 ? null : preCeil);
                }
                if (e1.isSelected()) {
                    preCeil = e1;
                }
            }
            e.setCells(ceils);
            blueCeilsGroup.add(e);
        }
        chartView.addAnotherChart(blueCeilsGroup);
    }
}
