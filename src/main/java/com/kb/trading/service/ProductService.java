package com.kb.trading.service;
import com.kb.trading.entity.Product;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.util.List;
public interface ProductService {
    // 发布商品
    Product publishProduct(Product product);

    // 更新商品信息
    Product updateProduct(Product product);

    // 根据ID获取商品
    Product getProductById(Long id);

    // 根据卖家ID获取商品列表
    List<Product> getProductsBySellerId(Long sellerId);

    // 获取所有上架商品（分页）
    Page<Product> getActiveProducts(int page, int size);

    // 搜索商品
    List<Product> searchProducts(String keyword);

    // 按分类获取商品
    List<Product> getProductsByCategory(String category);

    // 按地点获取商品
    List<Product> getProductsByLocation(String location);

    // 价格区间查询
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    // 下架商品
    Product deactivateProduct(Long productId, Long sellerId);

    // 重新上架商品
    Product activateProduct(Long productId, Long sellerId);

    // 标记商品为已售出
    Product markAsSold(Long productId, Long sellerId);

    // 增加商品浏览次数
    void increaseViewCount(Long productId);

    // 获取热门商品（按浏览量）
    List<Product> getHotProducts(int limit);

    // 删除商品（逻辑删除）
    void deleteProduct(Long productId, Long sellerId);
}

