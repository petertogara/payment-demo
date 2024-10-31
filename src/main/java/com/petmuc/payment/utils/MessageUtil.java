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

    public String getPaymentProcessingErrorMessage(String reference, String errorDetails) {
        return String.format(messageSource.getMessage("payment.processing_error",null, Locale.getDefault()), reference, errorDetails);
    }

    public String getReversalProcessingErrorMessage(String reference, String errorDetails) {
        return String.format(messageSource.getMessage("payment.reversal_processing_error", null, Locale.getDefault()), reference, errorDetails);
    }

    public String getDefaultErrorMessage() {
        return String.format(messageSource.getMessage("generic_error", null, Locale.getDefault()));
    }
}