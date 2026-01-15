package com.transaction.ledger.controller;

import com.transaction.ledger.util.PCILogSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/test")
public class PCILoggingController {
    
    private static final Logger logger = LoggerFactory.getLogger(PCILoggingController.class);
    
    @Autowired
    private PCILogSanitizer pciLogSanitizer;
    
    @PostMapping("/transaction")
    public Map<String, Object> simulateTransaction(@RequestBody TransactionRequest request) {
        String transactionId = UUID.randomUUID().toString();
        
        logger.info("=== Starting Transaction Processing ===");
        logger.info("Transaction ID: {}", transactionId);
        logger.info("Amount: ${}", request.getAmount());
        
        // This will show redacted card number in logs if PCI redaction is enabled
        logger.info("Original card number received: {}", request.getCardNumber());
        logger.info("Sanitized card number: {}", pciLogSanitizer.sanitizeCardNumber(request.getCardNumber()));
        
        logger.info("Cardholder: {}", request.getCardHolderName());
        logger.info("Transaction type: {}", request.getTransactionType());
        
        // Simulate processing
        try {
            Thread.sleep(100); // Simulate processing time
            logger.info("Transaction {} processed successfully", transactionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("transactionId", transactionId);
            response.put("status", "SUCCESS");
            response.put("amount", request.getAmount());
            response.put("currency", request.getCurrency());
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("cardLastFour", extractLastFour(request.getCardNumber()));
            response.put("message", "Transaction completed with PCI-aware logging");
            
            return response;
            
        } catch (InterruptedException e) {
            logger.error("Transaction processing interrupted", e);
            throw new RuntimeException("Processing error");
        }
    }
    
    @GetMapping("/log-demo")
    public Map<String, Object> logDemo() {
        logger.info("=== PCI Logging Demo ===");
        
        // Test various card numbers
        String[] testCards = {
            "4111111111111111", // Visa
            "5555555555554444", // MasterCard
            "378282246310005",  // American Express
            "6011111111111117"  // Discover
        };
        
        List<String> original = new ArrayList<>();
        List<String> sanitized = new ArrayList<>();
        
        for (String card : testCards) {
            original.add(card);
            sanitized.add(pciLogSanitizer.sanitizeCardNumber(card));
            
            logger.info("Original: {} -> Sanitized: {}", card, 
                       pciLogSanitizer.sanitizeCardNumber(card));
        }
        
        // Test log message with card data
        String logMessage = "Processing payment with card 4111111111111111 and CVV 123";
        logger.info("Original log message: {}", logMessage);
        logger.info("Sanitized log message: {}", pciLogSanitizer.sanitize(logMessage));
        
        Map<String, Object> response = new HashMap<>();
        response.put("demo", "Check application logs for PCI redaction examples");
        response.put("pciRedactionEnabled", pciLogSanitizer.isRedactionEnabled());
        response.put("testCards", Map.of(
            "original", original,
            "sanitized", sanitized
        ));
        response.put("timestamp", LocalDateTime.now().toString());
        
        return response;
    }
    
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("pciRedactionEnabled", pciLogSanitizer.isRedactionEnabled());
        config.put("dbHost", System.getenv("DB_HOST"));
        config.put("dbName", System.getenv("DB_NAME"));
        config.put("springProfile", System.getenv("SPRING_PROFILES_ACTIVE"));
        config.put("containerized", true);
        config.put("timestamp", LocalDateTime.now().toString());
        
        return config;
    }
    
    private String extractLastFour(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "N/A";
        }
        String digitsOnly = cardNumber.replaceAll("[^0-9]", "");
        return digitsOnly.length() > 4 ? 
               digitsOnly.substring(digitsOnly.length() - 4) : 
               digitsOnly;
    }
    
    // Request DTOs
    public static class TransactionRequest {
        private String cardNumber;
        private String cardHolderName;
        private Double amount;
        private String currency = "USD";
        private String transactionType = "PURCHASE";
        
        // Getters and setters
        public String getCardNumber() { return cardNumber; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
        
        public String getCardHolderName() { return cardHolderName; }
        public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public String getTransactionType() { return transactionType; }
        public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    }
}
