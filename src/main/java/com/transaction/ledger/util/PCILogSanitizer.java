package com.transaction.ledger.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class PCILogSanitizer {
    
    @Value("${pci.logging.redaction.enabled:true}")
    private boolean redactionEnabled;
    
    private static final Pattern[] CARD_PATTERNS = {
        Pattern.compile("\\b4[0-9]{12}(?:[0-9]{3})?\\b"), // Visa
        Pattern.compile("\\b5[1-5][0-9]{14}\\b"), // MasterCard
        Pattern.compile("\\b3[47][0-9]{13}\\b"), // American Express
        Pattern.compile("\\b(?:6011|65[0-9]{2}|64[4-9][0-9])\\d{12}\\b") // Discover
    };
    
    private static final Pattern CVV_PATTERN = Pattern.compile("\\b[0-9]{3,4}\\b");
    
    public String sanitize(String input) {
        if (!redactionEnabled || input == null) {
            return input;
        }
        
        String result = input;
        
        // Sanitize card numbers
        for (Pattern pattern : CARD_PATTERNS) {
            Matcher matcher = pattern.matcher(result);
            result = matcher.replaceAll("[CARD_REDACTED]");
        }
        
        // Sanitize CVV (simple version - in real app, be more precise)
        result = result.replaceAll("\\b(cvv|cvc|security code)[:=\\s]*[0-9]{3,4}\\b", 
                                  "$1:[CVV_REDACTED]");
        
        return result;
    }
    
    public String sanitizeCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return "[NO_CARD]";
        }
        
        if (!redactionEnabled) {
            return cardNumber;
        }
        
        // Keep only last 4 digits
        String cleaned = cardNumber.replaceAll("[^0-9]", "");
        if (cleaned.length() <= 4) {
            return "[INVALID_CARD]";
        }
        
        return "****-****-****-" + cleaned.substring(cleaned.length() - 4);
    }
    
    public boolean isRedactionEnabled() {
        return redactionEnabled;
    }
}
