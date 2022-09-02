package myMain;

import java.util.ArrayList;
import java.util.Scanner;
import simulation.Simulation;
import simulation.Circle;
import simulation.Line;
import simulation.Triangle;
import utils.MyChart;

public class myMain {
    public static void main(String[] args) {
        System.out.println("Please enter the number before the shape you want to simulate:");
        System.out.println("1. Line");
        System.out.println("2. Circle");
        System.out.println("3. Triangle");

        int shape = new Scanner(System.in).nextInt();
        //Wait the input
        ArrayList<Double>[][] results = new ArrayList[0][];
        switch (shape) {
            case 1:
                System.out.println("You have chosen the line.");
                System.out.println("Please enter the angle(degree):");
                double angle = new Scanner(System.in).nextDouble();

                System.out.println("Please enter the velocity(cm/s):");
                double velocity = new Scanner(System.in).nextDouble();

                System.out.println("Please enter the time(ms):");
                long time = new Scanner(System.in).nextLong();
                Line line = new Line(angle, velocity, time);

                results = Simulation.track(line);
                break;
            case 2:
                System.out.println("You have chosen the circle.");
                System.out.println("Please enter the radius(cm):");
                double radius = new Scanner(System.in).nextDouble();

                System.out.println("Please enter the velocity(cm/s):");
                double velocity1 = new Scanner(System.in).nextDouble();

                System.out.println("Please enter the time(ms):");
                long time1 = new Scanner(System.in).nextLong();

                Circle circle = new Circle(radius, velocity1, time1);

                results = Simulation.track(circle);
                break;
            case 3:
                System.out.println("You have chosen the Triangle.");
                System.out.println("Please enter the length(cm):");
                double length = new Scanner(System.in).nextDouble();

                System.out.println("Please enter the velocity(cm/s):");
                double velocity2 = new Scanner(System.in).nextDouble();

                System.out.println("Please enter the time(ms):");
                long time2 = new Scanner(System.in).nextLong();

                Triangle triangle = new Triangle(length, velocity2, time2);

                results = Simulation.track(triangle);
                break;
            default:
                System.out.println("You have chosen an invalid shape.");
                return;
        }
        myGUI.myGUI(results);
    }
}