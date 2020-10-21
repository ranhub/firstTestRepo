package com.ford.turbo.aposb.common.business;

public class JointVentureMapper {
	
    public static JointVentureType getJointVenture(String vin){
        switch(vin.substring(0, 3).toUpperCase()){
            case "LVS":
            case "LVR":
                return JointVentureType.CAF;
            case "LJX":
                return JointVentureType.JMC;
            default:
                return JointVentureType.FCO;
        }
    }
	
}
