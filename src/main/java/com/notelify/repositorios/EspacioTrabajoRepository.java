package com.notelify.repositorios;

import com.notelify.entidades.EspacioTrabajo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EspacioTrabajoRepository extends JpaRepository<EspacioTrabajo, String>{
    
    @Query("SELECT e FROM EspacioTrabajo e WHERE e.activo = true")
    public List<EspacioTrabajo> buscarActivos();
    
}
