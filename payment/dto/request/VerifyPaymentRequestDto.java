package com.bookmysport.backend.payment.dto.request;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyPaymentRequestDto {


    private Long paymentId;


    private String razorpayOrderId;


    private String razorpayPaymentId;


    private String razorpaySignature;

}
