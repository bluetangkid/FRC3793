/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3793.robot;

import edu.wpi.first.wpilibj.Controller;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * Add your docs here.
 */
public class landingGearController {
    toggleSwitch extend;
    toggleSwitch retract;
    toggleSwitch stop;

    GenericHID controller;

    int timer = 0;
    int extendButton;
    int retractButton;
    

    boolean stopEnabled;

    landingGearController(GenericHID controller, int extendButton, int retractButton, toggleSwitch extend, toggleSwitch retract, toggleSwitch stop){
        this.extend = extend;
        this.retract = extend;
        this.stop = stop;
        this.extendButton = extendButton;
        this.retractButton = retractButton;
        this.controller = controller;
    }

    void update(){
        boolean extendPressed = controller.getRawButton(extendButton);
        boolean retractPressed = controller.getRawButton(retractButton);
    //     if(timer<Settings.TIMER_DELAY){
    //         timer ++;
    //     }

    //     if(timer == Settings.TIMER_DELAY){
            
    //         if(extendPressed){
    //             timer =0;

    //             if(!extend.getB()){
    //                 extend.setB(true);
    //                 retract.setB(false);
    //                 System.out.println("xxxxx");
    //                 //stop.setB(false);
    //             }else if(extend.getB() && !stop.getB()){
    //                 //extend.setB(true);
    //                 //retract.setB(false);
    //                 //stop.setB(true);
    //             }
                
    //         }

    //         if(retractPressed){
    //             timer =0;
    //             extend.setB(false);
    //             //stop.setB(false);
    //             retract.setB(true);
    //             System.out.println("yuyyyyyyyy");
    //         }
    //     }
        
    //     extend.reflect();
    //     retract.reflect();
    //     //stop.reflect();
     }
}
