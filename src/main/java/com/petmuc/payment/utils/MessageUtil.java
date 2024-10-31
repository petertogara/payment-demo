package com.petmuc.payment.utils;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {
    private final MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getCustomerNotFoundMessage(Long id) {
        return String.format(messageSource.getMessage("customer.not_found", null, Locale.getDefault()), id);
    }

    public String getCustomerAlreadyExistsMessage(String email) {
        return String.format(messageSource.getMessage("customer.already_exists", null, Locale.getDefault()), email);
    }

    public String getPaymentNotFoundMessage(Long id) {
        return String.format(messageSource.getMessage("payment.not_found", null, Locale.getDefault()), id);
    }

    public String getPaymentProcessingErrorMessage(String errorMessage) {
        return String.format(messageSource.getMessage("payment.processing_error", null, Locale.getDefault()), errorMessage);
    }

    public String getReversalProcessingErrorMessage(String errorMessage) {
        return String.format(messageSource.getMessage("payment.reversal_processing_error", null, Locale.getDefault()), errorMessage);
    }
    public String getPaymentProcessingErrorMessage(String reference, String errorDetails) {
        return String.format("Error processing payment for reference %s: %s", reference, errorDetails);
    }

    public String getReversalProcessingErrorMessage(String reference, String errorDetails) {
        return String.format("Error processing payment reversal for reference %s: %s", reference, errorDetails);
    }
}