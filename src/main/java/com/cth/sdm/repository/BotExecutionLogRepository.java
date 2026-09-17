package com.cth.sdm.repository;

import com.cth.sdm.model.entity.BotExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BotExecutionLogRepository extends JpaRepository<BotExecutionLog, Long> {

    List<BotExecutionLog> findTop20ByOrderByIdDesc();
}
