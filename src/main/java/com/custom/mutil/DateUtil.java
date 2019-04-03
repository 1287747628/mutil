package com.custom.mutil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private static final DateTimeFormatter yyyy_MM_dd_HH_mm_ss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter yyyyMMddHHmmss = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String format(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(dateTime);
    }

    public static String format(LocalDateTime dateTime) {
        return yyyy_MM_dd_HH_mm_ss.format(dateTime);
    }

    public static String format(Date date, String pattern) {
        LocalDateTime localDateTime = date4LocalDateTime(date);
        return format(localDateTime, pattern);
    }

    public static String format(Date date) {
        LocalDateTime localDateTime = date4LocalDateTime(date);
        return yyyy_MM_dd_HH_mm_ss.format(localDateTime);
    }

    public static String format_yyyyMMddHHmmss(LocalDateTime dateTime) {
        return yyyyMMddHHmmss.format(dateTime);
    }

    public static String format_yyyyMMddHHmmss(Date date) {
        LocalDateTime localDateTime = date4LocalDateTime(date);
        return yyyyMMddHHmmss.format(localDateTime);
    }

    public static String format_yyyyMMdd(Date date) {
        LocalDateTime localDateTime = date4LocalDateTime(date);
        return yyyyMMdd.format(localDateTime);
    }

    public static String format_yyyyMMdd(LocalDateTime dateTime) {
        return yyyyMMdd.format(dateTime);
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

    public static LocalDateTime parse(String dateStr, String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateStr, dtf);
    }

    public static Date parseDate(String dateStr, String pattern) {
        LocalDateTime localDateTime = parse(dateStr, pattern);
        return localDateTime4Date(localDateTime);
    }

    public static LocalDateTime parse(String dateStr) {
        return LocalDateTime.parse(dateStr, yyyy_MM_dd_HH_mm_ss);
    }

    public static Date parseDate(String dateStr) {
        LocalDateTime localDateTime = parse(dateStr);
        return localDateTime4Date(localDateTime);
    }

    public static LocalDateTime parse_yyyyMMddHHmmss(String dateStr) {
        return LocalDateTime.parse(dateStr, yyyyMMddHHmmss);
    }

    public static Date parseDate_yyyyMMddHHmmss(String dateStr) {
        LocalDateTime localDateTime = parse_yyyyMMddHHmmss(dateStr);
        return localDateTime4Date(localDateTime);
    }

    public static LocalDateTime parse_yyyyMMdd(String dateStr) {
        return LocalDateTime.parse(dateStr, yyyyMMdd);
    }

    public static Date parseDate_yyyyMMdd(String dateStr) {
        LocalDateTime localDateTime = parse_yyyyMMdd(dateStr);
        return localDateTime4Date(localDateTime);
    }

    public static void main(String[] args) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(dtf.format(LocalDateTime.now()));
    }

}
