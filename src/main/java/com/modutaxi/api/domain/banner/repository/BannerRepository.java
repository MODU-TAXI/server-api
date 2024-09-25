package com.modutaxi.api.domain.banner.repository;

import com.modutaxi.api.domain.banner.entity.Banner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByActiveTrue();
}
