package com.upmudoum.trade.domain.chart.repository;

import com.upmudoum.trade.domain.chart.entity.ChartDrawing;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartDrawingRepository extends JpaRepository<ChartDrawing, Long> {

    List<ChartDrawing> findByUserIdAndItemCodeOrderByUpdatedAtDesc(String userId, String itemCode);

    Optional<ChartDrawing> findByIdAndUserIdAndItemCode(Long id, String userId, String itemCode);
}
