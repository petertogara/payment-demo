package com.petmuc.payment.exception;

import com.petmuc.payment.utils.MessageUtil;

public enum ProcessType {
    PAYMENT {
        @Override
        public void handleError(String reference, String errorMessage, MessageUtil messageUtil) {
            final String detailedErrorMessage = messageUtil.getPaymentProcessingErrorMessage(reference, errorMessage);
            throw new PaymentProcessingException(detailedErrorMessage);
        }
    },
    REVERSAL {
        @Override
        public void handleError(String reference, String errorMessage, MessageUtil messageUtil) {
            final String detailedErrorMessage = messageUtil.getReversalProcessingErrorMessage(reference, errorMessage);
            throw new ReversalProcessingException(detailedErrorMessage);
        }
    };

    public abstract void handleError(String reference, String errorMessage, MessageUtil messageUtil);
}