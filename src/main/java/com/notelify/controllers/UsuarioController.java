package com.notelify.controllers;

import com.notelify.entidades.Usuario;
import com.notelify.exceptions.ElementoNoEncontradoException;
import com.notelify.exceptions.ErrorInputException;
import com.notelify.servicios.UsuarioService;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String form(ModelMap modelo) {
        return "registro.html";
    }

    /**
     *
     * Controlador para registrar el usuario. Recibe los atributos listados
     * abajo y llama al metodo crear y persistir del usuarioService.
     *
     * @param att
     * @param username
     * @param correo
     * @param clave
     *
     * @return Template: index.html
     *
     */
    @PostMapping("/registro")
    public String registrar(RedirectAttributes att, @RequestParam String username, @RequestParam String correo, @RequestParam String clave) {
        try {
            usuarioService.creacionRapida(correo, clave, username);

            att.addFlashAttribute("exito", "Usuario registrado correctamente.");
        } catch (ErrorInputException ex) {
            att.addFlashAttribute("error", ex.getMessage());
            att.addFlashAttribute("username", username);
            att.addFlashAttribute("correo", correo);
        }

        return "redirect:/usuario/registro";
    }

    /**
     *
     * Controlador para visualizar el usuario seleccionado. Recibe un único
     * parámetro desde el front y es variable : Id
     *
     * @param modelo
     * @param id
     * @PathVariable id
     *
     * @return el objeto buscado.
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN') || hasAnyRole('ROLE_USER')")
    @GetMapping("/editar/{id}")
    public String editarGet(ModelMap modelo, @PathVariable String id) {
        Usuario usuario = new Usuario();

        try {
            if (id != null && !id.trim().isEmpty()) {
                usuario = usuarioService.buscarPorId(id);
            }

            modelo.put("perfil", usuario);
        } catch (ElementoNoEncontradoException | ErrorInputException ex) {
            modelo.put("error", ex.getMessage());
        }

        return "modificarDatos.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN') || hasAnyRole('ROLE_USER')")
    @PostMapping("/editar")
    public String editarPost(HttpSession session, RedirectAttributes attr, MultipartFile archivo, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaNacimiento, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String username, @RequestParam String correo, @RequestParam String clave1, @RequestParam String clave2) {
        Usuario usuario = new Usuario();

        try {
            usuario = usuarioService.modificar(archivo, id, nombre, apellido, username, correo, clave1, clave2, fechaNacimiento);
            
            session.setAttribute("usuariosession", usuario);
            
            attr.addFlashAttribute("exito", "¡Perfecto!");
            attr.addFlashAttribute("descripcion", "El perfil fue modificado exitosamente");
        } catch (ErrorInputException | ElementoNoEncontradoException ex) {
            attr.addFlashAttribute("error", ex.getMessage());
            attr.addFlashAttribute("nombre", nombre);
            attr.addFlashAttribute("apellido", apellido);
            attr.addFlashAttribute("username", username);
            attr.addFlashAttribute("correo", correo);
        }

        return "redirect:/usuario/editar/" + usuario.getId();
    }

    @PostMapping("/recuperar")
    public String recuperarClave(ModelMap modelo, @RequestParam String correo, @RequestParam String nuevaClave, @RequestParam String repeticionNuevaClave) {
        try {
            usuarioService.recuperarClave(correo, nuevaClave, repeticionNuevaClave);
        } catch (ErrorInputException | ElementoNoEncontradoException e) {
            modelo.put("error", e.getMessage());
        }
        return "login.html";
    }

}
