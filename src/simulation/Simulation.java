/*
 * To change this licence header, choose Licence Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;


import Jama.Matrix;
import glac.Config;
import glac.Coordinate;
import glac.TagData;
import glac.HMM;
import org.jfree.chart.ChartPanel;
import utils.MyChart;
import utils.MyRandom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
// import org.apache.commons.lang3.tuple.Pair;
import utils.MyUtils;

import javax.swing.*;


/**
 * 仿真测试类，用于评估系统的各项性能。
 *
 * @author Wang
 */
public class Simulation {

    static double sigma = 0.01; //仿真生成的相位的标准差
    static MyRandom random = new MyRandom(); //随机数生成器

    static public ArrayList<Double> xList = new ArrayList<>();

    public static ArrayList<Double>[][] track(Shape shape) throws IOException {
        ArrayList<Double>[][] lists = new ArrayList[3][4];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                lists[i][j] = new ArrayList<>();
            }
        } //初始化List

        int error = 0;
        int simulate_times = 100;
        HMM hmm = new HMM();
        for (int t = 0; t < simulate_times; t++) {
            System.out.print("正在进行第");
            System.out.print(t+1);
            System.out.println("次仿真");
            hmm.clear();
            int i = 0;
            ArrayList<StateStamp> g = shape.generate();
//            genPhase_file("output.txt", g);
//            BufferedReader reader = new BufferedReader(new java.io.FileReader("output.txt"));
            for (StateStamp s : g) {
                double ph1[] = genPhase(s.getStateVector().get(0, 0), s.getStateVector().get(1, 0), s.getStateVector().get(2, 0), i);//获得相位值，i是天线标号
                double ph = ph1[0];
                double[] x = new double[7];
                x[0] = s.getStateVector().get(0, 0);
                x[1] = s.getStateVector().get(1, 0);
                x[2] = s.getStateVector().get(2, 0);
                x[3] = s.getStateVector().get(3, 0);
                x[4] = s.getStateVector().get(4, 0);
                x[5] = s.getStateVector().get(5, 0);
                x[6] = Math.abs(ph1[1] - ph1[0]) / ph1[1]; // 相对误差
                TagData td = new TagData(i, s.getTime(), ph, x);
                hmm.add(td);
                i = (i + 1) % Config.getK();
            }
            ArrayList<Coordinate> tr = hmm.getTrajectory(0);
            ArrayList<Coordinate> v = hmm.getVelocity();

//            BufferedWriter writer = new BufferedWriter(new FileWriter("trajectory1.csv"));
//            writer.write("x,y,z\n");
//            for (Coordinate c : tr) {
//                writer.write(c.getX() + "," + c.getY() + "," + c.getZ() + "\n");
//            }

//            ArrayList<Coordinate> tr2 = hmm.getTrajectory(1);
//            BufferedWriter writer2 = new BufferedWriter(new FileWriter("trajectory2.csv"));
//            writer2.write("x,y,z\n");
//            for (Coordinate c : tr2) {
//                writer2.write(c.getX() + "," + c.getY() + "," + c.getZ() + "\n");
//            }
//            writer2.close();

//            ArrayList<Coordinate> tr3 = hmm.getTrajectory(2);
//            BufferedWriter writer3 = new BufferedWriter(new FileWriter("trajectory3.csv"));
//            writer3.write("x,y,z\n");
//            for (Coordinate c : tr3) {
//                writer3.write(c.getX() + "," + c.getY() + "," + c.getZ() + "\n");
//            }
//            writer3.close();

            
            if (tr == null) {
                t--;
                continue;
            }

            for (int k = 0; k < g.size(); k++) {
                Matrix e = getError(tr.get(k), v.get(k), g.get(k).getStateVector());
                for (i = 0; i < 2; i++) {
                    for (int j = 0; j < 4; j++) {
                        lists[i][j].add(e.get(i, j));
                    }
                }
            }

            Matrix e = getError(tr.get(0), v.get(0), g.get(0).getStateVector());
            for(i = 0; i < 4; i++) {
                lists[2][i].add(e.get(0, i));
            }

//            for (i = 0; i < hmm.error_p1[3].size(); i++) { //获得相对误差
//                xList.add(hmm.error_p1[3].get(i));
//            }
//            error += hmm.number_error;
            // System.out.println(error);
        }

