package com.kb.trading.repository;
import com.kb.trading.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 根据卖家ID查询商品
    List<Product> findBySellerId(Long sellerId);

    // 根据状态查询商品
    List<Product> findByStatus(Integer status);

    // 根据分类查询商品
    List<Product> findByCategory(String category);

    // 根据地点查询商品
    List<Product> findByLocationContaining(String location);

    // 价格区间查询
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // 搜索商品（标题或描述中包含关键词）
    @Query(value = "SELECT p FROM Product p WHERE p.status = 1 AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    // 分页查询上架商品
    Page<Product> findByStatus(Integer status, Pageable pageable);

    // 根据多个条件查询（分类+价格区间）
    List<Product> findByCategoryAndPriceBetween(String category,
                                                BigDecimal minPrice,
                                                BigDecimal maxPrice);

    // 统计卖家商品数量
    Long countBySellerId(Long sellerId);
}
