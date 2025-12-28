package com.kb.trading.repository;
import com.kb.trading.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>{
    // 根据用户ID和商品ID查找收藏
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    // 获取用户的收藏列表
    List<Favorite> findByUserId(Long userId);

    // 分页获取用户的收藏
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    // 获取商品的收藏数量
    Long countByProductId(Long productId);

    // 获取用户的收藏数量
    Long countByUserId(Long userId);

    // 检查是否已收藏
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // 根据用户ID和商品ID删除收藏
    void deleteByUserIdAndProductId(Long userId, Long productId);

    // 获取用户收藏的商品ID列表
    @Query("SELECT f.productId FROM Favorite f WHERE f.userId = :userId")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);

    // 获取热门收藏商品（收藏数最多的）
    @Query("SELECT f.productId, COUNT(f) as favoriteCount FROM Favorite f " +
            "GROUP BY f.productId ORDER BY favoriteCount DESC")
    List<Object[]> findHotFavoriteProducts(Pageable pageable);
}
