package com.ignacio_natalia.puzzleteca.repositorios;

import com.ignacio_natalia.puzzleteca.modelos.post.Post;
import com.ignacio_natalia.puzzleteca.modelos.paginacion.PaginacionPost;
import com.ignacio_natalia.puzzleteca.red.posts.PostApi;
import com.ignacio_natalia.puzzleteca.red.posts.ServiciosApiPost;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PostRepositorio {

    private final ServiciosApiPost api;

    public PostRepositorio() {
        api = PostApi.getCliente().create(ServiciosApiPost.class);
    }

    // -------------------------------------------------------------------------
    // Crear post — con imagen
    // -------------------------------------------------------------------------
    public void crearPost(String token, Integer idUsuario, String contenido,
                          File imagenFile, String mimeType, Callback<Post> callback) {

        RequestBody rbId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idUsuario));

        RequestBody rbContenido = RequestBody.create(MediaType.parse("text/plain"), contenido != null ? contenido : "");

        MultipartBody.Part imagenPart = null;
        if (imagenFile != null && imagenFile.exists()) {
            RequestBody rbFile = RequestBody.create(MediaType.parse(mimeType), imagenFile);
            imagenPart = MultipartBody.Part.createFormData("imagen", imagenFile.getName(), rbFile);
        }

        Call<Post> call;
        if (imagenPart != null) {
            call = api.crearPost("Bearer " + token, rbId, rbContenido, imagenPart);
        } else {
            call = api.crearPostSoloTexto("Bearer " + token, rbId, rbContenido);
        }

        call.enqueue(callback);
    }

    // -------------------------------------------------------------------------
    // Obtener feed paginado
    // -------------------------------------------------------------------------
    public void obtenerFeed(String token, int pagina, int tamanno,
                            Callback<PaginacionPost> callback) {
        api.getFeed("Bearer " + token, pagina, tamanno).enqueue(callback);
    }

    // -------------------------------------------------------------------------
    // Eliminar post
    // -------------------------------------------------------------------------
    public void eliminarPost(String token, Integer idPost, Integer idUsuario,
                             Callback<Map<String, String>> callback) {
        api.eliminarPost("Bearer " + token, idPost, idUsuario).enqueue(callback);
    }

    public void toggleLike(String token, Integer idPost, boolean liked,
                           Callback<Map<String, Boolean>> callback) {
        api.toggleLike("Bearer " + token, idPost, liked).enqueue(callback);
    }

    public void obtenerPost(String token, Integer idPost, Callback<Post> callback) {

        api.obtenerPost("Bearer " + token, idPost).enqueue(callback);

    }
}