package com.notelify.servicios;

import com.notelify.entidades.EspacioTrabajo;
import com.notelify.entidades.Imagen;
import com.notelify.entidades.Tarea;
import com.notelify.entidades.Usuario;
import com.notelify.exceptions.ElementoNoEncontradoException;
import com.notelify.exceptions.ErrorInputException;
import com.notelify.repositorios.EspacioTrabajoRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EspacioTrabajoService {

    @Autowired
    private EspacioTrabajoRepository espacioTrabajoRepository;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ImagenService imagenService;

    /**
     *
     * Método que crea y persiste un objeto EspacioTarea en la DDBB. A su vez
     * verifica que el idUsuario y el Nombre no venga como objeto nulo o vacío.
     * Dentro se utiliza el método validar(). Se hace uso del método
     *
     *
     * @param nombre
     * @param idUsuario
     * @param archivo
     * @return el objeto persistido.
     * @throws ErrorInputException cuando los argumentos son nulos o vienen
     * vacíos.
     * @throws com.notelify.exceptions.ElementoNoEncontradoException
     * @see ImagenService
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EspacioTrabajo crear(MultipartFile archivo, String nombre, String idUsuario) throws ErrorInputException, ElementoNoEncontradoException {
        validar(nombre, idUsuario);

        EspacioTrabajo espacioTrabajo = new EspacioTrabajo();
        Imagen fondo = imagenService.guardar(archivo);

        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        espacioTrabajo.getListaUsuarios().add(usuario);

        espacioTrabajo.setNombre(nombre);
        espacioTrabajo.setFondo(fondo);
        espacioTrabajo.setFechaCreacion(new Date());
        espacioTrabajo.setActivo(true);

        return espacioTrabajoRepository.save(espacioTrabajo);
    }

    /**
     * Agrega Tareas al EspacioTarea utilizando el metodo buscarPorId(). Las
     * Tareas agregan a la lista de Tareas
     *
     * @param id
     * @param idTarea
     * @return el objeto Tarea queda agregado y persistido.
     * @throws ErrorInputException cuando los argumentos vienen nulos o vacíos.
     * @throws ElementoNoEncontradoException cuando el elemento solicitado no se
     * encontró.
     */
    @Transactional(rollbackFor = {Exception.class})
    public EspacioTrabajo agregarTarea(String id, String idTarea) throws ElementoNoEncontradoException, ErrorInputException {
        EspacioTrabajo espacioTrabajo = buscarPorId(id);
        Tarea tarea = tareaService.buscarPorId(idTarea);

        espacioTrabajo.getListaTareas().add(tarea);
        return espacioTrabajoRepository.save(espacioTrabajo);
    }

    /**
     * Agrega Usuario al EspacioTarea utilizando el metodo buscarPorId().
     * Primero buscar el espacio Tarea por id y luego busca el Usuario mediante
     * el metodo buscarPorId. El Usuario encontrado se agrega a la lista de
     * Usuarios creada en los atributos de EspacioTrabajo.
     *
     * @param id
     * @param idUsuario
     * @return el objeto EspacioTrabajo queda agregado y persistido.
     * @throws ErrorInputException cuando los argumentos vienen nulos o vacíos.
     * @throws ElementoNoEncontradoException cuando el elemento solicitado no se
     * encontró.
     */
    @Transactional(rollbackFor = {Exception.class})
    public EspacioTrabajo agregarUsuario(String id, String idUsuario) throws ElementoNoEncontradoException, ErrorInputException {
        EspacioTrabajo espacioTrabajo = buscarPorId(id);
        Usuario usuario = usuarioService.buscarPorId(idUsuario);

        espacioTrabajo.getListaUsuarios().add(usuario);
        return espacioTrabajoRepository.save(espacioTrabajo);
    }

    /**
     * Modifica al objeto EspacioTarea pidiendo los nuevos datos. Dentro se
     * utiliza el método validar() y el método actualizar() de ImagenService.
     *
     * @see ImagenService
     * @param archivo
     * @param id
     * @param nombre
     * @return el objeto Usuario modificado y persistido.
     * @throws ErrorInputException cuando los argumentos vienen nulos o vacíos.
     * @throws ElementoNoEncontradoException cuando el elemento solicitado no se
     * encontró.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EspacioTrabajo modificar(String id, MultipartFile archivo, String nombre) throws ElementoNoEncontradoException, ErrorInputException {
        EspacioTrabajo espacioTrabajo = buscarPorId(id);
        espacioTrabajo.setNombre(nombre);

//        String idImagen = null;
//        if (espacioTrabajo.getFondo().getId() != null) {
//            idImagen = espacioTrabajo.getFondo().getId();
//        }
//        Imagen imagen = imagenService.actualizar(idImagen, archivo);
//
//        espacioTrabajo.setFondo(imagen);

        return espacioTrabajoRepository.save(espacioTrabajo);
    }

    public List<EspacioTrabajo> espaciosDelUsuario(String idUsuario) throws ErrorInputException, ElementoNoEncontradoException {
        List<EspacioTrabajo> espaciosDelUsuario = new ArrayList<>();

        Usuario usuarioEncontrado = null;
        usuarioEncontrado = usuarioService.buscarPorId(idUsuario);

        for (EspacioTrabajo espacio : listarTodos()) {
            for (Usuario usuario : espacio.getListaUsuarios()) {
                if (usuario.equals(usuarioEncontrado)) {
                    espaciosDelUsuario.add(espacio);
                }
            }
        }

        return espaciosDelUsuario;
    }

    /**
     *
     * Deshabilita el EspacioTrabajo estableciendo el atributo boolean a true.
     *
     * @param id para buscar el EspacioTrabajo en la DDBB.
     * @return el EspacioTrabajo modificado y persistido.
     * @throws ElementoNoEncontradoException cuando la petición no se encontró.
     * @throws ErrorInputException cuando el argumento viene nulo o vacío.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EspacioTrabajo activo(String id) throws ElementoNoEncontradoException, ErrorInputException {
        EspacioTrabajo espacioTrabajo = buscarPorId(id);

        espacioTrabajo.setActivo(true);
        return espacioTrabajoRepository.save(espacioTrabajo);
    }

    /**
     *
     * Deshabilita el EspacioTrabajo estableciendo el atributo boolean a falso y
     * dejando registro de la fecha de baja a la fecha en la que se ejecutó el
     * método. Este método no elimina al EspacioTrabajo.
     *
     * @param id para buscar el EspacioTrabajo en la DDBB.
     * @return el EspacioTrabajo modificado y persistido.
     * @throws ElementoNoEncontradoException cuando la petición no se encontró.
     * @throws ErrorInputException cuando el argumento viene nulo o vacío.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EspacioTrabajo deshabilitar(String id) throws ErrorInputException, ElementoNoEncontradoException {
        EspacioTrabajo espacioTrabajo = buscarPorId(id);

        espacioTrabajo.setActivo(false);
        return espacioTrabajoRepository.save(espacioTrabajo);
    }

    /**
     *
     * Busca al objeto solicitado haciendo uso del Optional<T>
     *
     * @param id como identificador único del objeto.
     * @return un objeto del tipo EspacioTrabajo solicitado.
     * @throws ErrorInputException cuando el dato entrante no es el correcto.
     * @throws ElementoNoEncontradoException cuando el objeto solicitado no se
     * encontró.
     */
    @Transactional(readOnly = true)
    public EspacioTrabajo buscarPorId(String id) throws ElementoNoEncontradoException, ErrorInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new ErrorInputException("El id del espacio de trabajo no es correcto.");
        }
        Optional<EspacioTrabajo> respuesta = espacioTrabajoRepository.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new ElementoNoEncontradoException("El espacio de trabajo solicitado no existe.");
        }
    }

    /**
     *
     * Lista todos los objetos EspacioTrabajo persistidos en la base de datos.
     * En caso de no encontrar ninguno, el return es null.
     *
     * @return lista con todos los objetos del tipo Usuario que estén en la
     * DDBB.
     */
    @Transactional(readOnly = true)
    public List<EspacioTrabajo> listarTodos() {
        return espacioTrabajoRepository.findAll();
    }

    /**
     *
     * Lista todos los objetos EspacioTrabajo persistidos en la base de datos.
     * En caso de no encontrar ninguno, el return es null.
     *
     * @return lista con todos los objetos del tipo EspacioTrabajo que estén
     * activos (boolean = true) en la DDBB.
     */
    @Transactional(readOnly = true)
    public List<EspacioTrabajo> listarActivos() {
        return espacioTrabajoRepository.buscarActivos();
    }

    /**
     *
     * Verifica que los argumentos no lleguen nulos o vacíos. En caso de ser
     * cierto alguno de estos, arroja la respectiva excepción.
     *
     * @param nombre
     * @param idUsuario
     * @throws ErrorInputException cuando los argumentos son nulos o vienen
     * vacíos.
     */
    public void validar(String nombre, String idUsuario) throws ErrorInputException {
        if (nombre == null || nombre.isEmpty() || nombre.contains("  ")) {
            throw new ErrorInputException("Debe proporcionar un nombre a su espacio de trabajo.");
        }
        if (idUsuario == null || idUsuario.isEmpty() || idUsuario.contains("  ")) {
            throw new ErrorInputException("EL id no puede estar nulo o vacío.");
        }
    }
}
