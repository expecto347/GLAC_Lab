package myMain;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.ChartPanel;
import simulation.StateStamp;
import utils.MyChart;

import javax.swing.*;
import java.util.ArrayList;

public class myGUI_draw {
    public myGUI_draw(ArrayList<StateStamp> statelist) {
        JFrame frame = new JFrame("draw");

        String[] legends = {"track"};
        // ChartPanel panel = MyChart.track_plot("track", statelist);
        // panel.setBounds(0, 0, 100, 100);

        // frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setVisible(true);
    }
}
