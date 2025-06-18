package com.aim.enums;

import org.springframework.util.StringUtils;

public enum TimeSheetPeriod {
	
	ONE_WEEK("oneWeek", "Weekly"),
	TWO_WEEK("twoWeek", "Biweekly"),
	ONE_MONTH("oneMonth", "Monthly");
		
	public String urlParam;
	
	public String displayLabel;
	
	private TimeSheetPeriod(String urlParam, String displayLabel) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
    }
	
	/**
	 * Get file sort by url param
	 * @param urlParam
	 * @return
	 */
	public static TimeSheetPeriod getAdminTimeSheetAction(String urlParam) {
		
		if(StringUtils.isEmpty(urlParam))
			return TimeSheetPeriod.ONE_WEEK;
		
		for(TimeSheetPeriod timeSheetPeriod : TimeSheetPeriod.values()) {
			
			if(timeSheetPeriod.urlParam.equals(urlParam))
				return timeSheetPeriod;
		}
		
		return TimeSheetPeriod.ONE_WEEK;
	}
}
