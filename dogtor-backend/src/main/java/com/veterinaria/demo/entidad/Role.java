package com.veterinaria.demo.entidad;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="roles")
@Data
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;

    private String name;

    public Role(String name){
        this.name = name;
    }

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserEntity> usuarios;
}