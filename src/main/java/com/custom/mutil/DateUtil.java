package com.custom.mutil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMdd = "yyyyMMdd";

    public static String format(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(dateTime);
    }

    public static String format(LocalDateTime dateTime) {
        return format(dateTime, yyyy_MM_dd_HH_mm_ss);
    }

    public static String format(Date date, String pattern) {
        LocalDateTime localDateTime = date4LocalDateTime(date);
        return format(localDateTime, pattern);
    }

    public static String format(Date date) {
        LocalDateTime localDateTime = date4LocalDateTime(date);
        return format(localDateTime, yyyy_MM_dd_HH_mm_ss);
    }

    public static LocalDateTime date4LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static Date localDateTime4Date(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static LocalDateTime parse(String dateStr) {
        return parse(dateStr, yyyy_MM_dd_HH_mm_ss);
    }

    public static LocalDateTime parse(String dateStr, String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateStr, dtf);
    }

    public static Date parseDate(String dateStr) {
        return parseDate(dateStr,yyyy_MM_dd_HH_mm_ss);
    }

    public static Date parseDate(String dateStr, String pattern) {
        LocalDateTime localDateTime = parse(dateStr, pattern);
        return localDateTime4Date(localDateTime);
    }

    public static void main(String[] args) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(dtf.format(LocalDateTime.now()));
    }

}
