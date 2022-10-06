package glac;


import Jama.Matrix;

import java.util.ArrayList;


/**
 * 三角定位法。该类不可实例化
 * @author Wang
 */
class Trilateration {
    /**
     * 三角定位法,传入三个基站的坐标和距离，返回定位点的坐标
     * @param n1
     * @param n2
     * @param n3
     * @param n4
     * @param r1
     * @param r2
     * @param r3
     * @param r4
     * @return 坐标，如果没有结果则返回空
     */
    static Coordinate Trilateration(int n1, int n2, int n3, int n4, double r1, double r2, double r3, double r4){
        Coordinate[] c1 = Insection_3D(n1, r1, n2, r2, n3, r3);
        Coordinate[] c2 = Insection_3D(n1, r1, n2, r2, n4, r4);
        Coordinate[] c3 = Insection_3D(n1, r1, n3, r3, n4, r4);
        Coordinate[] c4 = Insection_3D(n2, r2, n3, r3, n4, r4);

        if(c1 == null || c2 == null || c3 == null || c4 == null){
            return null;
        }

        double perimeter = Double.MAX_VALUE;
        Coordinate k1 = null, k2 = null, k3 = null, k4 = null;
        for(Coordinate x1 : c1){
            for(Coordinate x2 : c2){
                for(Coordinate x3 : c3){
                    for(Coordinate x4 : c4){
                        double p = getPerimeter(x1, x2, x3, x4);
                        if(p < perimeter){
                            perimeter = p;
                            k1 = x1;
                            k2 = x2;
                            k3 = x3;
                            k4 = x4;
                        }
                    }
                }
            }
        }
        if (perimeter > Config.getMaxPerimeter()){
            return null;
        }
        else{
            return getCenter(k1, k2, k3, k4);
        }
    }

    /**
     *  参数参见这张图https://upload.wikimedia.org/wikipedia/commons/b/bb/3D_Trilat_Scenario_2019-0116.jpg
     *
     */
    static Coordinate[] Intersection(double U, double V_x, double V_y, double r1, double r2, double r3){
        double x = (r1 * r1 - r2 * r2 + U * U) / 2 * U; //求出交点的x值
        double y = (r1 * r1 - r3 * r3 + V_x * V_x + V_y * V_y - 2 * V_x * x) / (2 * V_y); //求出交点的y值
        if(r1 * r1 > x * x + y * y + 0.00000001){ //浮点数运算，精度设定为0.00000001
            double z = Math.sqrt(r1 * r1 - x * x - y * y); //求出交点的z值
            Coordinate c1 = new Coordinate(x, y, z);
            Coordinate c2 = new Coordinate(x, y, -z);
            Coordinate[] c = {c1, c2};
            return c;
        }
        else if(r1 * r1 < x * x + y * y - 0.00000001){
            return null; //没有交点
        }
        else{
            Coordinate c = new Coordinate(x, y, 0);
            Coordinate[] c1 = {c};
            return c1;
        }
    }

    /**
     * 旋转坐标轴使得三个点形成的平面在新的坐标系下的XOY平面上
     * @param n1 第一个点的编号(第一个点的编号应该在原点处，即已经经过平移了)
     * @param n2 第二个点的编号
     * @param n3 第三个点的编号
     * @return 旋转矩阵
     */
    static Matrix rotateCoordinate(Coordinate n1, Coordinate n2, Coordinate n3){
        Matrix n1_ = new Matrix(new double[][]{{n1.getX()}, {n1.getY()}, {n1.getZ()}});
        Matrix n2_ = new Matrix(new double[][]{{n2.getX()}, {n2.getY()}, {n2.getZ()}});
        Matrix n3_ = new Matrix(new double[][]{{n3.getX()}, {n3.getY()}, {n3.getZ()}});

        double x_z = n2_.get(0, 0);
        double y_z = n2_.get(0, 1);
        double r1 = Math.sqrt(x_z * x_z + y_z * y_z);
        Matrix m_z = new Matrix(new double[][]{{x_z / r1, y_z / r1, 0}, {-y_z / r1, x_z / r1, 0}, {0, 0, 1}}); //求出绕z轴旋转的旋转矩阵

        n1_ = m_z.times(n1_);
        n2_ = m_z.times(n2_);
        n3_ = m_z.times(n3_); //将第二个点的x坐标变为0

        double x_y = n2_.get(0, 0);
        double z_y = n2_.get(0, 2);
        double r2 = Math.sqrt(x_y * x_y + z_y * z_y);
        Matrix m_y = new Matrix(new double[][]{{x_y / r2, 0, z_y / r2}, {0, 1, 0}, {-z_y / r2, 0, x_y / r2}}); //求出绕y轴的旋转矩阵m_y

        n1_ = m_y.times(n1_);
        n2_ = m_y.times(n2_);
        n3_ = m_y.times(n3_); //将第二个点的z坐标变为0

        double y_x = n3_.get(0, 1);
        double z_x = n3_.get(0, 2);
        double r3 = Math.sqrt(y_x * y_x + z_x * z_x);
        Matrix m_x = new Matrix(new double[][]{{1, 0, 0}, {0, y_x / r3, z_x / r3}, {0, -z_x / r3, y_x / r3}}); //求出绕x轴的旋转矩阵m_x

        n1_ = m_x.times(n1_);
        n2_ = m_x.times(n2_);
        n3_ = m_x.times(n3_); //将第三个点的y坐标变为0

        Matrix m = m_x.times(m_y).times(m_z); //求出总的旋转矩阵

        return m;
    }

