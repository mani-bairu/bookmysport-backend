package com.bookmysport.backend.payment.service;

import com.bookmysport.backend.payment.config.RazorpayConfig;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayService {


    private final RazorpayConfig razorpayConfig;


    public Order createOrder(Long amount, String currency, String receipt) throws Exception {

        log.info("Razorpay created a razorpay client");

        RazorpayClient client = new RazorpayClient(
                        razorpayConfig.getKeyId(),
                        razorpayConfig.getKeySecret()
                        );

        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", amount);

        orderRequest.put("currency", currency);

        orderRequest.put("receipt", receipt);

//        orderRequest.put("expire_by",
//                Instant.now().plusSeconds(120).getEpochSecond());


        log.info("Razorpay service sent a Payment order request to Razorpay server");
        return client.orders.create(orderRequest);
    }


    public boolean verifyPayment(
            String orderId,
            String paymentId,
            String signature
    ) {

        try {

            String payload = orderId + "|" + paymentId;

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
