/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package glac;

/**
 * A tag reading / An observation.<br>
 * This class is immutable.<br>
 * 一次标签读取的结果，或称作一次观测。该类是不可变的。
 *
 * @author Wang
 */
public class TagData {

    private final int antennaIndex;//天线编号
    private final long time;//时间
    private final double phase;//相位（弧度）

    private final double[] s;//状态戳

    /**
     * 构造器(Constructor)
     *
     * @param antennaIndex 天线编号(The index of the antenna)
     * @param time 毫秒表示的时间(The time in millisecond)
     * @param phase 弧度表示的相位(The phase in radian)
     */
    public TagData(int antennaIndex, long time, double phase, double[] s) {
        this.antennaIndex = antennaIndex;
        this.time = time;
        this.phase = phase;
        this.s = s;
    }

    /**
     * 获取天线编号<br>
     * Get the index of antenna
     *
     * @return The index of antenna
     */
    public int getAntennaIndex() {
        return antennaIndex;
    }

    /**
     * 获取毫秒表示的时间<br>
     * Get the time in millisecond
     *
     * @return The time in millisecond
     */
    public long getTime() {
        return time;
    }

    /**
     * 获取弧度制相位<br>
     * Get the phase in radian
     *
     * @return The phase in radian
     */
    public double getPhase() {
        return phase;
    }

    public double[] getStateStamp(){
        return s;
    }
}
