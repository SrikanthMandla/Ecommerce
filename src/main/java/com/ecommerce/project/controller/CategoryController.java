package com.ecommerce.project.controller;

import com.ecommerce.project.Config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @Tag(name = "Categories API", description = "APIs for managing categories")
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(name = "PageNumber",
            defaultValue = AppConstants.Page_Number, required = false) Integer pageNumber,
            @RequestParam(name = "PageSize", defaultValue = AppConstants.Page_Size, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false)  String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false)  String sortOrder) {
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber,pageSize,sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> CreateCategory(@Valid @RequestBody CategoryDTO category) {
        //CategoryResponse categoryResponse = categoryService.createCategory(category);
       // return "Category added Successfully";

        CategoryDTO categoryDTO = categoryService.createCategory(category);
        return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> DeleteCategory(@PathVariable Long categoryId) {

            CategoryDTO categoryDTO= categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> UpdateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId) {
            CategoryDTO savedCategory = categoryService.UpdatedCategory(categoryDTO,categoryId);
            return  new ResponseEntity<>(savedCategory, HttpStatus.OK);

           // return new ResponseEntity<>("Category with categoryId " + categoryId + " updated successfully",HttpStatus.OK);
    }
}
