/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3793.robot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * Add your docs here.
 * 
 * @author Faris Prasetiawan
 */
public class BeltController {
    GenericHID controller;
    Spark motor;
    int timer = 0;
    final int TIMER_DELAY = 15;

    final int UP = 1;
    final int DOWN = -1;

    enum beltStates {GOING_UP, MIDPOINT, GOING_DOWN, STOPPED };
    beltStates beltState = beltStates.STOPPED;

    BeltController(GenericHID controller, Spark motor){
        this.controller = controller;
        this.motor = motor;
    }

    void update(){
        timer ++;
        if(timer> TIMER_DELAY){
            timer = TIMER_DELAY;
        }

        if(timer == TIMER_DELAY && controller.getRawButton(ControllerMap.X)){
            timer = 0;
           
            switch(beltState){
                case STOPPED:
                    beltState = beltStates.GOING_UP;
                    break;
                case GOING_UP:
                    beltState = beltStates.STOPPED;
                    break;
                case MIDPOINT:
                    beltState = beltStates.GOING_UP;
                    break;
                case GOING_DOWN:
                    beltState = beltStates.STOPPED;
                    break;
           }
        }

        if(timer == TIMER_DELAY && controller.getRawButton(ControllerMap.B)){
            timer = 0;
           
            switch(beltState){
                case STOPPED:
                    beltState = beltStates.GOING_DOWN;
                    break;
                case GOING_UP:
                    beltState = beltStates.STOPPED;
                    break;
                case MIDPOINT:
                    beltState = beltStates.GOING_DOWN;
                    break;
                case GOING_DOWN:
                    beltState = beltStates.STOPPED;
                    break;
            }
        }

        // if( beltState == beltStates.GOING_UP && Sensors.beltLimit.get()){
        //     beltState = beltStates.MIDPOINT;
        // }

        setMotors();
    }


    void setMotors(){
        if(beltState == beltStates.STOPPED || beltState == beltStates.MIDPOINT){
            motor.set(0);
        }

        if(beltState == beltStates.GOING_UP){
            motor.set(UP);
        }

        if(beltState == beltStates.GOING_DOWN){
            motor.set(DOWN);
        }
    }
}
