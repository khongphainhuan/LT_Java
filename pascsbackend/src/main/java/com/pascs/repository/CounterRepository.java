package com.pascs.repository;

import com.pascs.model.Counter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Long> {
    List<Counter> findByActiveTrue();
    List<Counter> findByLocation(String location);
}