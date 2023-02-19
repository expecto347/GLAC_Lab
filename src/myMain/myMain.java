package myMain;

import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartPanel;
import simulation.*;
import utils.MyChart;

import javax.swing.*;

public class myMain {
    public static void main(String[] args) throws IOException {
        Line line = new Line(10, 10, 10,1000);
        ArrayList<Double>[][] results = Simulation.track(line);
        myGUI.myGUI(results);
    }
}