    /**
     * 以n1坐标为原点进行平移，返回平移后的坐标
     * @param n1
     * @param n2
     * @param n3
     * @return
     */
    static Coordinate[] moveCoordinate(Coordinate n1, Coordinate n2, Coordinate n3){
        Coordinate[] result = new Coordinate[3];
        result[0] = new Coordinate(0, 0, 0);
        result[1] = new Coordinate(n2.getX() - n1.getX(), n2.getY() - n1.getY(), n2.getZ() - n1.getZ());
        result[2] = new Coordinate(n3.getX() - n1.getX(), n3.getY() - n1.getY(), n3.getZ() - n1.getZ());
        return result;
    }

    static  Coordinate[] Insection_3D(int a0, double r0, int a1, double r1, int a2, double r2){
        Coordinate c0 = new Coordinate(Config.getX(a0), Config.getY(a0), Config.getZ(a0));
        Coordinate c1 = new Coordinate(Config.getX(a1), Config.getY(a1), Config.getZ(a1));
        Coordinate c2 = new Coordinate(Config.getX(a2), Config.getY(a2), Config.getZ(a2));

        Coordinate[] c = moveCoordinate(c0, c1, c2);

        Matrix m = rotateCoordinate(c[0], c[1], c[2]);

        Matrix c1_ = m.times(new Matrix(new double[][]{{c[1].getX()}, {c[1].getY()}, {c[1].getZ()}}));
        Matrix c2_ = m.times(new Matrix(new double[][]{{c[2].getX()}, {c[2].getY()}, {c[2].getZ()}}));

        Coordinate[] c_ = Intersection(c1_.get(0, 0), c2_.get(0, 0), c2_.get(0, 1), r0, r1, r2); //求出旋转后的坐标系的值

        if(c_ == null) return null; //如果没有交点则返回null
        else{
            for(Coordinate t : c_){
                Matrix t_ = m.inverse().times(new Matrix(new double[][]{{t.getX()}, {t.getY()}, {t.getZ()}}));
                t.setX(t_.get(0, 0) + c0.getX());
                t.setY(t_.get(0, 1) + c0.getY());
                t.setZ(t_.get(0, 2) + c0.getZ()); //将坐标换成原来坐标系
            }
        }
        return c_;
    }

    /**
     * 求四边形边长
     * @param x1
     * @param x2
     * @param x3
     * @param x4
     * @return
     */
    static double getPerimeter(Coordinate x1, Coordinate x2, Coordinate x3, Coordinate x4){
        double sum = 0;
        sum += MyUtils.dist(x1.getX(), x1.getY(), x1.getZ(), x2.getX(), x2.getY(), x2.getZ());
        sum += MyUtils.dist(x2.getX(), x2.getY(), x2.getZ(), x3.getX(), x3.getY(), x3.getZ());
        sum += MyUtils.dist(x3.getX(), x3.getY(), x3.getZ(), x4.getX(), x4.getY(), x4.getZ());
        sum += MyUtils.dist(x4.getX(), x4.getY(), x4.getZ(), x1.getX(), x1.getY(), x1.getZ());
        return sum;
    }

    static Coordinate getCenter(Coordinate x1, Coordinate x2, Coordinate x3, Coordinate x4){
        double x = (x1.getX() + x2.getX() + x3.getX() + x4.getX()) / 4;
        double y = (x1.getY() + x2.getY() + x3.getY() + x4.getY()) / 4;
        double z = (x1.getZ() + x2.getZ() + x3.getZ() + x4.getZ()) / 4;
        return new Coordinate(x, y, z);
    }
}

