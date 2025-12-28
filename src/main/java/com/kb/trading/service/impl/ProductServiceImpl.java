package com.kb.trading.service.impl;
import com.kb.trading.entity.Product;
import com.kb.trading.repository.ProductRepository;
import com.kb.trading.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Override
    public Product publishProduct(Product product) {
        // 验证必要字段
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new RuntimeException("商品标题不能为空");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("商品价格必须大于0");
        }
        if (product.getSellerId() == null) {
            throw new RuntimeException("卖家ID不能为空");
        }

        // 设置默认值
        product.setStatus(1); // 上架状态
        product.setViewCount(0);
        product.setLikeCount(0);

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        // 检查商品是否存在
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 检查操作者是否为商品所有者
        if (!existingProduct.getSellerId().equals(product.getSellerId())) {
            throw new RuntimeException("无权修改此商品");
        }

        // 更新字段
        existingProduct.setTitle(product.getTitle());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setOriginalPrice(product.getOriginalPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setLocation(product.getLocation());
        existingProduct.setStock(product.getStock());

        return productRepository.save(existingProduct);
    }

    @Override
    public Product getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 增加浏览次数
        increaseViewCount(id);

        return product;
    }

    @Override
    public List<Product> getProductsBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    @Override
    public Page<Product> getActiveProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return productRepository.findByStatus(1, pageable);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findByStatus(1);
        }
        return productRepository.searchProducts(keyword.trim());
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> getProductsByLocation(String location) {
        return productRepository.findByLocationContaining(location);
    }

    @Override
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null) minPrice = BigDecimal.ZERO;
        if (maxPrice == null) maxPrice = new BigDecimal("9999999");

        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    public Product deactivateProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权操作此商品");
        }

        product.setStatus(0); // 下架
        return productRepository.save(product);
    }

    @Override
    public Product activateProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权操作此商品");
        }

        product.setStatus(1); // 上架
        return productRepository.save(product);
    }

    @Override
    public Product markAsSold(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权操作此商品");
        }

        product.setStatus(2); // 已售出
        return productRepository.save(product);
    }

    @Override
    public void increaseViewCount(Long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setViewCount(product.getViewCount() + 1);
            productRepository.save(product);
        });
    }

    @Override
    public List<Product> getHotProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("viewCount").descending());
        return productRepository.findByStatus(1, pageable).getContent();
    }

    @Override
    public void deleteProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权删除此商品");
        }

        product.setStatus(3); // 标记为已删除（逻辑删除）
        productRepository.save(product);
    }
}
