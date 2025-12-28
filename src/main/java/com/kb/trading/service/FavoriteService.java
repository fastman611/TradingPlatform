package com.kb.trading.service;
import com.kb.trading.entity.Favorite;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;

public interface FavoriteService {
    // 添加收藏
    Favorite addFavorite(Long userId, Long productId, String note);

    // 取消收藏
    void removeFavorite(Long userId, Long productId);

    // 获取用户收藏列表
    List<Favorite> getUserFavorites(Long userId);

    // 分页获取用户收藏
    Page<Favorite> getUserFavoritesPage(Long userId, int page, int size);

    // 检查是否已收藏
    boolean isFavorite(Long userId, Long productId);

    // 获取用户收藏的商品ID列表
    List<Long> getUserFavoriteProductIds(Long userId);

    // 获取商品的收藏数量
    Long getFavoriteCount(Long productId);

    // 获取用户的收藏数量
    Long getUserFavoriteCount(Long userId);

    // 获取热门收藏商品
    List<Map<String, Object>> getHotFavoriteProducts(int limit);

    // 批量检查收藏状态
    Map<Long, Boolean> checkFavoriteStatus(Long userId, List<Long> productIds);
}
