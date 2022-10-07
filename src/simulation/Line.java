/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import Jama.Matrix;
import java.util.ArrayList;
import static simulation.Simulation.random;

/**
 *
 * @author Wang
 */
public class Line extends Shape {
        
        double v_x;
        double v_y;
        double v_z;
        long time;
        
        public Line(double v_x, double v_y, double v_z, long time) {
                this.v_x = v_x;
                this.v_y = v_y;
                this.v_z = v_z;
                this.time = time;
        }

        /**
         * 生成匀速直线运动的状态序列
         *
         * @return
         * 描述运动状态的序列，序列的每个元素是一个5*1的列向量，五个元素分别代表时间、X方向位置、Y方向位置、X方向速度、Y方向速度，单位为ms、cm和cm/s
         */
        @Override
        public ArrayList<StateStamp> generate() {
            ArrayList<StateStamp> statelist = new ArrayList<>();
            double x = 30;
            double y = 30;
            double z = 30;
            for (long t = 0; t <= time; t += 25) {
                double[][] mat = {{x}, {y}, {z}, {v_x}, {v_y}, {v_z}};
                statelist.add(new StateStamp(t, new Matrix(mat), null));
                x = x + v_x / 40.0;
                y = y + v_y / 40.0;
                z = z + v_z / 40.0;
            }
            return statelist;
        }
    }
