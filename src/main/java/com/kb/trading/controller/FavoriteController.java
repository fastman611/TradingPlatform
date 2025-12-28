package com.kb.trading.controller;
import com.kb.trading.entity.Favorite;
import com.kb.trading.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    // 添加收藏
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(required = false) String note) {

        try {
            Favorite favorite = favoriteService.addFavorite(userId, productId, note);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "收藏成功");
            response.put("favorite", favorite);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 取消收藏
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @RequestParam Long userId,
            @RequestParam Long productId) {

        try {
            favoriteService.removeFavorite(userId, productId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "取消收藏成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取用户收藏列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserFavorites(@PathVariable Long userId) {
        try {
            List<Favorite> favorites = favoriteService.getUserFavorites(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("favorites", favorites);
            response.put("count", favorites.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 分页获取用户收藏
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Map<String, Object>> getUserFavoritesPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<Favorite> favoritePage = favoriteService.getUserFavoritesPage(userId, page, size);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("favorites", favoritePage.getContent());
            response.put("totalPages", favoritePage.getTotalPages());
            response.put("totalElements", favoritePage.getTotalElements());
            response.put("currentPage", page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 检查是否已收藏
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @RequestParam Long userId,
            @RequestParam Long productId) {

        try {
            boolean isFavorite = favoriteService.isFavorite(userId, productId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isFavorite", isFavorite);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取收藏数量
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getFavoriteCount(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId) {

        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            if (userId != null) {
                Long userCount = favoriteService.getUserFavoriteCount(userId);
                response.put("userFavoriteCount", userCount);
            }

            if (productId != null) {
                Long productCount = favoriteService.getFavoriteCount(productId);
                response.put("productFavoriteCount", productCount);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取热门收藏商品
    @GetMapping("/hot")
    public ResponseEntity<Map<String, Object>> getHotFavoriteProducts(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<Map<String, Object>> hotProducts = favoriteService.getHotFavoriteProducts(limit);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hotProducts", hotProducts);
            response.put("count", hotProducts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 批量检查收藏状态
    @PostMapping("/batch-check")
    public ResponseEntity<Map<String, Object>> batchCheckFavorite(
            @RequestParam Long userId,
            @RequestBody List<Long> productIds) {

        try {
            Map<Long, Boolean> statusMap = favoriteService.checkFavoriteStatus(userId, productIds);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("favoriteStatus", statusMap);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
