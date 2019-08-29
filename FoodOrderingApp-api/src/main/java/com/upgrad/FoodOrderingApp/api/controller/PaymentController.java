package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
@RestController
//"@CrossOrigin” annotation enables cross-origin requests for all methods in that specific controller class.
@CrossOrigin
@RequestMapping("/")

public class PaymentController {

    //Required services are autowired to enable access to methods defined in respective Business services
    @Autowired
    private PaymentService paymentService;


    //getpaymentmethods endpoint retrieves all the payment methods details  present in the database
    @RequestMapping(method = RequestMethod.GET, path = "/payment",  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getpaymentmethods(){

        List<PaymentEntity> paymentEntityList=new ArrayList<PaymentEntity>();
        paymentEntityList.addAll(paymentService.getPaymentMethods());
        PaymentListResponse paymentListResponse=new PaymentListResponse();

        for (PaymentEntity paymentEntity : paymentEntityList) {

            PaymentResponse paymentResponse =new PaymentResponse();
            paymentResponse.setId(UUID.fromString(paymentEntity.getUuid()));
            paymentResponse.setPaymentName(paymentEntity.getPaymentName());
            paymentListResponse.addPaymentMethodsItem(paymentResponse);
        }

        return new ResponseEntity<PaymentListResponse>(paymentListResponse, HttpStatus.OK);
    }
}
