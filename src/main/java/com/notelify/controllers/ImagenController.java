package com.notelify.controllers;

import com.notelify.entidades.Usuario;
import com.notelify.exceptions.ElementoNoEncontradoException;
import com.notelify.exceptions.ErrorInputException;
import com.notelify.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN') || hasAnyRole('ROLE_USER')")
@RequestMapping("/imagen")
public class ImagenController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<byte[]> fotoUsuario(ModelMap modelo, @PathVariable String idUsuario) {
        Usuario usuario = new Usuario();

        try {
            usuario = usuarioService.buscarPorId(idUsuario);

            if (usuario.getFotoPerfil() == null) {
                throw new ElementoNoEncontradoException("El usuario no tiene una foto asignada.");
            }

            byte[] fotoPerfil = usuario.getFotoPerfil().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(fotoPerfil, headers, HttpStatus.OK);
        } catch (ElementoNoEncontradoException | ErrorInputException e) {
            modelo.put("error", e.getMessage());
            System.out.println(e.getMessage());

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
