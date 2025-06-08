package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductDTO> getAllProducts(int page, int size);
    ProductDTO getProductById(Long id);
    Page<ProductDTO> getProductsByCategory(String categoryName, int page, int size);
    ProductDTO createProduct(ProductDTO dto);
    ProductDTO updateProduct(Long id, ProductDTO dto);
    void deleteProduct(Long id);
}
