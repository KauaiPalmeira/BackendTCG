package com.example.backend.repository;

import com.example.backend.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalRepository extends JpaRepository<Local, Long> {
}
