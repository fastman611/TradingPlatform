package com.kb.trading.controller;
import com.kb.trading.entity.Product;
import com.kb.trading.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    // 发布商品
    @PostMapping("/publish")
    public Product publishProduct(@RequestBody Product product) {
        return productService.publishProduct(product);
    }

    // 更新商品
    @PutMapping("/update")
    public Product updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    // 获取商品详情
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // 获取卖家商品列表
    @GetMapping("/seller/{sellerId}")
    public List<Product> getProductsBySellerId(@PathVariable Long sellerId) {
        return productService.getProductsBySellerId(sellerId);
    }

    // 获取所有上架商品（分页）
    @GetMapping("/list")
    public Page<Product> getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getActiveProducts(page, size);
    }

    // 搜索商品
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    // 按分类查询
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }

    // 按地点查询
    @GetMapping("/location/{location}")
    public List<Product> getProductsByLocation(@PathVariable String location) {
        return productService.getProductsByLocation(location);
    }

    // 价格区间查询
    @GetMapping("/price-range")
    public List<Product> getProductsByPriceRange(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return productService.getProductsByPriceRange(minPrice, maxPrice);
    }

    // 下架商品
    @PutMapping("/deactivate/{productId}")
    public Product deactivateProduct(
            @PathVariable Long productId,
            @RequestParam Long sellerId) {
        return productService.deactivateProduct(productId, sellerId);
    }

    // 上架商品
    @PutMapping("/activate/{productId}")
    public Product activateProduct(
            @PathVariable Long productId,
            @RequestParam Long sellerId) {
        return productService.activateProduct(productId, sellerId);
    }

    // 标记为已售出
    @PutMapping("/sold/{productId}")
    public Product markAsSold(
            @PathVariable Long productId,
            @RequestParam Long sellerId) {
        return productService.markAsSold(productId, sellerId);
    }

    // 获取热门商品
    @GetMapping("/hot")
    public List<Product> getHotProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return productService.getHotProducts(limit);
    }

    // 删除商品（逻辑删除）
    @DeleteMapping("/{productId}")
    public String deleteProduct(
            @PathVariable Long productId,
            @RequestParam Long sellerId) {
        productService.deleteProduct(productId, sellerId);
        return "商品删除成功";
    }
}
