package myMain;

import org.jfree.chart.ChartPanel;
import utils.MyChart;

import javax.swing.*;
import java.util.ArrayList;

public class myGUI {
    public myGUI(ArrayList<Double>[][] results) {
        JFrame pFrame = new JFrame("pFrame");
        JFrame vFrame = new JFrame("vFrame");

        String[] legends = {"X", "Y", "Z", "Combined"};
        ChartPanel pPanel = MyChart.cdfPlot("CDF of Position Error", "Position Error (cm)", legends, results[0]);
        pPanel.setBounds(0, 0, 100, 100);

        ChartPanel vPanel = MyChart.cdfPlot("CDF of Velocity Error", "Velocity Error (cm/s)", legends, results[1]);
        vPanel.setBounds(0, 1000, 100, 100);

        pFrame.setContentPane(pPanel);
        pFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pFrame.pack();

        vFrame.setContentPane(vPanel);
        vFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vFrame.pack();

        pFrame.setVisible(true);
        vFrame.setVisible(true);
    }

    public static void myGUI(ArrayList<Double>[][] results) {
        new myGUI(results);
    }
}
