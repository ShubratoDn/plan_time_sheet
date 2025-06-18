package com.aim.utils;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.Normalizer.Form;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class Utils {
	
	public static final String USA_DATE_FORMATE = "MM/dd/yyyy";
	
	/**
	 * Get month from date
	 * @param date
	 * @return
	 * Comment :http://www.java67.com/2016/12/how-to-get-current-day-month-year-from-date-in-java8.html
	 * Month is always start with 0 so we need to plus one
	 */
	public static int getMonthFromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		return month +1;
	}
	
	public static int getYearFromDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		return year;
	}
	
	public static int getDatedifference(Date dateTime) {
		long timeDifferenceMilliseconds = new Date().getTime() - dateTime.getTime();
		int diffInDay = (int) (timeDifferenceMilliseconds / (1000 * 60 * 60 * 24)) ;
	    return diffInDay;
	}

	/**
	 * Format Date to String mm/dd/yyyy
	 * 	
	 * @param date
	 * @return
	 */
	public static String formateToUSADate(Date date) {
		DateFormat df = new SimpleDateFormat(USA_DATE_FORMATE);
		if (date != null)
			return df.format(date);
		return null;
	}
	
	/**
	 * Change the date format mm/dd/yyyy to yyyy-mm-dd
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date formateDateMonthToYear(String date){
		if(date != null && date.length() >0) {
			 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			 Date convertDate;
			try {
				convertDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
				String formateDate = formatter.format(convertDate);
				 return formatter.parse(formateDate);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			} 
		}
		return null;
	}
	
	public static Calendar getDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal;
	}
	
	public static int getBiweekDay(Date date) {
        
		int totalDay = 0;
		int referenceYear = 2017;
		
		for(int i = referenceYear; i<= date.getYear() + 1900; i++) {
			if(i == date.getYear() + 1900) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				totalDay = totalDay + cal.get(Calendar.DAY_OF_YEAR);
			}else {
				Calendar cal  = Utils.getDate(referenceYear, 11,31);
				totalDay = totalDay + cal.get(Calendar.DAY_OF_YEAR);
			}
		}
		
		int weekOver = totalDay%7;
		int numberOfDay = totalDay/7;
		
		if(numberOfDay%2 == 0) {
			return weekOver;
		}else {
			return weekOver+7;
		}
    }
	
	/**
	 * To url slug
	 */
	public static String toSlug(String input) {
		Pattern NONLATIN = Pattern.compile("[^\\w-]");
		Pattern WHITESPACE = Pattern.compile("[\\s]");
		String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
	    String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
	    String slug = NONLATIN.matcher(normalized).replaceAll("");
	    return slug.toLowerCase(Locale.ENGLISH);
	}

	public static String getDbName(String urlSlug) {
		return urlSlug.replace('-','_');
	}
}
