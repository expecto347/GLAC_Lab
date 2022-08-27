package glac;


import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;


/**
 * 三角定位法。该类不可实例化
 * @author Wang
 */
class Trilateration {

    /**
     * 使用三角定位法进行位置估计。当估计失败（三角形周长大于阈值）时，返回null。
     *
     * @param a1 天线1的编号
     * @param a2 天线2的编号
     * @param a3 天线3的编号
     * @param d1 目标与天线1的距离
     * @param d2 目标与天线2的距离
     * @param d3 目标与天线3的距离
     * @return 采用三角定位法估计出的坐标
     */
    public static Pair<Double, Double> estimate(int a1, int a2, int a3, double d1, double d2, double d3) {
        Pair<Double, Double>[] p1 = intersect(a1, d1, a2, d2);
        Pair<Double, Double>[] p2 = intersect(a1, d1, a3, d3);
        Pair<Double, Double>[] p3 = intersect(a2, d2, a3, d3);
        if (p1 == null || p2 == null || p3 == null) {
            return null;
        }
        double perimeter = Double.MAX_VALUE;
        Pair<Double, Double> k1 = null, k2 = null, k3 = null;
        for (Pair<Double, Double> x1 : p1) {
            for (Pair<Double, Double> x2 : p2) {
                for (Pair<Double, Double> x3 : p3) {
                    double t = getPerimeter(x1, x2, x3);
                    if (t < perimeter) {
                        k1 = x1;
                        k2 = x2;
                        k3 = x3;
                        perimeter = t;
                    }
                }
            }
        }
        if (perimeter > Config.getMaxPerimeter()) {
            return null;
        } else {
            return Pair.of((k1.getLeft() + k2.getLeft() + k3.getLeft()) / 3.0, (k1.getRight() + k2.getRight() + k3.getRight()) / 3.0);
        }
    }

    /**
     * 求两个圆的交点。
     *
     * @param no1 第一个天线的编号
     * @param s1 第一个天线的传播距离（往返）
     * @param no2 第二个天线编号
     * @param s2 第二个天线的传播距离（往返）
     * @return 交点坐标(0或2个)
     */
    private static Pair<Double, Double>[] intersect(int no1, double s1, int no2, double s2) {
        double a = Config.getX(no1) - Config.getX(no2);
        double b = Config.getY(no1) - Config.getY(no2);
        Pair<Double, Double> p[] = solve(a, b, s1 / 2, s2 / 2);
        if (p == null) {
            return null;
        }
        ArrayList<Pair<Double, Double>> list = new ArrayList<>(2);
        for (Pair<Double, Double> xPair : p) {
            if (xPair.getLeft() + Config.getX(no2) > 0 && xPair.getRight() + Config.getY(no2) > 0) {
                list.add(Pair.of(xPair.getLeft() + Config.getX(no2), xPair.getRight() + Config.getY(no2)));
            }
        }
        return list.toArray(new Pair[0]);
    }

    /**
     * 求解两个圆的交点坐标。<br>
     * 圆方程分别为：<br>
     * (x-a)^2+(y-b)^2=r1^2<br>
     * x^2+y^2=r2^2<br>
     *
     * @param a
     * @param b
     * @param r1
     * @param r2
     * @return 交点坐标
     */
    private static Pair<Double, Double>[] solve(double a, double b, double r1, double r2) {
        Pair<Double, Double>[] list = new Pair[2];
        double d = a * a + b * b;
        if ((r1 + r2) * (r1 + r2) < d || (r1 - r2) * (r1 - r2) > d) {
            return null;
        }
        double s = Math.sqrt(((r1 + r2) * (r1 + r2) - d) * (d - (r1 - r2) * (r1 - r2)));
        double t = r2 * r2 - r1 * r1;
        double x0 = a * d + a * t, y0 = b * d + b * t;
        list[0] = Pair.of((x0 + b * s) / (2 * d), (y0 - a * s) / (2 * d));
        list[1] = Pair.of((x0 - b * s) / (2 * d), (y0 + a * s) / (2 * d));
        return list;
    }

    /**
     * 求三角形周长
     *
     * @param x1 顶点坐标
     * @param x2 顶点坐标
     * @param x3 顶点坐标
     * @return 三角形周长
     */
    private static double getPerimeter(Pair<Double, Double> x1, Pair<Double, Double> x2, Pair<Double, Double> x3) {
        double sum = 0;
        sum += MyUtils.dist(x1.getLeft(), x1.getRight(), x2.getLeft(), x2.getRight());
        sum += MyUtils.dist(x1.getLeft(), x1.getRight(), x3.getLeft(), x3.getRight());
        sum += MyUtils.dist(x2.getLeft(), x2.getRight(), x3.getLeft(), x3.getRight());
        return sum;
    }

}
