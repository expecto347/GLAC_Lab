package utils;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Wang
 */
public class MyChart {

    /**
     * 使用指定的数据创建XYSeries
     *
     * @param lineName 数据标签
     * @param x X轴数据
     * @param y Y轴数据
     * @return 创建好的XYSeries
     */
    private static XYSeries createXYSeries(String lineName, double[] x, double y[]) {
        XYSeries serie = new XYSeries(lineName);
        int n = x.length;
        for (int i = 0; i < n; i++) {
            serie.add(x[i], y[i]);
        }
        return serie;
    }

    public static ChartPanel cdfPlot(String title, String axisLabel, String[] legends, ArrayList<Double>[] data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        int m = data.length;
        double max = data[0].get(0);
        for (int i = 0; i < m; i++) {
            Collections.sort(data[i]);//排序原始数据
            int n = data[i].size();
            double x[] = new double[n + 1], y[] = new double[n + 1];
            x[0] = data[i].get(0);
            y[0] = 0;
            for (int j = 1; j <= n; j++) {
                y[j] = j / (n * 1.0);
                x[j] = data[i].get(j - 1);
            }
            max = Math.max(max, data[i].get(n - 1));
            //组织数据
            if (i < legends.length) {
                dataset.addSeries(createXYSeries(legends[i], x, y));
            } else {
                dataset.addSeries(createXYSeries("Line" + i, x, y));
            }
        }
        JFreeChart chart = ChartFactory.createXYLineChart(title, axisLabel, "CDF", dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chart.getXYPlot().getDomainAxis().setUpperBound(max);//设置X轴范围
        chart.getXYPlot().getRangeAxis().setUpperBound(1.0);//设置Y轴范围
        //设置字体
        chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Helvetica",Font.PLAIN,20));
        chart.getXYPlot().getDomainAxis().setTickLabelFont(new Font("Helvetica",Font.PLAIN,18));
        chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Helvetica",Font.PLAIN,20));
        chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("Helvetica",Font.PLAIN,18));
        chart.getLegend().setItemFont(new Font("Helvetica",Font.PLAIN,20));   
        return chartPanel;
    }

    public static ChartPanel track_plot(String title, ArrayList<Pair<Double, Double>> tr) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        double x[] = new double[tr.size()];
        double y[] = new double[tr.size()];

        double max_x = tr.get(0).getLeft();
        double min_x = tr.get(0).getLeft();

        double max_y = tr.get(0).getRight();
        double min_y = tr.get(0).getRight();

        for (int i = 0; i < tr.size(); i++) {
            x[i] = tr.get(i).getLeft();
            y[i] = tr.get(i).getRight();
            if(x[i] > max_x) max_x = x[i];
            if(x[i] < min_x) min_x = x[i];
            if(y[i] > max_y) max_y = y[i];
            if(y[i] < min_y) min_y = y[i];
        }

        dataset.addSeries(createXYSeries("track", x, y));

        JFreeChart chart = ChartFactory.createScatterPlot(title, "x", "y", dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chart.getXYPlot().getDomainAxis().setUpperBound(max_x);
        chart.getXYPlot().getDomainAxis().setLowerBound(min_x);//设置X轴范围
        chart.getXYPlot().getRangeAxis().setUpperBound(max_y);//设置Y轴范围
        chart.getXYPlot().getRangeAxis().setLowerBound(min_y);//设置Y轴范围
        //设置字体
        chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Helvetica", Font.PLAIN, 20));
        chart.getXYPlot().getDomainAxis().setTickLabelFont(new Font("Helvetica", Font.PLAIN, 18));
        chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Helvetica", Font.PLAIN, 20));
        chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("Helvetica", Font.PLAIN, 18));
        chart.getLegend().setItemFont(new Font("Helvetica", Font.PLAIN, 20));

        return chartPanel;
    }

    public static ChartPanel error_plot(String title, String title_x, String title_y, String[] legends, ArrayList<Double>[] data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        int a = 0;
        for (int j = 0; j < data.length; j++) {
            int n = data[j].size();
            double x[] = new double[n + 1], y[] = new double[n + 1];
            for(int i = 0; i < 41; i++) x[i] = i * 25;
            for (int i = 0; i < n; i++) {
                if(i % 41 == 0) a++;
                y[i % 41] = data[j].get(i);
            }

            for(int i = 0; i < 41; i++){
                y[i] = y[i] / a;
            }

            //组织数据
            if (j < legends.length) {
                dataset.addSeries(createXYSeries(legends[j], x, y));
            } else {
                dataset.addSeries(createXYSeries("Line" + j, x, y));
            }
        }
    JFreeChart chart = ChartFactory.createXYLineChart(title, title_x, title_y, dataset);
    ChartPanel chartPanel = new ChartPanel(chart);
    chart.getXYPlot().getDomainAxis().setLowerBound(0);
    // chart.getXYPlot().getDomainAxis().setUpperBound(max);//设置X轴范围
    chart.getXYPlot().getRangeAxis().setUpperBound(0.2);//设置Y轴范围
    //设置字体
    chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Helvetica",Font.PLAIN,20));
    chart.getXYPlot().getDomainAxis().setTickLabelFont(new Font("Helvetica",Font.PLAIN,18));
    chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Helvetica",Font.PLAIN,20));
    chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("Helvetica",Font.PLAIN,18));
    chart.getLegend().setItemFont(new Font("Helvetica",Font.PLAIN,20));
    return chartPanel;
    }
    public static ChartPanel error_p(String title, String xLabel, String yLabel,String[] legends, ArrayList<Double>[] data) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        int m = data.length;
        for (int i = 0; i < m; i++) {
            int n = data[i].size();
            double x[] = new double[n + 1], y[] = new double[n + 1];
            for (int j = 0; j < n; j++) {
                y[j] = data[i].get(j);
                x[j] = j + 1;
            }
            //组织数据
            if (i < legends.length) {
                dataset.addSeries(createXYSeries(legends[i], x, y));
            } else {
                dataset.addSeries(createXYSeries("Line" + i, x, y));
            }
        }
        JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel , yLabel, dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        // chart.getXYPlot().getDomainAxis().setUpperBound(41);//设置X轴范围
        // chart.getXYPlot().getRangeAxis().setUpperBound(1.0);//设置Y轴范围
        //设置字体
        chart.getXYPlot().getDomainAxis().setLabelFont(new Font("Helvetica",Font.PLAIN,20));
        chart.getXYPlot().getDomainAxis().setTickLabelFont(new Font("Helvetica",Font.PLAIN,18));
        chart.getXYPlot().getRangeAxis().setLabelFont(new Font("Helvetica",Font.PLAIN,20));
        chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("Helvetica",Font.PLAIN,18));
        chart.getLegend().setItemFont(new Font("Helvetica",Font.PLAIN,20));
        return chartPanel;
    }
}
