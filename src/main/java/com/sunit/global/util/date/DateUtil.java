package com.sunit.global.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.print.attribute.DateTimeSyntax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunit.global.util.SunitStringUtil;

public class DateUtil {

	private static final ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal();

	private final static String MONTH_FORMAT = new String("yyyy-MM");

	private final static String SIMPLE_FORMAT = new String("yyyy-MM-dd");

	private final static String LONG_FORMAT = new String("yyyy-MM-dd HH:mm:ss");

	private final static String LONG_FORMAT_MINUTES = new String(
			"yyyy-MM-dd HH:mm");

	private final static String CN_FORMAT = new String("yyyy年MM月dd日");

	private final static String L8_DATE_FORMAT = new String("yyyyMMdd");

	private final static Log log = LogFactory.getLog(DateUtil.class);

	
	
	public static SimpleDateFormat getDateFormat(String format) {

		SimpleDateFormat sd = dateFormats.get();
		if (sd == null) {
			sd = new SimpleDateFormat(format); 
			dateFormats.set(sd);
		}
		sd.applyPattern(format);
		return sd;
	}
	
	public static Date parseDate(String string) {
		Date date = null;

		if (null == string || 0 == string.length())
			return null;

		try {
			if (string.length() > 10)
				date =  getDateFormat(LONG_FORMAT).parse(string);
			else
				date =getDateFormat(SIMPLE_FORMAT).parse(string);
		} catch (ParseException e) { 
			log.error(DateUtil.class, e);
		}

		return date;
	}
	

	public static Date parseDate(String format, String date) {
		SimpleDateFormat fmt = getDateFormat(format);
		Date dt = null;
		try {
			dt = fmt.parse(date);
		} catch (ParseException e) {
			log.error(DateUtil.class, e);
		}

		return dt;
	}

	public static Date ifParse(String string) {

		Date date = null;

		if (SunitStringUtil.isBlankOrNull(string))
			return null;
		try {
			if (string.length() > 10)
				date = getDateFormat(DateStyle.YYYY_MM_DD_HH_MM_SS.getValue())
						.parse(string);
			else if (string.length() > 7)
				date = getDateFormat(DateStyle.YYYY_MM_DD.getValue()).parse(
						string);
			else
				date = getDateFormat(DateStyle.YYYY_MM.getValue())
						.parse(string);
		} catch (ParseException e) {
			log.error(DateUtil.class, e);
		}
		return date;

	}

	public static String getCurrentTime() {
		return getDateFormat(DateStyle.YYYY_MM_DD_HH_MM_SS.getValue()).format(
				Calendar.getInstance().getTime());
	}

	public static String getCurrentDate() {
		return getDateFormat(DateStyle.YYYY_MM_DD.getValue()).format(
				Calendar.getInstance().getTime());
	}

	public static String getL8CurrentDate() {
		return getDateFormat(DateStyle.YYYYMMDD.getValue()).format(
				Calendar.getInstance().getTime());
		// return L8_DATE_FORMAT.format(Calendar.getInstance().getTime());
	}

	public static SimpleDateFormat getLongFormat() {
		return getDateFormat(DateStyle.YYYY_MM_DD_HH_MM_SS.getValue());
	}

	public static SimpleDateFormat getSimpleFormat() {
		return getDateFormat(DateStyle.YYYY_MM_DD.getValue());
	}

	public static SimpleDateFormat getMonthFormat() {
		return getDateFormat(DateStyle.YYYY_MM.getValue());
	}

	public static SimpleDateFormat getMinuteFormat() { 
		return getDateFormat(DateStyle.YYYY_MM_DD_HH_MM.getValue());
	}
	
	public static String toL8Style(String date) {
		Date d =  ifParse(date);
		return getDateFormat(DateStyle.YYYYMMDD.getValue()).format(d);
	}
	
	
	/**
	 * YYMMDDHHMMSS
	* @Title: toL12Style 
	* @Description: 
	* @param @param date
	* @param @return     
	* @return String  
	* @throws 
	* @author joye 
	* 2018年1月22日 下午6:30:55
	 */
	public static String toL12Style(String date) { 
		Date d =  ifParse(date);
		return getDateFormat(DateStyle.YYMMDDHHMMSS.getValue()).format(d);
	}
	
	
	/**
	 * 日期计算
	* @Title: calcDate 
	* @Description: 
	* @param @param scale
	* @param @return     
	* @return Date  
	* @throws 
	* @author joye 
	* 2018年1月16日 上午10:46:13
	 */
	public static  Date  calcDate(int scale) { 
		Calendar ca =Calendar.getInstance();
		ca.set(ca.DATE, scale);
		return ca.getTime(); 
	}
	
	
	public static  String  calcDateToStringYYYYMMDD(int scale) { 
		Calendar ca =Calendar.getInstance();
		ca.add(ca.DATE, scale);  
		return  getDateFormat(DateStyle.YYYYMMDD.getValue()).format(ca.getTime());
	}

	public static void main(String[] args) {
		
		System.out.println(	DateUtil.calcDateToStringYYYYMMDD(0));; 
	System.out.println(	DateUtil.calcDateToStringYYYYMMDD(-1));; 
	 
	Calendar ca =Calendar.getInstance();
	ca.add(ca.DAY_OF_MONTH, -1);  
	System.out.println(DateUtil.getDateFormat(DateStyle.YYYYMMDD.getValue()).format(ca.getTime()));;

	
	
	
	}

	public static String getL8DateFormat() {
		return L8_DATE_FORMAT;
	}
	
}
