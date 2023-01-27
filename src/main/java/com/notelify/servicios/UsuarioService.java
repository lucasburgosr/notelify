package com.notelify.servicios;

import com.notelify.entidades.Imagen;
import com.notelify.entidades.Usuario;
import com.notelify.enums.Rol;
import com.notelify.exceptions.ElementoNoEncontradoException;
import com.notelify.exceptions.ErrorInputException;
import com.notelify.repositorios.UsuarioRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ImagenService imagenService;

    /**
     *
     * Método que crea y persiste un objeto Usuario en la DDBB. A su vez
     * verifica que el Rol no venga como objeto nulo. Dentro se utiliza el
     * método validar(). Se hace uso del método
     * {@link ImagenService#guardar(org.springframework.web.multipart.MultipartFile)}.
     *
     * @param correo
     * @param clave
     * @param username
     * @return el objeto persistido.
     * @throws ErrorInputException cuando los argumentos son nulos o vienen
     * vacíos.
     * @see ImagenService
     */
    @Transactional(rollbackFor = {Exception.class})
    public Usuario creacionRapida(String correo, String clave, String username) throws ErrorInputException {
        validacionRapida(correo, clave, username);

        Usuario usuario = new Usuario();

        String claveEncriptada = encriptacion(clave);

        usuario.setClave(claveEncriptada);
        usuario.setCorreo(correo);
        usuario.setUsername(username);
        usuario.setRol(Rol.USER);
        usuario.setFechaAlta(new Date());
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    /**
     * Modifica al objeto Usuario pidiendo los nuevos datos. Dentro se utiliza
     * el método validar() y el método actualizar() de ImagenService.
     *
     * @param nombre
     * @param apellido
     * @param username
     * @see ImagenService
     * @param archivo
     * @param id
     * @param correo
     * @param clave1
     * @param clave2
     * @param fechaNacimiento
     * @return el objeto Usuario modificado y persistido.
     * @throws ErrorInputException cuando los argumentos vienen nulos o vacíos.
     * @throws ElementoNoEncontradoException cuando el elemento solicitado no se
     * encontró.
     */
    @Transactional(rollbackFor = Exception.class)
    public Usuario modificar(MultipartFile archivo, String id, String nombre, String apellido, String username, String correo, String clave1, String clave2, Date fechaNacimiento) throws ErrorInputException, ElementoNoEncontradoException {
        validacionCompleta(nombre, apellido, username, clave1, clave2, correo);

        Imagen imagen = new Imagen();

        Usuario usuario = buscarPorId(id);

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setUsername(username);
        usuario.setCorreo(correo);
        usuario.setFechaNacimiento(fechaNacimiento);

        String claveEncriptada = encriptacion(clave1);
        usuario.setClave(claveEncriptada);

        if (usuario.getFotoPerfil() == null) {
            imagen = imagenService.guardar(archivo);
        } else {
            imagen = imagenService.actualizar(usuario.getFotoPerfil().getId(), archivo);
        }

        usuario.setFotoPerfil(imagen);

        return usuarioRepository.save(usuario);
    }

    /**
     *
     * Deshabilita el Usuario estableciendo el atributo boolean a falso y
     * dejando registro de la fecha de baja a la fecha en la que se ejecutó el
     * método. Este método no elimina al Usuario.
     *
     * @param id para buscar el Usuario en la DDBB.
     * @return el Usuario modificado y persistido.
     * @throws ElementoNoEncontradoException cuando la petición no se encontró.
     * @throws ErrorInputException cuando el argumento viene nulo o vacío.
     */
    public Usuario deshabilitar(String id) throws ElementoNoEncontradoException, ErrorInputException {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuario.setFechaBaja(new Date());

        return usuarioRepository.save(usuario);
    }

    public Usuario recuperarClave(String correo, String clave1, String clave2) throws ErrorInputException, ElementoNoEncontradoException {
        if (!clave1.equals(clave2)) {
            throw new ErrorInputException("Las contraseñas deben ser iguales.");
        }

        Usuario usuario = buscarPorCorreo(correo);
        String crypt = encriptacion(clave1);
        usuario.setClave(crypt);

        return usuarioRepository.save(usuario);
    }

    /**
     *
     * Busca al objeto solicitado haciendo uso del Optional<T>
     *
     * @param id como identificador único del objeto.
     * @return un objeto del tipo Usuario solicitado.
     * @throws ErrorInputException cuando el dato entrante no es el correcto.
     * @throws ElementoNoEncontradoException cuando el objeto solicitado no se
     * encontró.
     */
    public Usuario buscarPorId(String id) throws ErrorInputException, ElementoNoEncontradoException {
        if (id == null || id.trim().isEmpty()) {
            throw new ErrorInputException("El id del usuario no es correcto.");
        }
        Optional<Usuario> respuesta = usuarioRepository.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new ElementoNoEncontradoException("El se encontró al usuario solicitado.");
        }
    }

    /**
     *
     * Busca un Usuario mediante el correo asociado.
     *
     * @param correo asociado al objeto.
     * @return un objeto del tipo Usuario.
     * @throws ErrorInputException cuando el parámetro como dato entrante no es
     * el correcto.
     * @throws com.notelify.exceptions.ElementoNoEncontradoException
     */
    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) throws ErrorInputException, ElementoNoEncontradoException {
        if (correo == null || correo.trim().isEmpty()) {
            throw new ErrorInputException("El correo no puede ser nulo.");
        }

        Usuario usuario = usuarioRepository.buscarPorCorreo(correo);
        if (usuario == null) {
            throw new ElementoNoEncontradoException("El usuario solicitado con dicho correo no existe.");
        }

        return usuario;
    }

    /**
     *
     * Lista todos los objetos Usuario persistidos en la base de datos. En caso
     * de no encontrar ninguno, el return es null.
     *
     * @return lista con todos los objetos del tipo Usuario que estén en la
     * DDBB.
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.buscarPorCorreo(correo);

        if (u == null) {
            return null;
        }

        List<GrantedAuthority> permisos = new ArrayList<>();

        GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + u.getRol().toString());
        permisos.add(p1);

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpSession session = attr.getRequest().getSession(true);
        session.setAttribute("usuariosession", u);

        return new User(u.getCorreo(), u.getClave(), permisos);
    }

    /**
     *
     * Encripta cualquier tipo de objeto String que se le pase como parámetro.
     * Usado en este caso para encriptar la contraseña del Usuario.
     *
     * @param clave
     * @return la clave encriptada bajo la funcion BCryptPasswordEncoder.
     */
    private String encriptacion(String clave) {
        String claveEncriptada = new BCryptPasswordEncoder().encode(clave);
        return claveEncriptada;
    }

    /**
     *
     * Verifica que los argumentos no lleguen nulos o vacíos. En caso de ser
     * cierto alguno de estos, arroja la respectiva excepción.
     *
     * @param correo
     * @param clave
     * @param nombreCompleto
     * @throws ErrorInputException cuando los argumentos son nulos o vienen
     * vacíos.
     */
    private void validacionRapida(String correo, String clave, String username) throws ErrorInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new ErrorInputException("El nombre de usuario no puede ser nulo.");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new ErrorInputException("El correo no puede ser nulo.");
        }
        if (clave == null || clave.trim().isEmpty() || clave.length() < 6) {
            throw new ErrorInputException("La contraseña no puede ser nula y debe tener al menos 6 caracteres.");
        }
    }

    private void validacionCompleta(String nombre, String apellido, String username, String clave1, String clave2, String correo) throws ErrorInputException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorInputException("El nombre no puede ser nulo.");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new ErrorInputException("El apellido no puede ser nulo.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new ErrorInputException("El nombre de usuario no puede ser nulo.");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new ErrorInputException("El correo no puede ser nulo.");
        }
        if (clave1 == null || clave1.trim().isEmpty() || clave1.length() < 6) {
            throw new ErrorInputException("La contraseña no puede ser nula y debe tener al menos 6 caracteres.");
        }
        if (!clave2.equals(clave1)) {
            throw new ErrorInputException("Las claves deben ser iguales.");
        }
    }

}
