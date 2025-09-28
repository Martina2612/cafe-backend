package com.cafe.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comentarios")
public class Comentario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // opcional
    private String texto;
    private LocalDateTime fecha = LocalDateTime.now();

    private Boolean activo = false; // aprobado por admin

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;
}
