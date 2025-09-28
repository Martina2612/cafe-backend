package com.cafe.demo.service;

import com.cafe.demo.model.Cafe;
import com.cafe.demo.repository.CafeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CafeService {

    @Autowired
    private CafeRepository cafeRepository;

    public Cafe guardarCafe(Cafe cafe){
        // Por defecto activo = false
        cafe.setActivo(false);
        return cafeRepository.save(cafe);
    }

    public List<Cafe> listarActivos(){
        return cafeRepository.findByActivoTrue();
    }

    public Optional<Cafe> obtenerPorId(Long id){
        return cafeRepository.findById(id);
    }

    public List<Cafe> buscarPorNombre(String nombre){
        return cafeRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    public List<Cafe> filtrarPorZona(String zona){
        return cafeRepository.findByZonaAndActivoTrue(zona);
    }
}
