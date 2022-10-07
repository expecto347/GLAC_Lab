package myMain;

import glac.Config;
import glac.Coordinate;
import utils.MyUtils;

public class myMain_Tri {
    public static void main(String[] args) {
        double r0, r1, r2 ,r3 ,r4;
        for(double i = 0; i <= 60; i++){
            for(double j = 0; j <= 60; j++){
                for(double k = 0; k <= 60; k++){
                    r0 = MyUtils.dist(i, j, k, Config.getX(0), Config.getY(0), Config.getZ(0));
                    r1 = MyUtils.dist(i, j, k, Config.getX(1), Config.getY(1), Config.getZ(1));
                    r2 = MyUtils.dist(i, j, k, Config.getX(2), Config.getY(2), Config.getZ(2));
                    r3 = MyUtils.dist(i, j, k, Config.getX(3), Config.getY(3), Config.getZ(3));
                    r4 = MyUtils.dist(i, j, k, Config.getX(4), Config.getY(4), Config.getZ(4));
                    Coordinate c = glac.Trilateration.Trilateration(1, 2, 3, 4, r1, r2, r3, r4);
                    System.out.print(i);
                    System.out.print(" ");
                    System.out.print(j);
                    System.out.print(" ");
                    System.out.print(k);
                    System.out.print("\n");
                    System.out.print(c.getX());
                    System.out.print(" ");
                    System.out.print(c.getY());
                    System.out.print(" ");
                    System.out.print(c.getZ());
                    System.out.print("\n");
                    System.out.print("\n");
                }
            }
        }
    }
}