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
 * 扩展卡尔曼滤波器，用于描述轨迹
 * @author Wang
 */
class EKF implements Comparable<EKF>, Cloneable {

    static final Matrix Q, R;//噪声

    static {//设置噪声
        double sigmaP = Config.getSigmaP(), sigmaV = Config.getSigmaV();
        //设置转移噪声
        Q = Matrix.identity(6, 6);
        Q.set(0, 0, sigmaP * sigmaP);//x方向位置的方差
        Q.set(1, 1, sigmaP * sigmaP);//y方向位置的方差
        Q.set(2, 2, sigmaP * sigmaP);//z方向位置的方差
        Q.set(3, 3, sigmaV * sigmaV);//x方向速度的方差
        Q.set(4, 4, sigmaV * sigmaV);//y方向速度的方差
        Q.set(5, 5, sigmaV * sigmaV);//z方向速度的方差
        //设置观测噪声
        R = Matrix.identity(1, 1).times(2 * sigmaP * sigmaP);
    }

    private final ArrayList<StateStamp> stateList;//状态列表
    private double weight;//权重

    /**
     * 生成函数，用于评估轨迹更新的精度。默认权重为1
     *
     * @param s 第一个状态帧
     */
    public EKF(StateStamp s) {
        stateList = new ArrayList<>();
        stateList.add(s);
        weight = 1.0;
    }

    /**
     * 生成函数
     *
     * @param s 第一个状态帧
     * @param obsDis 观测距离，用于初始赋权
     */
    public EKF(StateStamp s, double[] obsDis) {
        stateList = new ArrayList<>();
        stateList.add(s);
        weight = assignInitialWeight(new Coordinate(s.getStateVector().get(0, 0), s.getStateVector().get(1, 0), s.getStateVector().get(2, 0)), obsDis);
    }

    /**
     * 卡尔曼滤波更新
     *
     * @param td 观测
     */
    public void update(TagData td) {
        //获取前一时刻的分布
        StateStamp stamp = this.get(-1);
        Matrix oldstate = stamp.getStateVector();
        Matrix oldcov = stamp.getCovMatrix();
        //设置状态转移矩阵A
        Matrix A = getAMatrix((td.getTime() - stamp.getTime())*0.001);
        //使用状态转移方程估计新的分布
        Matrix newstate = A.times(oldstate);
        Matrix newcov = A.times(oldcov).times(A.transpose()).plus(Q);
        //求观测矩阵
        Matrix C = getObserveMatrix(newstate, td.getAntennaIndex());
        //计算得到残差
        double obsDis = (-td.getPhase() / Math.PI) * Config.getSemiLambda();
        Matrix diff = new Matrix(1, 1, getResidual(td.getAntennaIndex(), newstate, obsDis));
        //计算卡尔曼增益
        Matrix gain = C.times(newcov).times(C.transpose()).plus(R);
        gain = newcov.times(C.transpose()).times(gain.inverse());
        //估计综合后的状态和协方差矩阵
        newstate = newstate.plus(gain.times(diff));
        newcov = Matrix.identity(6, 6).minus(gain.times(C)).times(newcov);
        //生成状态戳并加入历史列表
        stamp = new StateStamp(td.getTime(), newstate, newcov);
        this.stateList.add(stamp);
        //为轨迹赋予权重
        this.weight = assignFollowupWeight(td.getAntennaIndex(), obsDis);
    }

    /**
     * 获取指定的状态帧。
     *
     * @param index 索引
     * @return 索引对应的状态帧
     */
    public StateStamp get(int index) {
        if (index < 0) {
            index += this.stateList.size();
        }
        return this.stateList.get(index);
    }

    /**
     * 获取权重
     *
     * @return 权重
     */
    public double getWeight() {
        return weight;
    }

    /**
     * 设置权重
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(EKF t) {
        if (this.getWeight() == t.getWeight()) {
            return 0;
        } else if (this.getWeight() > t.getWeight()) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * 生成状态转移矩阵A
     *
     * @param timestep 时间间隔
     * @return 状态转移矩阵
     */
    private Matrix getAMatrix(double timestep) {
        Matrix A = Matrix.identity(6, 6);
        A.set(0, 3, timestep);
        A.set(1, 4, timestep);
        A.set(2, 5, timestep);
        return A;
    }

    /**
     * 求观测矩阵
     *
     * @param state 状态向量
     * @param antIndex 给出观测的天线编号
     * @return 观测矩阵
     *
     */
    private Matrix getObserveMatrix(Matrix state, int antIndex) {
        double d = MyUtils.dist(state.get(0, 0), state.get(1, 0), state.get(2, 0), Config.getX(antIndex), Config.getY(antIndex), Config.getZ(antIndex));
        Matrix ans = new Matrix(1, 6);
        ans.set(0, 0, (state.get(0, 0) - Config.getX(antIndex)) / d);
        ans.set(0, 1, (state.get(1, 0) - Config.getY(antIndex)) / d);
        ans.set(0, 2, (state.get(2, 0) - Config.getZ(antIndex)) / d);
        return ans;
    }

