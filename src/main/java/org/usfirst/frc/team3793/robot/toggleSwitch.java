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

    GenericHID controller;
    int buttonNum;
    Object obj;
    Method method;

    boolean b = false;

    int timer = 0;

    final int TIMER_DELAY = 15;

    toggleSwitch(GenericHID controller, int buttonNum, Object obj, Method method) {
        this.buttonNum = buttonNum;
        this.controller = controller;
        this.obj = obj;
        this.method = method;
    }

    void update() {
        
        if (timer < TIMER_DELAY) {
            timer++;
        }


        if(timer == TIMER_DELAY && controller.getRawButton(buttonNum)){
            timer = 0;
            if (b) {
                b = false;
            } else {
                b = true;
            } 

        }

        

        

        try {
            method.invoke(obj, b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void reflect(){
        if (b) {
            b = false;
        } else {
            b = true;
        }
        try {
            method.invoke(obj, b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
