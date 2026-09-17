package com.cth.sdm.repository;

import com.cth.sdm.model.entity.BotConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotConfigRepository extends JpaRepository<BotConfig, String> {
}
