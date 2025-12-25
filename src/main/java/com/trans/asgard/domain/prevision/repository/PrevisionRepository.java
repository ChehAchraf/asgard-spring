package com.trans.asgard.domain.prevision.repository;

import com.trans.asgard.domain.prevision.model.Prevision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrevisionRepository extends JpaRepository<Prevision, Long> {
}