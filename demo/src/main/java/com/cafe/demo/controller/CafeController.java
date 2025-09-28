package com.cafe.demo.controller;

import com.cafe.demo.model.Cafe;
import com.cafe.demo.model.Comentario;
import com.cafe.demo.model.Horario;
import com.cafe.demo.service.CafeService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/cafes")
public class CafeController {

    @Autowired
    private Cloudinary cloudinary;

    private final CafeService cafeService;

    public CafeController(CafeService cafeService) {
        this.cafeService = cafeService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearCafe(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam String direccion,
            @RequestParam String zona,
            @RequestParam(required = false) List<String> dias,
            @RequestParam(required = false) List<String> aperturas,
            @RequestParam(required = false) List<String> cierres,
            @RequestParam(required = false) List<MultipartFile> imagenes,
            @RequestParam(required = false) MultipartFile menuPdf
    ) {
        try {
            Cafe cafe = new Cafe();
            cafe.setNombre(nombre);
            cafe.setDescripcion(descripcion);
            cafe.setDireccion(direccion);
            cafe.setZona(zona);

            // Guardar horarios
            if (dias != null && aperturas != null && cierres != null &&
                dias.size() == aperturas.size() && dias.size() == cierres.size()) {
                for (int i = 0; i < dias.size(); i++) {
                    Horario h = new Horario();
                    h.setDia(dias.get(i));
                    h.setApertura(aperturas.get(i));
                    h.setCierre(cierres.get(i));
                    h.setCafe(cafe);
                    cafe.getHorarios().add(h);
                }
            }

            List<String> urlsImagenes = new ArrayList<>();
        if (imagenes != null) {
            for (MultipartFile img : imagenes) {
                Map uploadResult = cloudinary.uploader().upload(img.getBytes(),
                        ObjectUtils.asMap("folder", "cafes"));
                String url = uploadResult.get("secure_url").toString();
                urlsImagenes.add(url);
            }
        }
        cafe.setImagenes(urlsImagenes);

        if (menuPdf != null) {
            Map uploadResult = cloudinary.uploader().upload(menuPdf.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "cafes",
                            "resource_type", "raw" // importante para PDF
                    ));
            String pdfUrl = uploadResult.get("secure_url").toString();
            cafe.setMenuPdf(pdfUrl);
        }

            Cafe guardado = cafeService.guardarCafe(cafe);
            return ResponseEntity.ok(guardado);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al guardar archivos: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    public List<Cafe> listarActivos(){
        return cafeService.listarActivos();
    }

    @GetMapping("/buscar")
    public List<Cafe> buscarPorNombre(@RequestParam String nombre){
        return cafeService.buscarPorNombre(nombre);
    }

    @GetMapping("/filtrar")
    public List<Cafe> filtrarPorZona(@RequestParam String zona){
        return cafeService.filtrarPorZona(zona);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id){
        return cafeService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{cafeId}/comentarios")
    public ResponseEntity<?> agregarComentario(
            @PathVariable Long cafeId,
            @RequestParam String nombre,
            @RequestParam String texto) {

        return cafeService.obtenerPorId(cafeId).map(cafe -> {
            Comentario c = new Comentario();
            c.setNombre(nombre);
            c.setTexto(texto);
            c.setCafe(cafe);
            c.setActivo(false); // requiere aprobación
            cafe.getComentarios().add(c);
            cafeService.guardarCafe(cafe);
            return ResponseEntity.ok("Comentario enviado y pendiente de aprobación.");
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{cafeId}/comentarios")
    public ResponseEntity<?> listarComentarios(@PathVariable Long cafeId) {
        return cafeService.obtenerPorId(cafeId).map(cafe -> {
            List<Comentario> activos = cafe.getComentarios().stream()
                    .filter(Comentario::getActivo)
                    .toList();
            return ResponseEntity.ok(activos);
        }).orElse(ResponseEntity.notFound().build());
    }


}
