package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper ;

    @Override
    public CategoryResponse getAllCategories(Integer PageNumber,Integer PageSize, String sortBy, String sortorder ) {
        Sort sortByAndOrder = sortorder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(PageNumber, PageSize,  sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

       List<Category> categories = categoryPage.getContent();
       if(categories.isEmpty()){
           throw new APIException("No category yet created !!!!");
       }
       List<CategoryDTO> categoryDTOS = categories.stream()
               .map(category -> modelMapper.map(category,CategoryDTO.class))
               .toList();

       CategoryResponse categoryResponse = new CategoryResponse();
       categoryResponse.setContent(categoryDTOS);
       categoryResponse.setPageNumber(categoryPage.getNumber());
       categoryResponse.setPageSize(categoryPage.getSize());
       categoryResponse.setTotalElements((int) categoryPage.getTotalElements());
       categoryResponse.setTotalPages(categoryPage.getTotalPages());
      categoryResponse.setLastPage(categoryPage.isLast());


        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category savedCategoryfromDB = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());

        if(savedCategoryfromDB != null){
            throw new APIException("Category with the name " + categoryDTO.getCategoryName() + " already exits !!!");
        }

        Category category = modelMapper.map(categoryDTO, Category.class);
         Category savedCategory = categoryRepository.save(category);

        CategoryDTO savedcategoryDTO = modelMapper.map(savedCategory,CategoryDTO.class);

        return savedcategoryDTO;

    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));

         categoryRepository.delete(category);
         CategoryDTO deletedcategoryDTO = modelMapper.map(category,CategoryDTO.class);

        return deletedcategoryDTO;
    }



    @Override
    public CategoryDTO UpdatedCategory(CategoryDTO categoryDTO, Long categoryId){
     Category category = modelMapper.map(categoryDTO, Category.class);

      Category savedCategoryfromDB = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));

      category.setCategoryId(categoryId);
      Category savedCategory = categoryRepository.save(category);

      CategoryDTO savedCategoryDTO = modelMapper.map(savedCategory,CategoryDTO.class);

      return savedCategoryDTO;


    }

}
