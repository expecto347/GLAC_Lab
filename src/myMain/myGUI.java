package myMain;

import org.jfree.chart.ChartPanel;
import utils.MyChart;

import javax.swing.*;
import java.util.ArrayList;

public class myGUI {
    public myGUI(ArrayList<Double>[][] results) {
        JFrame pFrame = new JFrame("pFrame");
        JFrame vFrame = new JFrame("vFrame");
        JFrame p_time_Frame = new JFrame("p_time_Frame");
        JFrame v_time_Frame = new JFrame("v_time_Frame");


        String[] legends = {"X", "Y", "Z", "Combined"};

        ChartPanel p_time_Panel = MyChart.error_plot("Position Error", "Time (ms)", "Position Error(cm)",legends, results[0]);
        p_time_Panel.setBounds(0, 0, 100, 100);

        ChartPanel v_time_Panel = MyChart.error_plot("Velocity Error", "Time (ms)", "Velocity Error(cm/s)",legends, results[1]);
        v_time_Panel.setBounds(0, 1000, 100, 100);

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

        p_time_Frame.setContentPane(p_time_Panel);
        p_time_Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        p_time_Frame.pack();

        v_time_Frame.setContentPane(v_time_Panel);
        v_time_Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        v_time_Frame.pack();

        pFrame.setVisible(true);
        vFrame.setVisible(true);
        // p_time_Frame.setVisible(true);
        // v_time_Frame.setVisible(true);
    }

    public static void myGUI(ArrayList<Double>[][] results) {
        new myGUI(results);
    }
}
