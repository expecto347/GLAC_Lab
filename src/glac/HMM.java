/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package glac;

import Jama.Matrix;
import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A Hidden Markov Model used to describe the trajectory tracking.<br>
 * We can provide phase observations to achieve tracking.<br>
 * 用于描述轨迹追踪的隐马尔可夫模型，通过向其提供相位观测来实现追踪。
 *
 * @author Wang
 */
public class HMM {

    private ArrayList<EKF> trajectories;//轨迹
    private ArrayList<TagData> tagDatas;//标签读数  
    private ArrayList<TagData>[] rawDatas;//用于初始插值的原始数据
    private boolean inited;//初始化标记

    /**
     * 构造器(Constructor)
     */
    public HMM() {
        clear();
    }

    /**
     * 清理内部数据，使其可以进行下一次追踪<br>
     * Clean up the private data so that it can be used for tracking for the
     * next time
     */
    public final void clear() {
        this.inited = false;
        this.trajectories = new ArrayList<>();
        this.tagDatas = new ArrayList<>();
        int k = Config.getK();
        this.rawDatas = new ArrayList[k];
        for (int i = 0; i < k; i++) {
            rawDatas[i] = new ArrayList<>();
        }
    }

    /**
     * 向HMM提供新的观测
     * Provide new tag reading to HMM
     *
     * @param td 标签观测(Tag reading)
     */
    public void add(TagData td) {
        tagDatas.add(td);
        if (inited) {
            for (EKF tr : trajectories) {
                tr.update(td);
            }//更新所有的卡尔曼滤波
            normalizeWeight();//对它们的权重归一化
        } else {
            init(td);//初始化
        }
    }

    /**
     * 初始化
     *
     * @param td 新的观测
     */
    private void init(TagData td) {
        rawDatas[td.getAntennaIndex()].add(td);
        long t = tagDatas.get(0).getTime();//得到第一个相位值的时刻，即插值的参考时刻
        int k = Config.getK();
        for (int i = 0; i < k; i++) {
            if (rawDatas[i].size() < 2) {
                return;
            }
        }//如果插值需要的原始数据不足，返回
        double p[] = new double[k];
        double v[] = new double[k];
        for (int i = 0; i < k; i++) {
            p[i] = interpolate(t, rawDatas[i].get(0), rawDatas[i].get(1));//插值
            v[i] = getProjectedVelocity(rawDatas[i].get(0), rawDatas[i].get(1));//估计投影速度
        }
        initialEstimate(t, p, v, td);
        //依次更新卡尔曼滤波
        for (int i = 1; i < tagDatas.size(); i++) {
            for (EKF tr : trajectories) {
                tr.update(tagDatas.get(i));
            }
        }
        normalizeWeight();//对权重归一化
        inited = true;
        rawDatas = null;
    }

    /**
     * 根据两个折叠后的相位求投影速度
     *
     * @param p1 前一时刻的观测
     * @param p2 当前时刻的观测
     * @return 投影速度
     */
    private double getProjectedVelocity(TagData p1, TagData p2) {
        double ph1 = p1.getPhase(), ph2 = unwrap(ph1, p2.getPhase());
        double timestep = (p2.getTime() - p1.getTime())*0.001;
        double diff = (ph1 - ph2) / Math.PI * Config.getSemiLambda();//注意，这是往返的距离差
        return diff / (2 * timestep);
    }

