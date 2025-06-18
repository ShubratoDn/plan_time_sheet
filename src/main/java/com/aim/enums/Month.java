package com.aim.enums;

public enum Month {
	
	JANUARY(0,"January"),
	FEBRUARY(1,"February"),
	MARCH(2,"March"), 
	APRIL(3,"April"), 
	MAY(4,"May"),
	JUNE(5,"June"), 
	JULY(6,"July"),
	AUGUST(7,"August"),
	SEPTEMBER(8,"September"),
	OCTOBER(9,"October"),
	NOVEMBER(10,"November"),
	DECEMBER(11,"December");
	
	public Integer urlParam;
	
	public String displayLabel;
	
	private Month(Integer urlParam, String displayLabel) {
        this.urlParam = urlParam;
        this.displayLabel = displayLabel;
    }
	
	/**
	 * Get month sort by url param
	 * @param urlParam
	 * @return
	 */
	public static Month getMonth(Integer urlParam) {
		
		if(urlParam == null)
			return null;
		
		for(Month month : Month.values()) {
			
			if(month.urlParam == urlParam)
				return month;
		}
		
		return null;
	}
}
