package com.jtf.orderservice.api.service;

import com.jtf.orderservice.api.common.Payment;
import com.jtf.orderservice.api.common.TransactionRequest;
import com.jtf.orderservice.api.common.TransactionResponse;
import com.jtf.orderservice.api.entity.Order;
import com.jtf.orderservice.api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

//    public Order saveOrder(Order order) {
//        return orderRepository.save(order);
//    }

    public TransactionResponse saveOrder(TransactionRequest request) {
        String resMessage = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());
        //rest call
        //Payment paymentResponse = restTemplate.postForObject("http://localhost:9191/payment/doPayment", payment, Payment.class);

        /*instaed of mapping the host and port map the service name */
        Payment paymentResponse = restTemplate.postForObject("http://PAYMENT-SERVICE/payment/doPayment", payment, Payment.class);
        resMessage = paymentResponse.getPaymentStatus().equals("success") ? "payment processing is sucessful and order placed" : "there is a failure in payment API order added to cart";

        orderRepository.save(order);
        return new TransactionResponse(order, paymentResponse.getTransactionId(), paymentResponse.getAmount(), resMessage);
    }
}
