package com.cafe.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "horarios")
public class Horario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dia; // "Lunes", "Martes", etc.

    private String apertura; // "08:00"
    private String cierre;   // "12:00"

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;
}
