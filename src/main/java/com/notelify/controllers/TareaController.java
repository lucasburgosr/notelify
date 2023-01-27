package com.notelify.controllers;

import com.notelify.entidades.EspacioTrabajo;
import com.notelify.entidades.Tarea;
import com.notelify.entidades.Usuario;
import com.notelify.enums.Estado;
import com.notelify.exceptions.ElementoNoEncontradoException;
import com.notelify.exceptions.ErrorInputException;
import com.notelify.servicios.EspacioTrabajoService;
import com.notelify.servicios.TareaService;
import com.notelify.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN') || hasAnyRole('ROLE_USER')")
@RequestMapping("/tarea")
public class TareaController {

    @Autowired
    private TareaService tareaService;

    @Autowired
    private EspacioTrabajoService espacioTrabajoService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/crear")
    public String crear(ModelMap modelo, @RequestParam String idEspacio, @RequestParam String titulo, @RequestParam(required = false) String descripcion, @RequestParam String idUsuario, RedirectAttributes attr) {
        EspacioTrabajo espacioTrabajo = new EspacioTrabajo();
        Tarea tarea = new Tarea();
        Usuario usuario = new Usuario();

        try {
            espacioTrabajo = espacioTrabajoService.buscarPorId(idEspacio);
            tarea = tareaService.crearYPersistir(titulo, descripcion, idUsuario);
            usuario = usuarioService.buscarPorId(idUsuario);

            espacioTrabajoService.agregarTarea(espacioTrabajo.getId(), tarea.getId());

            modelo.put("espacioTrabajo", espacioTrabajo);
        } catch (ErrorInputException | ElementoNoEncontradoException e) {
            attr.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/espacio-trabajo/mi-espacio/" + espacioTrabajo.getId() + '/' + usuario.getId();
    }

    @PostMapping("/estado")
    public String cambiarEstado(ModelMap modelo, RedirectAttributes attr, @RequestParam String idUsuario, @RequestParam String idEspacio, @RequestParam String id, Estado estado) {
        EspacioTrabajo espacioTrabajo = new EspacioTrabajo();
        Usuario usuario = new Usuario();

        try {
            usuario = usuarioService.buscarPorId(idUsuario);
            espacioTrabajo = espacioTrabajoService.buscarPorId(idEspacio);

            tareaService.moverDeEstado(id, estado);

            modelo.put("espacio", espacioTrabajo);
        } catch (ElementoNoEncontradoException | ErrorInputException e) {
            attr.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/espacio-trabajo/mi-espacio/" + espacioTrabajo.getId() + '/' + usuario.getId();
    }

    @PostMapping("/editar")
    public String editar(@RequestParam String idUsuario, @RequestParam String idEspacio, @RequestParam String id, @RequestParam String titulo, @RequestParam(required = false) String descripcion, RedirectAttributes attr) {
        EspacioTrabajo espacioTrabajo = new EspacioTrabajo();
        Usuario usuario = new Usuario();

        try {
            usuario = usuarioService.buscarPorId(idUsuario);
            espacioTrabajo = espacioTrabajoService.buscarPorId(idEspacio);

            tareaService.modificar(id, titulo, descripcion);
        } catch (ElementoNoEncontradoException | ErrorInputException e) {
            attr.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/espacio-trabajo/mi-espacio/" + espacioTrabajo.getId() + '/' + usuario.getId();
    }

    @PostMapping("/eliminar")
    public String deshabilitarTarea(RedirectAttributes attr, @RequestParam String id, @RequestParam String idUsuario, @RequestParam String idEspacio) {
        EspacioTrabajo espacioTrabajo = new EspacioTrabajo();
        Usuario usuario = new Usuario();
        
        try {
            espacioTrabajo = espacioTrabajoService.buscarPorId(idEspacio);
            usuario = usuarioService.buscarPorId(idUsuario);
            
            tareaService.deshabilitar(id);
        } catch (ElementoNoEncontradoException | ErrorInputException e) {
            attr.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/espacio-trabajo/mi-espacio/" + espacioTrabajo.getId() + '/' + usuario.getId();
    }

}
