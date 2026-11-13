package com.veterinaria.demo.entidad;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

/**
 * Refugio o fundación que publica mascotas en adopción.
 * Debe ser validado por un administrador de Dogtor antes de publicar (REQ-ADO-04).
 * Puede tener su propia cuenta (rol REFUGIO) para publicar y gestionar sus mascotas.
 */
@Entity
@Table(name = "refugio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refugio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRefugio;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String ciudad;

    private String telefono;

    private String correo;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 1000000)
    private String logo;

    // Redes sociales: se guarda el usuario (@huellitas) o el enlace completo, como lo escriba el refugio
    private String instagram;

    private String facebook;

    private String tiktok;

    private String sitioWeb;

    @Column(nullable = false)
    @Builder.Default
    private Boolean validado = false;

    @JsonIgnore
    @OneToMany(mappedBy = "refugio")
    private List<MascotaAdopcion> mascotas;

    /** Cuenta con rol REFUGIO con la que el refugio entra a su panel (null si lo registró el admin sin cuenta). */
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne
    @JoinColumn(name = "user_id_user", unique = true)
    private UserEntity user;

    /**
     * Usuario de la cuenta. No se guarda en esta tabla: solo viaja al crear la cuenta y se muestra
     * al administrador y al propio refugio (en el catálogo público siempre va vacío).
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nombreUsuario;

    /** Contraseña inicial que pone el administrador al crear la cuenta. Nunca se devuelve. */
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private String contrasena;

    /** Indica al panel del admin si el refugio puede iniciar sesión. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isConCuenta() {
        return user != null;
    }
}
