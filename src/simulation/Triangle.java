package simulation;

import Jama.Matrix;
import java.util.ArrayList;
import static simulation.Simulation.random;

public class Triangle extends Shape{
    double v;
    double length;

    long time;

    public Triangle(double length, double v, long time){
        this.length = length;
        this.time = time;
        this.v = v;
    }

    /**
     * 生成匀速直线运动的状态序列
     *
     * @return
     * 描述运动状态的序列，序列的每个元素是一个5*1的列向量，五个元素分别代表时间、X方向位置、Y方向位置、X方向速度、Y方向速度，单位为ms、cm和cm/s
     */
    public ArrayList<StateStamp> generate(){
        ArrayList<StateStamp> statelist = new ArrayList<>();
        double x = random.nextDouble(30, 80);
        double y = random.nextDouble(30, 80);
        double s = 0;
        for(long t = 0; t <= time; t += 25){
            if(s >= 0 && s<length){
                double[][] mat = {{x}, {y}, {v}, {0}};
                statelist.add(new StateStamp(t, new Matrix(mat), null));
                x += (v / 40.0);
                y += 0;
                s += (v / 40.0);
            }
            else if(s >= length && s < 2*length){
                double[][] mat = {{x}, {y}, {-v*Math.cos(Math.PI/3)}, {v*Math.sin(Math.PI/3)}};
                statelist.add(new StateStamp(t, new Matrix(mat), null));
                x -= (v*Math.cos(Math.PI/3) / 40.0);
                y += (v*Math.sin(Math.PI/3) / 40.0);
                s += (v / 40.0);
            }
            else if(s >= 2*length && s < 3*length){
                double[][] mat = {{x}, {y}, {-v*Math.cos(Math.PI/3)}, {-v*Math.sin(Math.PI/3)}};
                statelist.add(new StateStamp(t, new Matrix(mat), null));
                x -= (v*Math.cos(Math.PI/3)/ 40.0);
                y -= (v*Math.sin(Math.PI/3) / 40.0);
                s += (v / 40.0);
            }
            else{
                s = 0;
            }
        }
        return statelist;
    }
}
