package com.bookmysport.backend.payment.entity;

import com.bookmysport.backend.booking.entity.BookingEntity;
import com.bookmysport.backend.common.Entity.BaseEntity;
import com.bookmysport.backend.payment.enums.PaymentGateway;
import com.bookmysport.backend.payment.enums.PaymentStatus;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /*
     * One booking can have multiple payment attempts.
     *
     * Example:
     * Booking 101
     *    Payment attempt 1 -> FAILED
     *    Payment attempt 2 -> SUCCESS
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "booking_id",
            nullable = false
    )
    private BookingEntity booking;


    /*
     * Amount user needs to pay
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;


    /*
     * Example:
     * INR
     * USD
     */
    @Column(nullable = false, length = 3)
    private String currency;


    /*
     * Payment provider
     *
     * Example:
     * RAZORPAY
     * PAYU
     * PHONEPE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentGateway gateway;


    /*
     * Generic gateway order/reference id
     *
     * Razorpay:
     * order_xxxxx
     *
     * PayU:
     * txn_xxxxx
     */
    @Column(name = "gateway_order_id")
    private String gatewayOrderId;


    /*
     * Actual payment transaction id
     *
     * Razorpay:
     * pay_xxxxx
     */
    @Column(name = "gateway_payment_id")
    private String gatewayPaymentId;


    /*
     * Used for payment verification
     *
     * Razorpay signature
     * or equivalent verification data
     */
    @Column(name = "gateway_signature")
    private String gatewaySignature;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

}
