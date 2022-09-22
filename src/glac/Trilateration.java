package glac;


import java.util.ArrayList;


/**
 * 三角定位法。该类不可实例化
 * @author Wang
 */
class Trilateration {
    /**
     * 为了方便计算，我们要求天线1位于原点，2，3，4分别位于x，y，z轴上
     */
    static Coordinate Trilateration(int n1, int n2, int n3, int n4, double r1, double r2, double r3, double r4){
        double x0 = Config.getX(n2);
        double y0 = Config.getY(n3);
        double z0 = Config.getZ(n4); //initialize the coordinates of the four antennas

        double x1, x2, x3, y1, y2, y3, z1, z2, z3;

        x1 = (r1*r1 - r2*r2 + x0*x0)/(2*x0);
        y1 = (r1*r1 - r3*r3 + y0*y0)/(2*y0);
        if(r1*r1 >= (x1*x1 + y1*y1)){
            z1 = Math.sqrt(r1*r1 - x1*x1 - y1*y1);
        }
        else return null;

        y2 = (r1*r1 - r3*r3 + y0*y0)/(2*y0);
        z2 = (r1*r1 - r4*r4 + z0*z0)/(2*z0);
        if(r1*r1 >= (y2*y2 + z2*z2)){
            x2 = Math.sqrt(r1*r1 - y2*y2 - z2*z2);
        }
        else return null;

        x3 = (r1*r1 - r2*r2 + x0*x0)/(2*x0);
        z3 = (r1*r1 - r4*r4 + z0*z0)/(2*z0);
        if(r1*r1 >= (x3*x3 + z3*z3)){
            y3 = Math.sqrt(r1*r1 - x3*x3 - z3*z3);
        }
        else return null;
        Coordinate c = new Coordinate((x1 + x2 + x3)/3, (y1 + y2 + y3)/3, (z1 + z2 +z3)/3); //求得他们的重心，返回估计的坐标
        return c;
    }
}

