package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ignacio_natalia.puzzleteca.modelos.comentarios.Comentario;
import com.ignacio_natalia.puzzleteca.modelos.post.Post;
import com.ignacio_natalia.puzzleteca.modelos.paginacion.PaginacionPost;
import com.ignacio_natalia.puzzleteca.repositorios.ComentarioRepositorio;
import com.ignacio_natalia.puzzleteca.repositorios.PostRepositorio;
import com.ignacio_natalia.puzzleteca.modelos.paginacion.PaginacionComentarios;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForoViewModel extends AndroidViewModel {

    // =========================================================================
    // POSTS
    // =========================================================================

    private final MutableLiveData<List<Post>> posts =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> cargando =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> postCreado =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> hayMasPaginas =
            new MutableLiveData<>(true);

    // =========================================================================
    // COMENTARIOS
    // =========================================================================

    private final MutableLiveData<Map<Integer, List<Comentario>>> comentariosPorPost =
            new MutableLiveData<>(new HashMap<>());

    // =========================================================================
    // PAGINACIÓN
    // =========================================================================

    private int paginaActual = 0;
    private static final int TAMANNO_PAGINA = 20;

    // =========================================================================
    // REPOSITORIOS
    // =========================================================================

    private final PostRepositorio repositorio = new PostRepositorio();

    private final ComentarioRepositorio comentarioRepositorio =
            new ComentarioRepositorio();

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    public ForoViewModel(@NonNull Application application) {
        super(application);
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getPostCreado() {
        return postCreado;
    }

    public LiveData<Boolean> getHayMasPaginas() {
        return hayMasPaginas;
    }

    public LiveData<Map<Integer, List<Comentario>>> getComentariosPorPost() {
        return comentariosPorPost;
    }

    // =========================================================================
    // POSTS
    // =========================================================================
    public void cargarPosts(String token) {
        paginaActual = 0;
        posts.setValue(new ArrayList<>());
        hayMasPaginas.setValue(true);
        fetchPagina(token, 0);
    }
    public void cargarMasPosts(String token) {

        Boolean hayMas = hayMasPaginas.getValue();
        Boolean estaCargando = cargando.getValue();

        if (Boolean.FALSE.equals(hayMas)
                || Boolean.TRUE.equals(estaCargando)) {
            return;
        }

        fetchPagina(token, paginaActual);
    }

    private void fetchPagina(String token, int pagina) {

        cargando.setValue(true);

        repositorio.obtenerFeed(
                token,
                pagina,
                TAMANNO_PAGINA,
                new Callback<PaginacionPost>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<PaginacionPost> call,
                            @NonNull Response<PaginacionPost> response
                    ) {

                        cargando.setValue(false);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            PaginacionPost page = response.body();

                            List<Post> actuales = posts.getValue();

                            if (actuales == null)
                                actuales = new ArrayList<>();

                            List<Post> nuevos = page.getContent();

                            if (nuevos != null)
                                actuales.addAll(nuevos);

                            posts.setValue(actuales);

                            hayMasPaginas.setValue(!page.isLast());

                            paginaActual++;

                        } else if (response.code() == 404) {

                            hayMasPaginas.setValue(false);

                        } else {

                            error.setValue(
                                    "Error "
                                            + response.code()
                                            + " al cargar el foro"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<PaginacionPost> call,
                            @NonNull Throwable t
                    ) {

                        cargando.setValue(false);

                        error.setValue(
                                "Sin conexión: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    // =========================================================================
    // CREAR POST
    // =========================================================================

    public void crearPost(
            String token,
            Integer idUsuario,
            String contenido,
            File imagen,
            String mimeType
    ) {

        if ((contenido == null || contenido.isBlank())
                && imagen == null) {

            error.setValue("El post debe tener texto o imagen");
            return;
        }

        cargando.setValue(true);

        repositorio.crearPost(
                token,
                idUsuario,
                contenido,
                imagen,
                mimeType,
                new Callback<Post>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Post> call,
                            @NonNull Response<Post> response
                    ) {

                        cargando.setValue(false);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<Post> actuales = posts.getValue();

                            if (actuales == null)
                                actuales = new ArrayList<>();

                            actuales.add(0, response.body());

                            posts.setValue(actuales);

                            postCreado.setValue(true);

                        } else {

                            error.setValue(
                                    "Error "
                                            + response.code()
                                            + " al crear el post"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Post> call,
                            @NonNull Throwable t
                    ) {

                        cargando.setValue(false);

                        error.setValue(
                                "Sin conexión: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    // =========================================================================
    // ELIMINAR POST
    // =========================================================================

    public void eliminarPost(
            String token,
            Integer idPost,
            Integer idUsuario
    ) {

        repositorio.eliminarPost(
                token,
                idPost,
                idUsuario,
                new Callback<Map<String, String>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Map<String, String>> call,
                            @NonNull Response<Map<String, String>> response
                    ) {

                        if (response.isSuccessful()) {

                            List<Post> actuales = posts.getValue();

                            if (actuales != null) {

                                actuales.removeIf(
                                        p -> p.getId().equals(idPost)
                                );

                                posts.setValue(actuales);
                            }

                        } else {

                            error.setValue(
                                    "Error "
                                            + response.code()
                                            + " al eliminar el post"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Map<String, String>> call,
                            @NonNull Throwable t
                    ) {

                        error.setValue(
                                "Sin conexión: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    // =========================================================================
    // LIKES
    // =========================================================================

    public void toggleLike(
            String token,
            Integer idPost,
            boolean liked
    ) {

        repositorio.toggleLike(
                token,
                idPost,
                liked,
                new Callback<Map<String, Boolean>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Map<String, Boolean>> call,
                            @NonNull Response<Map<String, Boolean>> response
                    ) {

                        if (!response.isSuccessful()) {

                            error.setValue(
                                    "Error "
                                            + response.code()
                                            + " al dar like"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Map<String, Boolean>> call,
                            @NonNull Throwable t
                    ) {

                        error.setValue(
                                "Sin conexión: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    // =========================================================================
    // COMENTARIOS
    // =========================================================================
    public void cargarComentarios(
            String token,
            Integer idPost
    ) {

        comentarioRepositorio.obtenerComentariosPost(
                token,
                idPost,
                0,
                50,
                new Callback<PaginacionComentarios>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<PaginacionComentarios> call,
                            @NonNull Response<PaginacionComentarios> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Map<Integer, List<Comentario>> mapa =
                                    comentariosPorPost.getValue();

                            if (mapa == null)
                                mapa = new HashMap<>();

                            mapa.put(
                                    idPost,
                                    response.body().getContent()
                            );

                            comentariosPorPost.setValue(mapa);

                        } else {

                            error.setValue(
                                    "Error cargando comentarios"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<PaginacionComentarios> call,
                            @NonNull Throwable t
                    ) {

                        error.setValue(
                                "Sin conexión: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }
    public void crearComentario(
            String token,
            Comentario comentario
    ) {

        comentarioRepositorio.crearComentario(
                token,
                comentario,
                new Callback<Comentario>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Comentario> call,
                            @NonNull Response<Comentario> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            Comentario nuevo = response.body();

                            Map<Integer, List<Comentario>> mapa =
                                    comentariosPorPost.getValue();

                            if (mapa == null)
                                mapa = new HashMap<>();

                            List<Comentario> lista =
                                    mapa.get(nuevo.getId());

                            if (lista == null)
                                lista = new ArrayList<>();

                            lista.add(nuevo);

                            mapa.put(
                                    nuevo.getIdPost(),
                                    lista
                            );

                            comentariosPorPost.setValue(mapa);

                        } else {

                            error.setValue(
                                    "Error creando comentario"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Comentario> call,
                            @NonNull Throwable t
                    ) {

                        error.setValue(
                                "Sin conexión: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    public void eliminarComentario(
            String token,
            Integer idComentario,
            Integer idPost
    ) {

        comentarioRepositorio.eliminarComentario(
                token,
                idComentario,
                new Callback<Void>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Void> call,
                            @NonNull Response<Void> response
                    ) {

                        if (response.isSuccessful()) {

                            Map<Integer, List<Comentario>> mapa =
                                    comentariosPorPost.getValue();

                            if (mapa == null)
                                return;

                            List<Comentario> lista =
                                    mapa.get(idPost);

                            if (lista == null)
                                return;

                            lista.removeIf(
                                    c -> c.getId().equals(idComentario)
                            );

                            mapa.put(idPost, lista);

                            comentariosPorPost.setValue(mapa);

                        } else {

                            error.setValue(
                                    "Error eliminando comentario"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Void> call,
                            @NonNull Throwable t
                    ) {

                        error.setValue(
                                "Sin conexión: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }
}