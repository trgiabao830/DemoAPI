package com.example.demo.service;

import com.example.demo.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO createCategory(CategoryDTO dto);
    CategoryDTO updateCategory(Long id, CategoryDTO dto);
    void deleteCategory(Long id);
}
