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

    private boolean b = false;

    int timer = 0;

    final int TIMER_DELAY = 15;
    toggleSwitch(GenericHID controller, int buttonNum){
        this.buttonNum = buttonNum;
        this.controller = controller;
    }

    toggleSwitch(GenericHID controller, int buttonNum, Object obj, Method method) {
        this.buttonNum = buttonNum;
        this.controller = controller;
        this.obj = obj;
        this.method = method;
    }

    void buttonUpdate() {
        button();
        reflect();
    }

    public void button(){
        if(timer < TIMER_DELAY){
        timer++;
        }

        if(timer == TIMER_DELAY && controller.getRawButton(buttonNum)){
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
