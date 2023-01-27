package com.notelify.servicios;

import com.notelify.entidades.Tarea;
import com.notelify.entidades.Usuario;
import com.notelify.enums.Estado;
import com.notelify.exceptions.ElementoNoEncontradoException;
import com.notelify.exceptions.ErrorInputException;
import com.notelify.repositorios.TareaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioService usuarioService;

    /**
     *
     * Método que crea y persiste un objeto Tarea en la DDBB. Dentro se utiliza
     * el método validar(). Se hace uso del método
     * {@link UsuarioService#buscarPorId(java.lang.String)}
     *
     * @param titulo
     * @param descripcion
     * @param idUsuario
     * @return el objeto persistido.
     * @throws ErrorInputException cuando los argumentos son nulos o vienen
     * vacíos.
     * @throws com.notelify.exceptions.ElementoNoEncontradoException
     * @see UsuarioService.
     */
    @Transactional(rollbackFor = Exception.class)
    public Tarea crearYPersistir(String titulo, String descripcion, String idUsuario) throws ErrorInputException, ElementoNoEncontradoException {
        validar(titulo, idUsuario);

        Tarea tarea = new Tarea();

        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        tarea.getListaUsuarios().add(usuario);

        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setEstado(Estado.TODO);
        tarea.setActivo(true);

        return tareaRepository.save(tarea);
    }

    /**
     * Modifica al objeto Tarea pidiendo los nuevos datos. Dentro se utiliza el
     * método validar().
     *
     * @param id
     * @param titulo
     * @param descripcion
     * @return el objeto Tarea modificado y persistido.
     * @throws ErrorInputException cuando los argumentos vienen nulos o vacíos.
     * @throws ElementoNoEncontradoException cuando el elemento solicitado no se
     * encontró.
     */
    @Transactional(rollbackFor = {Exception.class})
    public Tarea modificar(String id, String titulo, String descripcion) throws ErrorInputException, ElementoNoEncontradoException {
        validar(titulo, id);

        Tarea tarea = buscarPorId(id);
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);

        return tareaRepository.save(tarea);
    }

    /**
     * Modifica el atributo estado del objeto Tarea.
     *
     * @param id
     * @param estado
     * @return el objeto Tarea modificado y persistido.
     * @throws ErrorInputException cuando los argumentos vienen nulos o vacíos.
     * @throws ElementoNoEncontradoException cuando el elemento solicitado no se
     * encontró.
     */
    @Transactional(rollbackFor = {Exception.class})
    public Tarea moverDeEstado(String id, Estado estado) throws ElementoNoEncontradoException, ErrorInputException {
        if (estado == null) {
            throw new ErrorInputException("Debe declarar el estado al que moverá la tarea actual.");
        }
        Tarea tarea = buscarPorId(id);
        tarea.setEstado(estado);

        return tareaRepository.save(tarea);
    }

    /**
     *
     * Deshabilita la Tarea estableciendo el atributo boolean a falso. Este
     * método no elimina el objeto Tarea.
     *
     * @param id para buscar la Tarea en la DDBB.
     * @return la Tarea deshabilitada.
     * @throws ElementoNoEncontradoException cuando la petición no se encontró.
     * @throws ErrorInputException cuando el argumento viene nulo o vacío.
     */
    @Transactional(rollbackFor = {Exception.class})
    public Tarea deshabilitar(String id) throws ElementoNoEncontradoException, ErrorInputException {
        Tarea tarea = buscarPorId(id);
        tarea.setActivo(false);
        return tareaRepository.save(tarea);
    }

    /**
     *
     * Busca al objeto solicitado haciendo uso del Optional<T>
     *
     * @param id como identificador único del objeto.
     * @return un objeto del tipo Tarea solicitado.
     * @throws ErrorInputException cuando el dato entrante no es el correcto.
     * @throws ElementoNoEncontradoException cuando el objeto solicitado no se
     * encontró.
     */
    @Transactional(readOnly = true)
    public Tarea buscarPorId(String id) throws ElementoNoEncontradoException, ErrorInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new ErrorInputException("El id de la tarea no es el correcto.");
        }
        Optional<Tarea> respuesta = tareaRepository.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            throw new ElementoNoEncontradoException("No existe la tarea solicitada.");
        }
    }

    /**
     *
     * Método que retorna una List con todos los objetos Tarea de la DDBB.
     *
     * @return una lista de Tareas.
     */
    @Transactional(readOnly = true)
    public List<Tarea> listarTodas() {
        return tareaRepository.findAll();
    }

    /**
     *
     * Verifica que los argumentos no lleguen nulos o vacíos. En caso de ser
     * cierto alguno de estos, arroja la respectiva excepción.
     *
     * @param titulo
     * @param id
     * @throws ErrorInputException cuando los argumentos son nulos o vienen
     * vacíos.
     */
    public void validar(String titulo, String id) throws ErrorInputException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ErrorInputException("Debe proporcionar un título.");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new ErrorInputException("Debe proporcionar un id válido.");
        }
    }
}
