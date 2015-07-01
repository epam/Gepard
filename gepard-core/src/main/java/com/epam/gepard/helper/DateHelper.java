package com.epam.gepard.helper;
/*==========================================================================
 Copyright 2004-2015 EPAM Systems

 This file is part of Gepard.

 Gepard is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Gepard is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Gepard.  If not, see <http://www.gnu.org/licenses/>.
===========================================================================*/

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Tamas_Kohegyi
 */
public class DateHelper {

    List<SimpleDateFormat> knownPatterns;

    public DateHelper() {
        knownPatterns = new ArrayList<>();
        knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"));
        knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));
        knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd"));
    }

    public Date getDateFromString(final String dateCandidate) {

        for (SimpleDateFormat pattern : knownPatterns) {
            try {
                // Take a try
                return new Date(pattern.parse(dateCandidate).getTime());
            } catch (ParseException pe) {
                // Loop on
            }
        }
        return null;
    }

    public String getLongStringFromDate(final Date date) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String dateString = DATE_FORMAT.format(date);
        return dateString;
    }

    public String getShortStringFromDate(final Date date) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = DATE_FORMAT.format(date);
        return dateString;
    }

    /**
     * Format a Calendar object as date into YYYY-MM-DD string format.
     *
     * @param cal is the source of the calendar.
     * @return Date string
     */
    public String getShortStringFromDate(final Calendar cal) {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMinimumIntegerDigits(2);
        df.setMaximumIntegerDigits(2);
        return cal.get(Calendar.YEAR) + "-" + df.format((long) cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DATE));
    }

    public long dayDiffsInWorkingDays(Date start, Date end){
        //Ignore argument check

        Calendar c1 = GregorianCalendar.getInstance();
        c1.setTime(start);
        int w1 = c1.get(Calendar.DAY_OF_WEEK);
        c1.add(Calendar.DAY_OF_WEEK, -w1 + 1);

        Calendar c2 = GregorianCalendar.getInstance();
        c2.setTime(end);
        int w2 = c2.get(Calendar.DAY_OF_WEEK);
        c2.add(Calendar.DAY_OF_WEEK, -w2 + 1);

        //end Saturday to start Saturday
        long days = (c2.getTimeInMillis()-c1.getTimeInMillis())/(1000*60*60*24);
        long daysWithoutSunday = days-(days*2/7);

        if (w1 == Calendar.SUNDAY) {
            w1 = Calendar.MONDAY;
        }
        if (w2 == Calendar.SUNDAY) {
            w2 = Calendar.MONDAY;
        }
        return daysWithoutSunday-w1+w2;
    }

    public Date addDays(final Date d, int i) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, i);  // number of days to add
        Date newDate = c.getTime();
        return newDate;
    }

}
