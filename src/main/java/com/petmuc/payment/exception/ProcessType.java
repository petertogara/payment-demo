package com.petmuc.payment.exception;

public enum ProcessType {
    PAYMENT {
        @Override
        public void handleError(String reference, String errorMessage) {
            throw new PaymentProcessingException(generateErrorMessage(reference, "payment", errorMessage));
        }
    },
    REVERSAL {
        @Override
        public void handleError(String reference, String errorMessage) {
            throw new ReversalProcessingException(generateErrorMessage(reference, "reversal", errorMessage));
        }
    };

    public abstract void handleError(String reference, String errorMessage);

    protected static String generateErrorMessage(String reference, String processType, String errorDetails) {
        return String.format("Error processing %s for reference %s: %s", processType, reference, errorDetails);
    }
}
