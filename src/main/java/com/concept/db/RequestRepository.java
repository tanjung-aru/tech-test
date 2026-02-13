package com.concept.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
}
