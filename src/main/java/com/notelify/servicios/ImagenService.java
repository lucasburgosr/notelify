package com.notelify.servicios;

import com.notelify.entidades.Imagen;
import com.notelify.exceptions.ElementoNoEncontradoException;
import com.notelify.exceptions.ErrorInputException;
import com.notelify.repositorios.ImagenRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagenService {

    @Autowired
    private ImagenRepository imagenRepository;

    /**
     *
     * Crea y persiste un archivo del tipo MultipartFile en la base de datos,
     * estableciendo el tipo mime, el nombre y el contenido del archivo a un
     * objeto del tipo Imagen. Dentro se verifica que el archivo no venga nulo o
     * esté vacío; esto es para evitar persistir un archivo sin contenido en la
     * DDBB. Lanza a la consola un IOException si se ha producido un error en la
     * entrada/salida.
     *
     * @param archivo
     * @return un objeto de tipo Imagen si y solo si el archivo pasa las
     * validaciones, de otro modo retorna null.
     */
    @Transactional(rollbackFor = Exception.class)
    public Imagen guardar(MultipartFile archivo) {
        if (archivo != null && !archivo.isEmpty()) {
            try {
                Imagen imagen = new Imagen();

                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());

                return imagenRepository.save(imagen);
            } catch (IOException ex) {
                Logger.getLogger(ImagenService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     *
     * Busca un objeto Imagen en la DDBB, toma el MultipartFile y le establece
     * los nuevos valores mime, nombre y contenido al objeto Imagen encontrado,
     * lo persiste y luego lo retorna. Si el MultipartFile viene nulo está
     * vacío, retorna null.
     *
     * @param id del objeto Imagen a buscar
     * @param archivo nuevo para establecer los nuevos valores
     * @return objeto Imagen sí y solo sí el MultipartFile no viene nulo o
     * vacío, de lo contrario retorna null.
     * @throws ErrorInputException si los argumentos no son válidos.
     * @throws ElementoNoEncontradoException si el objeto Imagen no se encontró.
     */
    @Transactional(rollbackFor = Exception.class)
    public Imagen actualizar(String id, MultipartFile archivo) throws ErrorInputException, ElementoNoEncontradoException {
        if (archivo != null && !archivo.isEmpty()) {
            try {
                Imagen imagen = buscarPorId(id);

                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());

                return imagenRepository.save(imagen);
            } catch (IOException ex) {
                Logger.getLogger(ImagenService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     *
     * Busca un objeto Imagen en la DDBB y lo retorna. Dentro se valida si el
     * argumento pasado no venga vacío o nulo.
     *
     * @param id del objeto Imagen a buscar
     * @return un objeto del tipo Imagen.
     * @throws ErrorInputException si los argumentos no son válidos.
     * @throws ElementoNoEncontradoException si el objeto Imagen no se encontró.
     */
    @Transactional(readOnly = true)
    public Imagen buscarPorId(String id) throws ElementoNoEncontradoException, ErrorInputException {
        Optional<Imagen> respuesta = imagenRepository.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        } else {
            return new Imagen();
        }
    }

    /**
     *
     * Busca en la base de datos todos los objetos del tipo Imagen y retorna una
     * lista de ellos.
     *
     * @return una List<Imagen> de todos los objetos encontrados en la DDBB.
     */
    @Transactional(readOnly = true)
    public List<Imagen> listarTodos() {
        return imagenRepository.findAll();
    }

}
