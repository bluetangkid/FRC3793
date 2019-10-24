/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3793.robot;

import java.lang.reflect.*;

import edu.wpi.first.wpilibj.GenericHID;

/*
 * Gives Toggle Switch Functionality to controller buttons
 * ONLY FOR SOLENOIDS
 *  
 * @author Faris Prasetiawan
 */
public class toggleSwitch {

    private GenericHID controller;
    private int buttonNum;
    private Object obj;
    private Method method;

    int delay;
    private boolean b = false;

    int timer = 0;

    
    toggleSwitch(GenericHID controller, int buttonNum){
        this.buttonNum = buttonNum;
        this.controller = controller;
        delay = Settings.TIMER_DELAY;
    }

    toggleSwitch(GenericHID controller, int buttonNum, Object obj, Method method) {
        this.buttonNum = buttonNum;
        this.controller = controller;
        this.obj = obj;
        this.method = method;
        delay = Settings.TIMER_DELAY;
    }
    toggleSwitch(GenericHID controller, int buttonNum, Object obj, Method method, int delay) {
        this.buttonNum = buttonNum;
        this.controller = controller;
        this.obj = obj;
        this.method = method;
        this.delay = delay;
    }

    void buttonUpdate() {
        button();
        reflect();
    }

    public void button(){
        if(timer < delay){ // TODO: Use timer class
            timer++;
        }

        if(timer == delay && controller.getRawButton(buttonNum)){
            timer = 0;
            b = !b;
        }
    }

    public void reflect(){
        try {
            method.invoke(obj, b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setB(boolean x){
        b = x;
    }

    
    public boolean getB(){
        return b;
    }

    public boolean buttonPressed(){
        return controller.getRawButton(buttonNum);
    }
}
