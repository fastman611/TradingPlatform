package com.kb.trading.service.impl;
import com.kb.trading.entity.Favorite;
import com.kb.trading.entity.Product;
import com.kb.trading.repository.FavoriteRepository;
import com.kb.trading.repository.ProductRepository;
import com.kb.trading.repository.UserRepository;
import com.kb.trading.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService{
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Favorite addFavorite(Long userId, Long productId, String note) {
        // 验证用户和商品是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 检查是否已收藏
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("该商品已在收藏夹中");
        }

        // 创建收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setNote(note);

        Favorite savedFavorite = favoriteRepository.save(favorite);

        // 增加商品的收藏数
        product.setLikeCount(product.getLikeCount() + 1);
        productRepository.save(product);

        log.info("用户 {} 收藏了商品 {}", userId, productId);

        return savedFavorite;
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        // 验证收藏是否存在
        Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("收藏记录不存在"));

        // 删除收藏
        favoriteRepository.delete(favorite);

        // 减少商品的收藏数
        productRepository.findById(productId).ifPresent(product -> {
            if (product.getLikeCount() > 0) {
                product.setLikeCount(product.getLikeCount() - 1);
                productRepository.save(product);
            }
        });

        log.info("用户 {} 取消收藏商品 {}", userId, productId);
    }

    @Override
    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Override
    public Page<Favorite> getUserFavoritesPage(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return favoriteRepository.findByUserId(userId, pageable);
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public List<Long> getUserFavoriteProductIds(Long userId) {
        return favoriteRepository.findProductIdsByUserId(userId);
    }

    @Override
    public Long getFavoriteCount(Long productId) {
        return favoriteRepository.countByProductId(productId);
    }

    @Override
    public Long getUserFavoriteCount(Long userId) {
        return favoriteRepository.countByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getHotFavoriteProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = favoriteRepository.findHotFavoriteProducts(pageable);

        return results.stream().map(result -> {
            Long productId = (Long) result[0];
            Long count = (Long) result[1];

            Map<String, Object> map = new HashMap<>();
            map.put("productId", productId);
            map.put("favoriteCount", count);

            // 获取商品信息
            productRepository.findById(productId).ifPresent(product -> {
                map.put("productTitle", product.getTitle());
                map.put("productPrice", product.getPrice());
                map.put("productImage", product.getMainImage());
            });

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<Long, Boolean> checkFavoriteStatus(Long userId, List<Long> productIds) {
        Map<Long, Boolean> statusMap = new HashMap<>();

        // 获取用户已收藏的商品ID列表
        List<Long> favoriteProductIds = getUserFavoriteProductIds(userId);

        // 构建状态映射
        for (Long productId : productIds) {
            statusMap.put(productId, favoriteProductIds.contains(productId));
        }

        return statusMap;
    }
}
