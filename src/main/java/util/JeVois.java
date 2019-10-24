package util;

import org.usfirst.frc.team3793.robot.Sensors;

import edu.wpi.first.wpilibj.SerialPort;

public class JeVois extends Thread{
    private static double targetDeg;
    public JeVois() {}

    public void run(){
        while(Sensors.jeVois1 == null){
            try {
                Sensors.jeVois1 = new SerialPort(9600, SerialPort.Port.kUSB);
                if(Sensors.jeVois1 != null) Sensors.jeVois1.writeString("setpar serout USB");
                Thread.sleep(800);
            } catch(Exception e) {
                System.out.println(e);
            }
        }
        while(true) {
            String[] info = null;
            try {
                if(Sensors.jeVois1.getBytesReceived() > 0) {
                    info = Sensors.jeVois1.readString().split(",");
                    for (int i = 0; i < info.length; i++) {
                        String temp = info[i];
                        targetDeg = Float.parseFloat(temp);
                    }
                }
            } catch (Exception e) {
               // e.printStackTrace();
            }

            try {
                Thread.sleep(5);
            } catch(Exception e){
                //e.printStackTrace();
            }
        }
    }

    public static synchronized double getTargetDeg(){
        return targetDeg;
    }
}