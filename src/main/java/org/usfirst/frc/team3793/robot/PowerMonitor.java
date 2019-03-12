package org.usfirst.frc.team3793.robot;
import java.util.ArrayList;
public class PowerMonitor {
    static double maxDriveCurrent;
    static double lowestVoltage;
    static double maxCompressorCurr;
    static double maxArmPivotCurr;
    static double maxArmCurr;
    static double maxAvocadoCurr;
    static double maxCurr;
    static double resistance;
    static ArrayList<Double> resistances;

    static void init() {
        lowestVoltage = 50;
        maxDriveCurrent = 0;
        maxArmCurr = 0;
        maxArmPivotCurr = 0;
        maxCurr = 0;
        maxAvocadoCurr = 0;
        maxCompressorCurr = 0;
        resistance = 0;
        resistances = new ArrayList<Double>();
    }

    static void evaluate() {
        double driveCurrent = Robot.pdp.getCurrent(0) + Robot.pdp.getCurrent(1) + Robot.pdp.getCurrent(2) + Robot.pdp.getCurrent(3);
        if(driveCurrent > maxDriveCurrent) maxDriveCurrent = driveCurrent;
        if(Robot.pdp.getCurrent(4) > maxCompressorCurr) maxCompressorCurr = Robot.pdp.getCurrent(4);
        if(Robot.pdp.getCurrent(5) > maxArmPivotCurr) maxArmPivotCurr = Robot.pdp.getCurrent(5);
        if(Robot.pdp.getCurrent(6) > maxArmCurr) maxArmCurr = Robot.pdp.getCurrent(6);
        if(Robot.pdp.getCurrent(7) > maxAvocadoCurr) maxAvocadoCurr = Robot.pdp.getCurrent(7);
        if(Robot.pdp.getTotalCurrent() > maxCurr) maxCurr = Robot.pdp.getTotalCurrent();
        if(Robot.pdp.getCurrent(8) > maxCompressorCurr) maxCompressorCurr = Robot.pdp.getCurrent(8);
        if(Robot.pdp.getVoltage() < lowestVoltage) lowestVoltage = Robot.pdp.getVoltage();
        resistances.add(Robot.pdp.getVoltage()/Robot.pdp.getTotalCurrent());
        if(resistances.size() > 5) resistances.remove(0);
        resistance = 0;
        for(int i = 0; i < resistances.size(); i++){
            resistance += resistances.get(i);
        }
        resistance /= 5;
        if(System.currentTimeMillis() % 1500 == 0) {
            System.out.println("Max Currents");
            System.out.println("Drive: " + maxDriveCurrent);
            System.out.println("Compressor: " + maxCompressorCurr);
            System.out.println("Arm: " + maxArmCurr);
            System.out.println("Arm Pivot: " + maxArmPivotCurr);
            System.out.println("Avocado: " + maxAvocadoCurr);
            System.out.println("Total: " + maxCurr);
            System.out.println("Resistance" + resistance);
        }
    }
}