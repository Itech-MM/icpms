package org.flexitech.projects.icpms.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class DateUtils {

	public static Date stringToDate(String dateTime, String standardDateInputFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(standardDateInputFormat);
		Date retDate = new Date();
		try {
			retDate = sdf.parse(dateTime);
		} catch (ParseException e) {
			System.out.println("Error : " + e.getMessage());
		}
		return retDate;
	}

	public static String dateToString(Date dateTime, String standardDateInputFormat) {

		if (dateTime == null)
			return "";

		SimpleDateFormat sdf = new SimpleDateFormat(standardDateInputFormat);
		String retDate = sdf.format(dateTime);
		return retDate;
	}

	public static String getRelativeTime(Date date, String format) {
		if (date == null) {
			return "";
		}

		Instant now = Instant.now();
		Instant then = date.toInstant();
		Duration duration = Duration.between(then, now);

		long seconds = duration.getSeconds();
		long minutes = duration.toMinutes();
		long hours = duration.toHours();
		long days = duration.toDays();

		if (seconds < 60) {
			return "Just now";
		} else if (minutes < 60) {
			return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
		} else if (hours < 24) {
			return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
		} else if (days < 7) {
			return days + " day" + (days == 1 ? "" : "s") + " ago";
		} else if (days < 30) {
			long weeks = days / 7;
			return weeks + " week" + (weeks == 1 ? "" : "s") + " ago";
		} else {
			return dateToString(date, format);
		}
	}

}
