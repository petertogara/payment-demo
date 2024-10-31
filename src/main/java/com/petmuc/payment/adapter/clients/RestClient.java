package com.petmuc.payment.adapter.clients;

import com.petmuc.payment.api.dtos.PaymentRequest;
import com.petmuc.payment.api.dtos.PaymentResponse;
import com.petmuc.payment.api.dtos.ReversalRequest;
import com.petmuc.payment.api.dtos.ReversalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "paymentClient", url = "https://api.example.com/payments")
public interface RestClient {
    @PostMapping
    ResponseEntity<PaymentResponse> payment(@RequestBody PaymentRequest request);

    @PostMapping("/reversal")
    ResponseEntity<ReversalResponse> paymentReversal(@RequestBody ReversalRequest request);
}
