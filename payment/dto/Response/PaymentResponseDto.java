package com.bookmysport.backend.payment.dto.Response;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponseDto {

    private Long paymentId;

    private String orderId;

    private Long amount;

    private String currency;

}
