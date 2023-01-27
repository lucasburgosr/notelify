package com.notelify.repositorios;

import java.util.List;

import com.notelify.entidades.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    //Buscar por Email
    @Query("SELECT u FROM Usuario u WHERE u.correo LIKE :correo")
    public Usuario buscarPorCorreo(@Param("correo") String correo);

    // Buscar por Roles
    @Query("SELECT u FROM Usuario u WHERE u.rol like :rol")
    public List<Usuario> buscarRoles(@Param("rol") String rol);

    // BUSCAR por ROL
    @Query("SELECT u FROM Usuario u WHERE u.rol like :rol")
    public Usuario buscarPorRol(@Param("rol") String rol);

}
