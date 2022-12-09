package myMain;

import java.util.ArrayList;

import org.jfree.chart.ChartPanel;
import simulation.*;
import utils.MyChart;

import javax.swing.*;

public class myMain {
    public static void main(String[] args) {
        ArrayList<Double>[] List = new ArrayList[1];
        List[0] = new ArrayList<Double>();
        ArrayList<Double>[][] results;
        int v_x;
        int v_y;
        int v_z;
        for(v_x = 5 ; v_x < 10; v_x++){
            for(v_y = 5; v_y < 10; v_y++){
                for(v_z = 5; v_z < 10; v_z++){
                    Line line = new Line(v_x, v_y, v_z, 1000);
                    Simulation.track(line);
                    for(int i = 0; i < Simulation.xList.size(); i++)
                        List[0].add(Simulation.xList.get(i));
                    System.out.print("v_x = ");
                    System.out.print(v_x);
                    System.out.print(" v_y = ");
                    System.out.print(v_y);
                    System.out.print(" v_z = ");
                    System.out.print(v_z);
                    System.out.print("\n");
                }
            }
        }
        JFrame Frame = new JFrame("Frame");


        String[] legends = {"Line"};

        ChartPanel Panel = MyChart.cdfPlot("CDF of Absolute Error", "Position Error (cm)", legends, List);
        Panel.setBounds(0, 0, 100, 100);

        Frame.setContentPane(Panel);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.pack();

        Frame.setVisible(true);
    }
}