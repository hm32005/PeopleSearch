package wikipedia_solr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/*
 "1", "January", "1978"  -->  "19780101"
 "December", "7,", "1941," --> "19411207,"
 "84", "BC" ---> "-00840101"
 "1948" -->  "19480101" 
 "10:15", "am." --> "10:15:00"
 "January", "30,", "1948" --> "19480130"
 "5:15PM." --> "17:15:00."
 "847", "AD." --> "08470101."
 "2004" --> "20040101"
 "00:58:53", "UTC", "on", "Sunday,", "26", "December", "2004" --> "20041226 00:58:53"
 "April", "11" --> "19000411"
 */
public class DateUtil {
	private static List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add(new SimpleDateFormat("HH:mm:ss z 'on' EEEE',' dd MMMM yyyy"));
			add(new SimpleDateFormat("MMMM dd',' yyyy"));
			add(new SimpleDateFormat("MMMM dd',' yyyy"));
			add(new SimpleDateFormat("MMMM dd yyyy"));
			add(new SimpleDateFormat("dd MMMM yyyy"));
			add(new SimpleDateFormat("MMMM dd"));
			add(new SimpleDateFormat("h:mmaa"));
			add(new SimpleDateFormat("h:mm aa"));
			add(new SimpleDateFormat("yyyy G"));
			add(new SimpleDateFormat("yyyy G"));
			add(new SimpleDateFormat("yyyy"));
		}
	};

	public static Date parseDate(String stringDate) {
		Date date = null;
		String dateString = stringDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("EST"));
		if (stringDate != null) {
			for (SimpleDateFormat dateFormat : dateFormats) {
				try {
					dateFormat.setLenient(false);
					dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					dateFormat.setCalendar(calendar);
//					System.out.println(stringDate);
					date = dateFormat.parse(stringDate);
//					System.out.println(date);
				} catch (ParseException e) {

				}
				catch (NumberFormatException e) {
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				if (date != null) {
					break;
				}
			}
		}
		return date;
		/*if (date != null) {
			String era = "";
			calendar.setTime(date);
			if (calendar.get(Calendar.ERA) == 0) {
				era = "-";
			}
			if ((calendar.get(Calendar.HOUR_OF_DAY) == 0) && (calendar.get(Calendar.MINUTE) == 0) && (calendar.get(Calendar.SECOND) == 0)) {
				int year = calendar.get(Calendar.YEAR);
				if (calendar.get(Calendar.YEAR) != 1970 || stringDate.contains("1970"))
					dateString = era + String.format("%04d", year) + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
				else
					dateString = era + "1900" + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
			} else if (calendar.get(Calendar.YEAR) == 1970) {
				dateString = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", calendar.get(Calendar.SECOND));
			} else {
				dateString = era + String.format("%04d", calendar.get(Calendar.YEAR)) + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + " " + String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)) + ":" + String.format("%02d", calendar.get(Calendar.SECOND));
			}
		}
		return dateString;*/
	}

}
