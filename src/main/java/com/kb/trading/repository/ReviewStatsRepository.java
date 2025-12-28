package com.kb.trading.repository;
import com.kb.trading.entity.ReviewStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ReviewStatsRepository extends JpaRepository<ReviewStats, Long> {
    // 根据目标ID和类型查找统计
    Optional<ReviewStats> findByTargetIdAndTargetType(Long targetId, String targetType);

    // 根据目标ID查找
    Optional<ReviewStats> findByTargetId(Long targetId);

    // 删除目标的统计
    void deleteByTargetId(Long targetId);
}
