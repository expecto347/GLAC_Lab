package simulation;

import Jama.Matrix;

import java.util.ArrayList;
import static simulation.Simulation.random;

/**
 *  Helix
 */
public class Helix extends Shape{

    double v_z; // z方向速度
    double omega; // 角速度rad/s
    double theta; // 初始角度
    double r;
    long time;

    public Helix(double v_z, double omega, double theta, double r, long time){
        this.v_z = v_z;
        this.omega = omega;
        this.theta = theta;
        this.r = r;
        this.time = time;
    }

    /**
     * 生成螺旋线运动的状态序列
     *
     * 我们将拓展到三维，那么序列的每个元素是一个7*1的列向量，五个元素分别代表时间，X方向位置、Y方向位置、Z方向位置，X方向速度、Y方向速度、Z方向速度，单位为s、m和m/s；
     */

    @Override
    public ArrayList<StateStamp> generate() {
        ArrayList<StateStamp> statelist = new ArrayList<>();
        double x0 = random.nextDouble(0, 60);
        double y0 = random.nextDouble(0, 60);
        double z0 = random.nextDouble(0, 60);
        double x = x0+r*Math.cos(theta);
        double y = y0+r*Math.sin(theta);
        double z = z0; //初始化位置
        for(long t = 0; t <= time; t += 25){
            double[][] mat = {{x}, {y}, {z}, {omega*r*Math.cos(theta+omega*t)}, {v_z*Math.sin(theta+omega*t)}, {v_z}};
            statelist.add(new StateStamp(t, new Matrix(mat), null));
            x = x0+omega*r*Math.cos(theta+omega*t);
            y = y0+omega*r*Math.sin(theta+omega*t);
            z = z0 + v_z*t;
        }
        return statelist;
    }
}
