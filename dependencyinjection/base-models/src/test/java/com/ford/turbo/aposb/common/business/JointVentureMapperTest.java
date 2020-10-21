package com.ford.turbo.aposb.common.business;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class JointVentureMapperTest {

    private static final String CAF_LVS_VIN = "LVSK7D85FGB196831";
    private static final String CAF_LVR_VIN = "LVRHFADN5EN572483";
    private static final String JMC_VIN = "LJXK7D85FGB196831";
    private static final String FCO_VIN = "FM5K7D85FGB196831";
    private static final String OTHER_VIN = "12345678901234567";
    
    
    @Test
	public void should_returnCAF_for_CAF_LVS_VIN() throws Exception {
		Assert.assertEquals(JointVentureMapper.getJointVenture(CAF_LVS_VIN), JointVentureType.CAF);
	}
    
    @Test
    public void should_returnCAF_for_CAF_LVR_VIN() throws Exception {
    	Assert.assertEquals(JointVentureMapper.getJointVenture(CAF_LVR_VIN), JointVentureType.CAF);
    }
    
    @Test
    public void should_returnJMC_for_JMC_VIN() throws Exception {
    	Assert.assertEquals(JointVentureMapper.getJointVenture(JMC_VIN), JointVentureType.JMC);
    }
    
    @Test
    public void should_returnFCO_for_FCO_VIN() throws Exception {
    	Assert.assertEquals(JointVentureMapper.getJointVenture(FCO_VIN), JointVentureType.FCO);
    }
    
    @Test
    public void should_returnDefaultFCO_for_Other_VINs() throws Exception {
    	Assert.assertEquals(JointVentureMapper.getJointVenture(OTHER_VIN), JointVentureType.FCO);
    }
	
}