    /**
     * 拉格朗日插值。<br>
     * 在已知n个插值点的情况下，得到n-1阶插值多项式函数f，并返回插值函数在t处的值。
     *
     * @param t 插值函数自变量的取值
     * @param p 不定长参数，表示已知的插值点及其对应的函数值
     * @return 插值函数在t处的值f(t)
     */
    private double interpolate(long t, TagData... p) {
        int n = p.length;
        long x[] = new long[n];
        double y[] = new double[n];
        x[0] = p[0].getTime();
        y[0] = p[0].getPhase();
        for (int i = 1; i < n; i++) {
            x[i] = p[i].getTime();
            y[i] = unwrap(y[i - 1], p[i].getPhase());
        }
        double f = 0;
        for (int i = 0; i < n; i++) {
            double temp = y[i];
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                temp = temp * (t - x[j]) / (x[i] - x[j]); //拉格朗日插值法
            }
            f += temp;
        }
        return f;
    }

    /**
     * 初始估计
     *
     * @param time 插值的参考时刻
     * @param phase 各天线插值后的同步相位
     * @param v 各天线的投影速度
     */
    private void initialEstimate(long time, double phase[], double v[], TagData td) {
        //将输入的相位转为距离
        int k = Config.getK();
        double observeDis[] = new double[k];
        for (int i = 0; i < k; i++) {
            observeDis[i] = -phase[i] / Math.PI * Config.getSemiLambda();
        }
        //初始位置估计
        ArrayList<Coordinate> initPos = initialPositionEstimate(observeDis);
        //为每一个初始位置计算初始速度，组成初始状态，并启动对应的EKF
        /**
        System.out.print(tagDatas.get(0).getTime());
        System.out.print(" ");
        System.out.print(tagDatas.get(0).getStateStamp()[0]);
        System.out.print(" ");
        System.out.print(tagDatas.get(0).getStateStamp()[1]);
        System.out.print(" ");
        System.out.print(tagDatas.get(0).getStateStamp()[2]);
        System.out.print("\n");
        double min = Double.MAX_VALUE;
        for (Coordinate t : initPos){
            if((t.getX() - td.getStateStamp()[0])*(t.getX() - td.getStateStamp()[0]) + (t.getY() - td.getStateStamp()[1])*(t.getY() - td.getStateStamp()[1]) + (t.getZ() - td.getStateStamp()[2])*(t.getZ() - td.getStateStamp()[2]) < min){
                min = (t.getX() - td.getStateStamp()[0])*(t.getX() - td.getStateStamp()[0]) + (t.getY() - td.getStateStamp()[1])*(t.getY() - td.getStateStamp()[1]) + (t.getZ() - td.getStateStamp()[2])*(t.getZ() - td.getStateStamp()[2]);
                Coordinate pv = initialVelocityEstimate(v, t);
                System.out.print(t.getX());
                System.out.print(" ");
                System.out.print(t.getY());
                System.out.print(" ");
                System.out.print(t.getZ());
                System.out.print(" ");
                System.out.print(pv.getX());
                System.out.print(" ");
                System.out.print(pv.getY());
                System.out.print(" ");
                System.out.print(pv.getZ());
                System.out.print("\n");
            }
        }调试内容**/
        for (Coordinate p : initPos) {
            //初始速度估计
            Coordinate pv = initialVelocityEstimate(v, p);
            //组合为初始状态
            double A[][] = {{p.getX()}, {p.getY()}, {p.getZ()}, {pv.getX()}, {pv.getY()}, {pv.getZ()}};
            StateStamp e = new StateStamp(time, new Matrix(A), EKF.Q);
            //启动对应的EKF
            trajectories.add(new EKF(e, observeDis));
        }
        ArrayList<Coordinate> tr1 = getTrajectory();
        //权重归一化
        normalizeWeight();
    }

    /**
     * 初始位置估计
     *
     * @param observeDis 所有天线的观测距离（模pi）
     * @return 估计得到的一系列初始位置
     */
    private ArrayList<Coordinate> initialPositionEstimate(double observeDis[]) {
        //六重循环遍历所有的天线组（包括四根天线）以及四元组S
        ArrayList<Coordinate> initPos = new ArrayList<>();
        int k = Config.getK();
        for (int a1 = 0; a1 < k; a1++) {//天线1
            for (int a2 = a1 + 1; a2 < k; a2++) {//天线2
                for (int a3 = a2 + 1; a3 < k; a3++) {//天线3
                    for (int a4 = a3 + 1; a4 < k; a4++) {//天线4
                        for (int s1 = 0; s1 <= Config.getMaxS(); s1++) {//天线1对应的整数波长
                            for (int s2 = 0; s2 <= Config.getMaxS(); s2++) {//天线2对应的整数波长
                                for (int s3 = 0; s3 <= Config.getMaxS(); s3++) {//天线3对应的整数波长
                                    for (int s4 = 0; s4 <= Config.getMaxS(); s4++) {//天线4对应的整数波长
                                        Coordinate p = Trilateration.Trilateration(a1, a2, a3, a4,(observeDis[a1] + s1 * Config.getSemiLambda())/2,
                                                (observeDis[a2] + s2 * Config.getSemiLambda())/2, (observeDis[a3] + s3 * Config.getSemiLambda())/2,
                                                (observeDis[a4] + s4 * Config.getSemiLambda())/2);
                                        if (p != null) {
                                            initPos.add(p);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return initPos;
    }

    /**
     * 速度估计。已知各个天线连线方向的投影速度，使用最小二乘法估计实际速度。
     *
     * @param v 各天线连线方向的投影速度
     * @param loc 标签位置
     * @return 估计的二维速度
     */
    private Coordinate initialVelocityEstimate(double v[], Coordinate loc) {
        double x[][] =new double[Config.getK()][3];
        double y[] = new double[Config.getK()];
        for (int i = 0; i < Config.getK(); i++) {
            double ex = loc.getX() - Config.getX(i);
            double ey = loc.getY() - Config.getY(i);
            double ez = loc.getZ() - Config.getZ(i);
            double e = Math.sqrt(ex * ex + ey * ey + ez * ez);
            ex = ex / e;
            ey = ey / e;
            ez = ez / e;
            //得到单位向量
            x[i][0] = 1;
            x[i][1] = ey/ex;
            x[i][2] = ez/ex;
            y[i] = v[i]/ex;
        }
        return MultipleLinearRegression.main(x,y);
    }

    /**
     * 最小二乘法进行线性拟合。拟合的式子为ax+by+c=z。
     *
     * @param x 用于拟合的x值列表
     * @param y 用于拟合的y值列表
     * @param z 用于拟合的z值列表
     * @return 拟合得到的参数(a,b,c)

    private Coordinate leastSquares(double x[], double y[], double z[]){
        double a = 0, b = 0, c = 0;
        double sumx = 0, sumy = 0, sumz = 0, sumxy = 0, sumxz = 0, sumyz = 0, sumx2 = 0, sumy2 = 0;
        for (int i = 0; i < x.length; i++) {
            sumx += x[i];
            sumy += y[i];
            sumz += z[i];
            sumxy += x[i] * y[i];
            sumxz += x[i] * z[i];
            sumyz += y[i] * z[i];
            sumx2 += x[i] * x[i];
            sumy2 += y[i] * y[i];
        }
        a = (sumx * sumy * sumz - sumx * sumyz - sumy * sumxz + sumz * sumxy) / (sumx * sumx * sumy - sumx * sumxy - sumy * sumx2 + sumxy * sumxy);
        b = (sumx * sumy * sumz - sumx * sumyz - sumy * sumxz + sumz * sumxy) / (sumx * sumx * sumy - sumx * sumxy - sumy * sumx2 + sumxy * sumxy);
        c = (sumz - a * sumx - b * sumy) / x.length;
        return new Coordinate(a, b, c);
    }

    */


    /**
     * 相位展开：将val增减若干个PI，得到展开后的值，使得它与base最接近。
     *
     * @param base 基准
     * @param val 将被展开的值
     * @return 展开后的结果
     */
    private double unwrap(double base, double val) {
        double temp = base - val;
        val = val + Math.floor(temp / Math.PI) * Math.PI;
        if (Math.abs(val - base) > Math.abs(val + Math.PI - base)) {
            val += Math.PI;
        }
        return val;
    }

    /**
     * 对轨迹权重进行归一化，并删除权重小于阈值的轨迹
     */
    private void normalizeWeight() {
        ArrayList<EKF> newList = new ArrayList<>();
        // double len;
        // int i;
        double total = 0;
        for (EKF tr : trajectories) {
            if (tr.getWeight() > Config.getStopThreshold()) {
                total += tr.getWeight();
                /**
                len = newList.size();
                i = 0;
                while(i < len){
                    if(tr.getWeight() < newList.get(i).getWeight()) i++;
                    else break;
                }
                newList.add(i , tr); //按顺序排列**/
                newList.add(tr);
            }
        }
        trajectories = newList;
        for (EKF tr : newList) {
            tr.setWeight(tr.getWeight() / total);
        }
    } //归一化，并对权重过小的轨迹剪枝

    /**
     * 获取权重最大的运动轨迹<br>
     * Get the trajectory with the highest probability
     *
     * @return 运动轨迹(The trajectory)
     */
    public ArrayList<Coordinate> getTrajectory() {
        if (trajectories.isEmpty()) {
            return null;
        }
        //找到权重最大的轨迹
        EKF picked = null;
        double max = 0;
        for (EKF tr : trajectories) {
            if (tr.getWeight() > max) {
                max = tr.getWeight();
                picked = tr;
            }
        }
        return picked.getTrajectory();
    }

    /**
     * 获取权重最大的轨迹对应的速度<br>
     * Get the velocity corresponding to the trajectory with the highest
     * probability
     *
     * @return 速度序列(The velocity sequence)
     */
    public ArrayList<Coordinate> getVelocity() {
        if (trajectories.isEmpty()) {
            return null;
        }
        //找到权重最大的轨迹
        EKF picked = null;
        double max = 0;
        for (EKF tr : trajectories) {
            if (tr.getWeight() > max) {
                max = tr.getWeight();
                picked = tr;
            }
        }
        return picked.getVelocity();
    }
    /*
    private static double getActualVelocity(TagData p1, TagData p2, int i) {
        double d1 = MyUtils.dist(p1.getStateStamp()[0], p1.getStateStamp()[1], p1.getStateStamp()[2], Config.getX(i), Config.getY(i), Config.getZ(i));
        double d2 = MyUtils.dist(p2.getStateStamp()[0], p2.getStateStamp()[1], p2.getStateStamp()[2], Config.getX(i), Config.getY(i), Config.getZ(i));

        return (d2 - d1) / (p2.getTime() - p1.getTime());
    }

     */
}