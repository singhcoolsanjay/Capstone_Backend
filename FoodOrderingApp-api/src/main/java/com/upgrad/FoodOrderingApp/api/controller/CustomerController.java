package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

//RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
@RestController
//"@CrossOrigin” annotation enables cross-origin requests for all methods in that specific controller class.
@CrossOrigin
@RequestMapping("/")
public class CustomerController {

    //Required services are autowired to enable access to methods defined in respective Business services
    @Autowired
    private CustomerService customerService;

    //signup  endpoint requests for all the attributes in “SignupCustomerRequest” about the customer and registers a customer successfully.
    //PLEASE NOTE @RequestBody(required = false) inside signup function will disable parameters in request body in request model.
    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup( @RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        final CustomerEntity customerEntity=new CustomerEntity();

        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setSalt("1234abc");
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        final CustomerEntity createdCustomerEntity = customerService.saveCustomer(customerEntity);

        SignupCustomerResponse customerResponse = new SignupCustomerResponse()
                .id(createdCustomerEntity.getUuid())
                .status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
    }

    //login method is used to perform a Basic authorization when the customer tries to login for the first time.
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authentication") final String authentication) throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authentication.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        final CustomerAuthEntity customerAuthToken = customerService.authenticate(decodedArray[0], decodedArray[1]);

        CustomerEntity customerEntity = customerAuthToken.getCustomer();

        LoginResponse loginResponse = new LoginResponse()
                .firstName(customerEntity.getFirstName())
                .lastName(customerEntity.getLastName())
                .emailAddress(customerEntity.getEmail())
                .contactNumber(customerEntity.getContactNumber())
                .id(customerEntity.getUuid())
                .message("LOGGED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        List<String> header = new ArrayList<>();
        header.add("access-token");
        headers.setAccessControlExposeHeaders(header);
        headers.add("access-token", customerAuthToken.getAccessToken());

        return new ResponseEntity<LoginResponse>(  loginResponse, headers, HttpStatus.OK);
    }

    //logout method is used to logout a loggedin customer from the application.
    @RequestMapping(method=RequestMethod.POST,path="/customer/logout",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("accessToken") final String accessToken)throws AuthorizationFailedException {

        String [] bearerToken = accessToken.split("Bearer ");
        final CustomerAuthEntity logout = customerService.logout(bearerToken[1]);

        LogoutResponse logoutResponse=new LogoutResponse()
                .id(logout.getUuid())
                .message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse,HttpStatus.OK);
    }

    //updateCustomer method is used to update the customer firstname and lastname from the application.
    @RequestMapping(method=RequestMethod.PUT,path="/customer",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(@RequestBody(required = false) final UpdateCustomerRequest updateCustomerRequest,
                                                         @RequestHeader("accessToken") final String accessToken) throws UpdateCustomerException,AuthorizationFailedException {

        String [] bearerToken = accessToken.split("Bearer ");
        CustomerEntity customerEntity = customerService.getCustomer(bearerToken[1]);
        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());

        final CustomerEntity updatedCustomerEntity=customerService.updateCustomer(customerEntity);

        UpdateCustomerResponse updateCustomerResponse=new UpdateCustomerResponse()
                .firstName(updatedCustomerEntity.getFirstName())
                .lastName(updatedCustomerEntity.getLastName())
                .id(updatedCustomerEntity.getUuid())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse,HttpStatus.OK);
    }

    //changePassword method is used to change the customer password details from the application.
    @RequestMapping(method=RequestMethod.PUT,path="/customer/password",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> changePassword(@RequestBody(required = false)final UpdatePasswordRequest updatePasswordRequest,
                                                         @RequestHeader("accessToken") final String accessToken) throws UpdateCustomerException,AuthorizationFailedException {

        String [] bearerToken = accessToken.split("Bearer ");
        CustomerEntity customerEntity = customerService.getCustomer(bearerToken[1]);

        final CustomerEntity updatedCustomerEntity=customerService.updateCustomerPassword(updatePasswordRequest.getOldPassword(),updatePasswordRequest.getNewPassword(),customerEntity);

        UpdatePasswordResponse updatePasswordResponse=new UpdatePasswordResponse()
                .id(updatedCustomerEntity.getUuid())
                .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse,HttpStatus.OK);
    }
}
