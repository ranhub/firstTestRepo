package com.ford.turbo.servicebooking.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DealerCodeFormatterTest {

    @Test
    public void should_formatDealerCode(){
        assertThat(DealerCodeFormatter.formatDealerCode("XC23sfge312")).isEqualTo("23sfge312");
        assertThat(DealerCodeFormatter.formatDealerCode("1C23sfge312")).isEqualTo("1C23sfge312");
        assertThat(DealerCodeFormatter.formatDealerCode("1223sfge312")).isEqualTo("1223sfge312");
        assertThat(DealerCodeFormatter.formatDealerCode("ab23sfge312")).isEqualTo("23sfge312");
        assertThat(DealerCodeFormatter.formatDealerCode("2")).isEqualTo("2");
    }
    
    @Test 
    public void should_formatDealerCodes()
    {
    	String dealerCode1 = "1C23sfge312";
    	String dealerCode2 = "1223sfge312";
    	String dealerCode3 = "ab23sfge312";
    	String dealerCode4 = "2";
    	
    	List<String> dealerCodes = new ArrayList<>();
    	dealerCodes.add(dealerCode1);
		dealerCodes.add(dealerCode2);
		dealerCodes.add(dealerCode3);
		dealerCodes.add(dealerCode4);
    	
    	List<String> actualDealersCodes = DealerCodeFormatter.formatDealerCodes(dealerCodes);
    	assertThat(actualDealersCodes.size() == 4);
    	assertThat(actualDealersCodes.contains(dealerCode1));
    	assertThat(actualDealersCodes.contains(dealerCode2));
    	assertThat(actualDealersCodes.contains(dealerCode3));
    	assertThat(actualDealersCodes.contains(dealerCode4));
    }
}