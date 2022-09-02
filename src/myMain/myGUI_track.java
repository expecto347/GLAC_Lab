package myMain;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.ChartPanel;
import utils.MyChart;

import javax.swing.*;
import java.util.ArrayList;

public class myGUI_track {
    public myGUI_track(ArrayList<Pair<Double, Double>> tr) {
        JFrame frame = new JFrame("track");

        String[] legends = {"track"};
        ChartPanel panel = MyChart.track_plot("track", tr);
        panel.setBounds(0, 0, 100, 100);

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setVisible(true);
    }

    public static void myGUI_track(ArrayList<Pair<Double, Double>> tr) {
        new myGUI_track(tr);
    }
}
