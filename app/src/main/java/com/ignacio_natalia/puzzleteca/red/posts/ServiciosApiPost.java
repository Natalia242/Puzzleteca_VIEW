package com.ignacio_natalia.puzzleteca.red.posts;

import com.ignacio_natalia.puzzleteca.modelos.post.Post;
import com.ignacio_natalia.puzzleteca.modelos.paginacion.PaginacionPost;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiciosApiPost {
    @Multipart
    @POST("posts/crearPost")
    Call<Post> crearPost(
            @Header("Authorization") String token,
            @Part("idUsuario") RequestBody idUsuario,
            @Part("contenido") RequestBody contenido,
            @Part MultipartBody.Part imagen   // puede ser null
    );

    /** Overload sin imagen (solo texto) */
    @Multipart
    @POST("posts/crearPost")
    Call<Post> crearPostSoloTexto(
            @Header("Authorization") String token,
            @Part("idUsuario") RequestBody idUsuario,
            @Part("contenido") RequestBody contenido
    );

    @POST("posts/{idPost}/like")
    Call<Map<String, Boolean>> toggleLike(
            @Header("Authorization") String token,
            @Path("idPost") Integer idPost,
            @Query("liked") boolean liked
    );

    /**
     * Feed paginado — devuelve un objeto Page de Spring.
     * Usamos Map para extraer el campo "content" sin definir un wrapper extra.
     */
    @GET("posts/feed")
    Call<retrofit2.converter.gson.GsonConverterFactory> obtenerFeed(
            @Header("Authorization") String token,
            @Query("pagina") int pagina,
            @Query("tamanno") int tamanno
    );

    /**
     * Feed paginado — versión simple que devuelve la lista "content" directamente.
     * Si el backend devuelve Page<Post>, Gson lo deserializará como Map; usamos
     * un wrapper propio {@link PaginacionPost}.
     */
    @GET("posts/feed")
    Call<PaginacionPost> getFeed(
            @Header("Authorization") String token,
            @Query("pagina") int pagina,
            @Query("tamanno") int tamanno
    );

    @GET("posts/{idPost}")
    Call<Post> obtenerPost(@Header("Authorization") String token,
                           @Path("idPost") Integer idPost);

    /** Elimina un post */
    @DELETE("posts/eliminar/{idPost}")
    Call<Map<String, String>> eliminarPost(
            @Header("Authorization") String token,
            @Path("idPost") Integer idPost,
            @Query("idUsuario") Integer idUsuario
    );
}