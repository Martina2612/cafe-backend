package com.cafe.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cafes")
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String nombre;

    private String descripcion;

    private String direccion;

    private String zona; // ej: Palermo, Recoleta

    private boolean activo = false; // se aprueba desde DB manualmente

    @ElementCollection
    private List<String> imagenes; // ruta en disco o nombre de archivo

    private String menuPdf; // ruta en disco o nombre del archivo pdf

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horario> horarios;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

}
