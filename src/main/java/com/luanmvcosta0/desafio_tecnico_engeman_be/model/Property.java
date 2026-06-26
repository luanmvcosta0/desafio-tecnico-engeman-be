package com.luanmvcosta0.desafio_tecnico_engeman_be.model;

import com.luanmvcosta0.desafio_tecnico_engeman_be.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "PROPERTY_TABLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "room")
    private Integer rooms;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "type")
    private Type type;

}
