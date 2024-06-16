package com.modutaxi.api.domain.history.repository;

import com.modutaxi.api.domain.history.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
