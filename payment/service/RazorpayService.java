package com.bookmysport.backend.payment.service;

import com.bookmysport.backend.payment.config.RazorpayConfig;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RazorpayService {


    private final RazorpayConfig razorpayConfig;


    public Order createOrder(Long amount, String currency, String receipt) throws Exception {


        RazorpayClient client =
                new RazorpayClient(
                        razorpayConfig.getKeyId(),
                        razorpayConfig.getKeySecret()
                );


        JSONObject orderRequest = new JSONObject();

        orderRequest.put(
                "amount",
                amount
        );

        orderRequest.put(
                "currency",
                currency
        );

        orderRequest.put(
                "receipt",
                receipt
        );


        return client.orders.create(orderRequest);
    }
    public boolean verifyPayment(
            String orderId,
            String paymentId,
            String signature
    ) {


        try {

            String payload =
                    orderId + "|" + paymentId;


            return Utils.verifySignature(
                    payload,
                    signature,
                    razorpayConfig.getKeySecret()
            );


        } catch (Exception e) {

            return false;
        }

    }
}
