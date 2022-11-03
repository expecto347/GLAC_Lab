/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package glac;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * It's a class of config saving all parameters.<br>
 * The instances of this class cannot be obtained.<br>
 * Parameters must be obtained through class methods.<br>
 * 配置类，保存所有的参数。<br>
 * 需要注意的是，该类的实例无法获取，必须通过类方法来获取参数。
 *
 * @author Wang
 */
public class Config {

    private static Config instance;

    static {
        instance = new Config();
    }
    /**
     * 天线数目<br>
     * The number of antennas
     */
    public final int k = 5;
    /**
     * 天线的X坐标，数组的每一个元素对应一根天线
     * The X coordinate of the antennas. Each element of the array corresponds
     * to an antenna.
     */
    public final double[] x = {0, 60, 0, 0, 60, 60, 60, 0};
    /**
     * 天线的Y坐标，数组的每一个元素对应一根天线
     * The Y coordinate of the antennas. Each element of the array corresponds
     * to an antenna.
     */
    public final double[] y = {0, 0, 60, 0, 60, 60, 0, 60};
    /**
     * 天线的Z坐标，数组的每一个元素对应一根天线
     * The Z coordinate of the antennas. Each element of the array corresponds
     * to an antenna.
     */
    public final double[] z = {0, 0, 0, 60, 60, 0, 60, 60};
    /**
     * RFID使用的电磁波的半波长<br>
     * The half wavelength of RFID
     */
    public final double semiLambda = 16.3;
    /**
     * 提前停止阈值，概率低于该值的轨迹的运算会被提前停止<br>
     * The threshold of quick stop. <br>
     * The calculation of trajectory whose probablity is lower than the
     * threshold will be stopped.
     */
    public final double stopThreshold = 1e-30;
    /**
     * 三角定位时的周长阈值，周长大于该值的三角形会被剪枝<br>
     * The threshold of triangulation. Triangles with perimeter greater than the
     * threshold will be pruned.
     */
    public final double maxPerimeter = 15.0;
    /**
     * 最大扩展的半波长数目<br>
     * Maximum number of extended semiLambda
     */
    public final int maxS = 8;
    /**
     * 位置估计的标准差<br>
     * The standard deviation of position estimation
     */
    public final double sigmaP = 0.5;
    /**
     * 速度估计的标准差<br>
     * The standard deviation of velocity estimation
     */
    public final double sigmaV = 3.5;

    private Config() {
    }

    /**
     * 获取天线数目<br>
     * Get the number of antennas
     *
     * @return 天线数目(The number of antennas)
     */
    public static int getK() {
        return instance.k;
    }

    /**
     * 获取指定天线的X坐标<br>
     * Get the X coordinate of the specified antenna
     *
     * @param index 天线编号(The index of the antenna)
     * @return X坐标(X coordinate of the specified antenna)
     */
    public static double getX(int index) {
        return instance.x[index];
    }

    /**
     * 获取指定天线的Y坐标<br>
     * Get the Y coordinate of the specified antenna
     *
     * @param index 天线编号(The index of the antenna)
     * @return Y坐标(Y coordinate of the specified antenna)
     */
    public static double getY(int index) {
        return instance.y[index];
    }

    /**
     * 获取指定天线的Z坐标<br>
     * Get the Z coordinate of the specified antenna
     *
     * @param index 天线编号(The index of the antenna)
     * @return Z坐标(Z coordinate of the specified antenna)
     */

    public static double getZ(int index) {
        return instance.z[index];
    }

    /**
     * 获取半波长<br>
     * Get the half wavelength of RFID
     *
     * @return 半波长(Half wavelength)
     */
    public static double getSemiLambda() {
        return instance.semiLambda;
    }

    /**
     * 获取提前停止阈值<br>
     * Get the threshold of quick stop
     *
     * @return 提前停止阈值(Threshold of quick stop)
     */
    public static double getStopThreshold() {
        return instance.stopThreshold;
    }

    /**
     * 获取三角定位时的周长阈值<br>
     * Get the threshold of triangulation
     *
     * @return 三角定位时的周长阈值(The threshold of triangulation)
     */
    public static double getMaxPerimeter() {
        return instance.maxPerimeter;
    }

    /**
     * 获取最大扩展的半波长数目<br>
     * Get the maximum number of extended semiLambda
     *
     * @return 最大扩展的半波长数目(Maximum number of extended semiLambda)
     */
    public static int getMaxS() {
        return instance.maxS;
    }

    /**
     * 获取位置误差的标准差<br>
     * Get the standard deviation of position estimation
     *
     * @return 位置误差的标准差(The standard deviation of position estimation)
     */
    public static double getSigmaP() {
        return instance.sigmaP;
    }

    /**
     * 获取速度误差的标准差<br>
     * The standard deviation of velocity estimation
     *
     * @return 速度误差的标准差(The standard deviation of velocity estimation)
     */
    public static double getSigmaV() {
        return instance.sigmaV;
    }

    /**
     * 修改参数配置<br>
     * Change the config setting
     *
     * @param s 以json格式表示的新的参数配置(The new config setting in json format)
     * @return 修改成功返回true，否则返回false(If change successfully, return true;
     * otherwise, return false)
     */
    public static boolean change(String s) {
        try {
            Gson gson = new Gson();
            Config c = gson.fromJson(s, Config.class);
            if (c == null) {
                return false;
            } else {
                instance = c;
            }
            save();
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
        return true;
    }

    /**
     * 将配置保存到Config.json中
     */
    private static void save() {
        try (PrintWriter writer = new PrintWriter(new File("Config.json"))) {
            writer.println(instance.toString());
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 输出参数配置<br>
     * Output the config setting
     *
     * @return json格式的参数配置(The config setting in json format)
     */
    public static String print() {
        String s = instance.toString();
        return s.replaceAll(",\"", ",\n\"").replaceAll("\\{", "\\{\n").replaceAll("\\}", "\n\\}");
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