    /**
     * 计算观测距离和预测距离的差值（残差）。<br>
     * 预测距离：初步估计的位置与天线的欧氏距离。<br>
     * 观测距离：将相位换算成距离，并增加若干个半波长（展开），使其与预测距离最接近。
     *
     * @param ano 做出观测的天线编号
     * @param state 初步估计的状态向量，包含位置信息
     * @return 残差
     */
    private double getResidual(int ano, Matrix state, double obsDist) {
        double theoryDist = 2 * MyUtils.dist(Config.getX(ano), Config.getY(ano), Config.getZ(ano), state.get(0, 0), state.get(1, 0), state.get(2,0));//天线到估计点的距离
        long n = Math.round((theoryDist - obsDist) / Config.getSemiLambda());
        obsDist = obsDist + n * Config.getSemiLambda();//观测距离
        double diff = (obsDist - theoryDist) / 2.0;
        return diff;
    }

    /**
     * 输入某一点的坐标，返回发送天线经过它到各个接收天线的距离（对波长取模）
     *
     * @param p 坐标
     * @return 长度为k的数组，发送天线经过它到各个接收天线的距离
     */
    private double[] dist(Coordinate p) {
        double[] ds = new double[Config.getK()];
        for (int i = 0; i < Config.getK(); i++) {
            double t1 = p.getX() - Config.getX(i), t2 = p.getY() - Config.getY(i), t3 = p.getZ() -Config.getZ(i);
            ds[i] = 2 * Math.sqrt(t1 * t1 + t2 * t2 + t3 * t3);//再加上点到接收天线的距离
            ds[i] = ds[i] - Math.floor(ds[i] / Config.getSemiLambda()) * Config.getSemiLambda();//距离模波长
        }
        return ds;
    }

    /**
     * 指定采样点的初始权重
     *
     * @param x 采样点坐标
     * @return 权重
     */
    private double assignInitialWeight(Coordinate x, double observeDis[]) {
        double ds[] = dist(x);
        double w = 1.0;
        for (int i = 0; i < Config.getK(); i++) {
            long n = Math.round((ds[i] - observeDis[i]) / Config.getSemiLambda());
            w *= MyUtils.getNormalDistribution(ds[i], Config.getSigmaP(), observeDis[i] + n * Config.getSemiLambda());
        }
        return w;
    }

    /**
     * 在后续更新中，对轨迹进行加权
     *
     * @param antIndex 提供观测的天线编号
     * @param obsDis 观测距离
     * @return 权重
     */
    private double assignFollowupWeight(int antIndex, double obsDis) {
        StateStamp s0 = this.get(-2);//得到倒数第二个状态
        StateStamp s1 = this.get(-1);//得到最后一个状态
        double w = 1.0;
        double residual = getResidual(antIndex, s1.getStateVector(), obsDis);//得到残差
        w = w * MyUtils.getNormalDistribution(0, Config.getSigmaP(), residual * 2);//使用距离的观测进行加权
        double timestep = (s1.getTime() - s0.getTime()) * 0.001;//时间差
        w *= MyUtils.getNormalDistribution(0, Config.getSigmaP(), MyUtils.dist(s1.getStateVector().get(0, 0), s1.getStateVector().get(1, 0),
                 s1.getStateVector().get(2, 0), s0.getStateVector().get(0, 0) + timestep * s0.getStateVector().get(3, 0),
                s0.getStateVector().get(1, 0) + timestep * s0.getStateVector().get(4, 0), s0.getStateVector().get(2, 0)+timestep * s0.getStateVector().get(5, 0)));//使用距离的估计进行加权
        w *= MyUtils.getNormalDistribution(0, Config.getSigmaV(), MyUtils.dist(s0.getStateVector().get(3, 0), s0.getStateVector().get(4, 0), s0.getStateVector().get(5, 0),
                s1.getStateVector().get(3, 0), s1.getStateVector().get(4, 0), s1.getStateVector().get(5, 0)));//使用速度变化进行加权
        return w * this.weight;
    }

    /**
     * 获取卡尔曼滤波得到的运动轨迹
     *
     * @return 运动轨迹
     */
    public ArrayList<Coordinate> getTrajectory() {
        ArrayList<Coordinate> list = new ArrayList<>();
        for (StateStamp e : stateList) {
            Matrix v = e.getStateVector();
            list.add(new Coordinate(v.get(0, 0), v.get(1, 0), v.get(2, 0)));
        }
        return list;
    }

    /**
     * 获取卡尔曼滤波得到的速度
     *
     * @return 运动轨迹
     */
    public ArrayList<Coordinate> getVelocity() {
        ArrayList<Coordinate> list = new ArrayList<>();
        for (StateStamp e : stateList) {
            Matrix v = e.getStateVector();
            list.add(new Coordinate(v.get(3, 0), v.get(4, 0), v.get(5, 0)));
        }
        return list;
    }

}
