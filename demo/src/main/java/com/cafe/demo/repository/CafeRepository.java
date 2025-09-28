package com.cafe.demo.repository;

import com.cafe.demo.model.Cafe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    List<Cafe> findByActivoTrue();
    List<Cafe> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    List<Cafe> findByZonaAndActivoTrue(String zona);
}