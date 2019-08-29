/*
package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
@RestController
//"@CrossOrigin” annotation enables cross-origin requests for all methods in that specific controller class.
@CrossOrigin
@RequestMapping("/")
public class ItemController {

    //Required services are autowired to enable access to methods defined in respective Business services
    @Autowired
    private ItemService itemService;

    @RequestMapping(method = RequestMethod.GET, path = "/item/restaurant/{restaurant_id}",  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getItemsByRestaurantId(@PathVariable("restaurant_id") final String restaurantUuid) throws RestaurantNotFoundException {
        final RestaurantEntity restaurantEntity = itemService.getRestaurantByUUID(restaurantUuid);
        if(restaurantEntity == null ) {
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        } else {
            final RestaurantIte

        }
            restaurantEntity.getId()
        }

    }
*/
