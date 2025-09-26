package com.subscription.manager.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

public class DateUtil {
    
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    
    public static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        return LocalDate.parse(dateStr, DEFAULT_FORMATTER);
    }
    
    
    public static String formatDate(LocalDate date) {
        return date.format(DEFAULT_FORMATTER);
    }
    
    
    public static boolean isInPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }
    
    
    public static boolean isOverdue(LocalDate echeanceDate) {
        return echeanceDate.isBefore(LocalDate.now());
    }
    
    
    public static long monthsBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.MONTHS.between(start, end);
    }
    
    
    public static List<LocalDate> generateMonthlyDueDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dueDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        while (currentDate.isBefore(endDate)) {
            dueDates.add(currentDate);
            currentDate = currentDate.plusMonths(1);
        }
        
        return dueDates;
    }
    
    
    public static LocalDate addMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }
    
    
    public static LocalDate getFirstDayOfMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }
    
    
    public static LocalDate getLastDayOfMonth() {
        LocalDate now = LocalDate.now();
        return now.withDayOfMonth(now.lengthOfMonth());
    }
    
    
    public static LocalDate getFirstDayOfYear() {
        return LocalDate.now().withDayOfYear(1);
    }
    
    
    public static LocalDate getLastDayOfYear() {
        LocalDate now = LocalDate.now();
        return now.withDayOfYear(now.lengthOfYear());
    }
}