//        System.out.println(error);
//
//        // Plot the CDF of the Xlist
//        JFrame Frame = new JFrame("Frame");
//        String[] legends = {"Absolute Error"};
//        //将hmm.error转换成ArrayList形式
//        ArrayList<Double>[] error_n = new ArrayList[1];
//        ArrayList<Double>[] error_p = new ArrayList[2];
//
//        error_n[0] = new ArrayList<>();
//        error_p[0] = new ArrayList<>();
//        error_p[1] = new ArrayList<>(); //initial
//
//        for (int i = 0; i < hmm.error_p1[0].size(); i++) {
//            error_n[0].add(hmm.error_p1[0].get(i));
//            error_p[0].add(hmm.error_p1[1].get(i));
//            error_p[1].add(hmm.error_p1[2].get(i));
//        }
//
//        JFrame nFrame = new JFrame("pFrame");
//        JFrame pFrame = new JFrame("vFrame");
//
//        ChartPanel nPanel = MyChart.error_p("最佳轨迹权重在所有轨迹的排名", "Update number", "n", new String[]{"n"}, error_n);
//        ChartPanel pPanel = MyChart.error_p("相对误差", "Update number", "相对误差", new String[]{"最准确位置的相对误差", "权重最高的轨迹的相对误差"}, error_p);
//        pPanel.setBounds(0, 0, 100, 100);
//        nPanel.setBounds(0, 1000, 100, 100);
//
//        pFrame.setContentPane(pPanel);
//        pFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        pFrame.pack();
//        pFrame.setVisible(true);
//
//        nFrame.setContentPane(nPanel);
//        nFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        nFrame.pack();
//        nFrame.setVisible(true);

        System.out.println("仿真结束");
        return lists;
    }

    /**
     * 生成误差值，其中误差值的第一行为位置误差，第二行为速度误差。
     * @param p 估计的位置
     * @param v 估计的速度
     * @param g 真实的状态向量
     * @return 误差值
     */
    private static Matrix getError(Coordinate p, Coordinate v, Matrix g) {
        double[][] mat = new double[2][4];
        mat[0][0] = Math.abs(g.get(0, 0) - p.getX());
        mat[0][1] = Math.abs(g.get(1, 0) - p.getY());
        mat[0][2] = Math.abs(g.get(2, 0) - p.getZ());
        mat[0][3] = MyUtils.dist(g.get(0, 0), g.get(1, 0), g.get(2, 0), p.getX(), p.getY(), p.getZ());
        mat[1][0] = Math.abs(g.get(3, 0) - v.getX());
        mat[1][1] = Math.abs(g.get(4, 0) - v.getY());
        mat[1][2] = Math.abs(g.get(5, 0) - v.getZ());
        mat[1][3] = MyUtils.dist(g.get(3, 0), g.get(4, 0), g.get(5, 0), v.getX(), v.getY(), v.getZ());
        return new Matrix(mat);
    }

    /**
     * 指定标签的位置，生成指定天线的相位(模pi)
     *
     * @param x 标签X坐标
     * @param y 标签Y坐标
     * @param z 标签Z坐标
     * @param ano 天线标号
     * @return 相位
     */
    private static double[] genPhase(double x, double y, double z, int ano) {
        double d = -MyUtils.dist(x, y, z, Config.getX(ano), Config.getY(ano), Config.getZ(ano)) * 2; //两倍的天线和tag的距离，负号的作用
        double phase[] = new double[2];
        phase[0] = random.nextGaussian(d * Math.PI / Config.getSemiLambda(), sigma);
        phase[0] = phase[0] - Math.floor(phase[0] / Math.PI) * Math.PI; //mod pi
        phase[1] = d * Math.PI / Config.getSemiLambda();
        phase[1] = phase[1] - Math.floor(phase[1] / Math.PI) * Math.PI; //mod pi
        return phase;
    }

    private static void genPhase_file(String file, ArrayList<StateStamp> g)
        throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        int i = 0;
        for (StateStamp s : g) {
            double ph1[] = genPhase(s.getStateVector().get(0, 0), s.getStateVector().get(1, 0), s.getStateVector().get(2, 0), i);//获得相位值，i是天线标号
            writer.append(String.valueOf(ph1[0])); //写入相位值
            writer.append(",");
            writer.append(String.valueOf(ph1[1])); //写入相位值
            writer.append(",");
            writer.append(String.valueOf(s.getStateVector().get(0, 0)));
            writer.append(",");
            writer.append(String.valueOf(s.getStateVector().get(1, 0)));
            writer.append(",");
            writer.append(String.valueOf(s.getStateVector().get(2, 0)));
            writer.append(",");
            writer.append(String.valueOf(s.getStateVector().get(3, 0)));
            writer.append(",");
            writer.append(String.valueOf(s.getStateVector().get(4, 0)));
            writer.append(",");
            writer.append(String.valueOf(s.getStateVector().get(5, 0)));
            writer.append(",");
            writer.append(String.valueOf(s.getTime()));
            writer.append("\n");
            i = (i + 1) % Config.getK();
        }
        writer.close();
    }
}
