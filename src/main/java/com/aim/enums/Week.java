package com.aim.enums;

public enum Week {

	Sunday("sunday","Su"),
	Monday("monday","Mo"),
	Tuesday("tuesday","Tu"), 
	Wednesday("wednesday","We"), 
	Thursday("thursday","Th"), 
	Friday("friday","Fr"),
	Saturday("saturday","Sa");
	
	public String urlParam;
	
	public String displayLabel;
	
	Week(String urlParam,String displayLabel){
		this.displayLabel = displayLabel;
		this.urlParam = urlParam;
	}
}
