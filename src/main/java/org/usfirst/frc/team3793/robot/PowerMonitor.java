package org.usfirst.frc.team3793.robot;
public class PowerMonitor {
    static double maxDriveCurrent;
    static double lowestVoltage;
    static double maxCompressorCurr;
    static double maxArmPivotCurr;
    static double maxArmCurr;
    static double maxAvocadoCurr;
    static double maxCurr;
    static double maxBeltCurr;
    static double resistance;
    static double theoreticalMax;

    static void init() {
        lowestVoltage = 50;
        maxDriveCurrent = 0;
        maxArmCurr = 0;
        maxArmPivotCurr = 0;
        maxCurr = 0;
        maxAvocadoCurr = 0;
        maxCompressorCurr = 10;
        maxBeltCurr = 0;
        // belt is 12 talon srx right is 2 victor srx right 3 arm is 1 talonSRX left is 14 VictorSrx left 13 Arm spin 0 Avocado is 15 for sure
        resistance = Robot.pdp.getVoltage()/Robot.pdp.getTotalCurrent();
    }//Use current spike to increase power to arm pivot motor, NOT IMPLEMENTED

    static void evaluate() {
        double driveCurrent = Robot.pdp.getCurrent(2) + Robot.pdp.getCurrent(3) + Robot.pdp.getCurrent(14) + Robot.pdp.getCurrent(13);
        if(driveCurrent > maxDriveCurrent) maxDriveCurrent = driveCurrent;
        //if(Robot.pdp.getCurrent(4) > maxCompressorCurr) maxCompressorCurr = Robot.pdp.getCurrent(4); //10 Amps
        if(Robot.pdp.getCurrent(1) > maxArmPivotCurr) maxArmPivotCurr = Robot.pdp.getCurrent(1);
        if(Robot.pdp.getCurrent(0) > maxArmCurr) maxArmCurr = Robot.pdp.getCurrent(0);
        if(Robot.pdp.getCurrent(15) > maxAvocadoCurr) maxAvocadoCurr = Robot.pdp.getCurrent(15);
        //if(Robot.pdp.getCurrent(8) > maxCompressorCurr) maxCompressorCurr = Robot.pdp.getCurrent(8);
        if(Robot.pdp.getCurrent(12) > maxBeltCurr) maxBeltCurr = Robot.pdp.getCurrent(12);
        if(Robot.pdp.getTotalCurrent() > maxCurr) maxCurr = Robot.pdp.getTotalCurrent();
        if(Robot.pdp.getVoltage() < lowestVoltage) lowestVoltage = Robot.pdp.getVoltage();
        // if(System.currentTimeMillis() % 1500 == 0) {
        //     System.out.println("Max Currents");
        //     System.out.println("Drive: " + maxDriveCurrent);
        //     System.out.println("Compressor: " + maxCompressorCurr);
        //     System.out.println("Arm: " + maxArmCurr);
        //     System.out.println("Arm Pivot: " + maxArmPivotCurr);
        //     System.out.println("Avocado: " + maxAvocadoCurr);
        //     System.out.println("Total: " + maxCurr);
        //     System.out.println("Resistance " + resistance);
        // }
        theoreticalMax = Settings.TARGET_MIN_VOLT/resistance;

        double driveBudget;
        driveBudget = theoreticalMax - (Robot.pdp.getTotalCurrent() - driveCurrent);
        Motors.talonLeft.configContinuousCurrentLimit((int)(driveBudget/4f));
        Motors.talonRight.configContinuousCurrentLimit((int)(driveBudget/4f));
    }
}