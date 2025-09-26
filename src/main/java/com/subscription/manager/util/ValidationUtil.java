package com.subscription.manager.util;

import java.time.LocalDate;

public class ValidationUtil {
    
    
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    
    public static boolean isPositiveAmount(double amount) {
        return amount > 0;
    }
    
    
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null && startDate.isBefore(endDate);
    }
    
    
    public static boolean isValidEngagementDuration(int months) {
        return months > 0;
    }
    
    
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    
    public static boolean isValidServiceName(String serviceName) {
        return isValidString(serviceName) && serviceName.length() >= 2 && serviceName.length() <= 100;
    }
    
    
    public static boolean isValidId(String id) {
        return isValidString(id);
    }
    
    
    public static boolean isValidDate(LocalDate date) {
        return date != null;
    }
}