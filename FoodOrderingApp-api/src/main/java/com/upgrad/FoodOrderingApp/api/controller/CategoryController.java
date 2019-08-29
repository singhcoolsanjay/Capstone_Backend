package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//RestController annotation specifies that this class represents a REST API(equivalent of @Controller + @ResponseBody)
@RestController
//"@CrossOrigin” annotation enables cross-origin requests for all methods in that specific controller class.
@CrossOrigin
@RequestMapping("/")

public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;


    // Get All Categories - “/category”
    @RequestMapping(method = RequestMethod.GET, path = "/category",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CategoryListResponse>> getAllCategories(){
        List<CategoryEntity> categoryEntityList=new ArrayList<CategoryEntity>();
        categoryEntityList.addAll(categoryService.getAllCategories());

        List<CategoryListResponse> categoryListResponseList=new ArrayList<CategoryListResponse>();

        for (CategoryEntity categoryEntity : categoryEntityList) {

            CategoryListResponse categoryListResponse=new CategoryListResponse();
            categoryListResponseList.add(categoryListResponse
                    .categoryName(categoryEntity.getCategoryName())
                    .id(UUID.fromString(categoryEntity.getUuid())));
        }

        return new ResponseEntity<List<CategoryListResponse>>(categoryListResponseList, HttpStatus.OK);
    }

    //  Get Category by Id - “/category/{category_id}”
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable("category_id")final String categoryUuid) throws CategoryNotFoundException {

        CategoryEntity categoryEntity = categoryService.categoryByUUID(categoryUuid);

        List<CategoryItemEntity> categoryItemEntityList = categoryService.getCategoryItemListByCategory(categoryEntity);
        CategoryDetailsResponse categoryDetailsResponse=new CategoryDetailsResponse();

        for( CategoryItemEntity categoryItemEntity : categoryItemEntityList){

            CategoryList categoryList =new CategoryList();
            final CategoryEntity category = categoryItemEntity.getCategory();
            categoryList.id(UUID.fromString(category.getUuid()));
            categoryList.categoryName(category.getCategoryName());
           // categoryDetailsResponse.id(UUID.fromString(categoryEntity.getUuid()));
           // categoryDetailsResponse.categoryName(categoryEntity.getCategoryName());

            final ItemEntity itemEntity =categoryItemEntity.getItem();
            ItemList itemList=new ItemList();
            itemList.id(UUID.fromString(itemEntity.getUuid()));
            itemList.itemName(itemEntity.getItemName());
            itemList.price(itemEntity.getPrice());
            itemList.itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType()));

            categoryList.addItemListItem(itemList);
           //categoryDetailsResponse.addItemListItem(itemList);

        }

        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }

}

