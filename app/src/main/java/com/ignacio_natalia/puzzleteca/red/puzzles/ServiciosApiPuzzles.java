package com.ignacio_natalia.puzzleteca.red.puzzles;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ServiciosApiPuzzles {

    @GET("obtenerPuzzles")
    Call<List<Puzzle>> obtenerPuzzles(
            @Header("Authorization") String token,
            @Query("estado") String estado
    );

    /**
     * Obtiene solo los puzzles del usuario indicado.
     * Útil para el selector de puzzle al crear un post.
     */
    @GET("misPuzzles")
    Call<List<Puzzle>> misPuzzles(
            @Header("Authorization") String token,
            @Query("idUsuario") Integer idUsuario
    );

    /**
     * Crea un puzzle enviando los datos como multipart/form-data.
     * La imagen viaja como Part binario; el backend la procesa con ImagenService
     * y guarda solo la ruta relativa en BD (no base64).
     */
    @Multipart
    @POST("registrarPuzzle")
    Call<Void> crearPuzzle(
            @Header("Authorization") String token,
            @Part("titulo")       RequestBody titulo,
            @Part("autor")        RequestBody autor,
            @Part("tiempo")       RequestBody tiempo,
            @Part("piezas")       RequestBody piezas,
            @Part("dificultad")   RequestBody dificultad,
            @Part("descripcion")  RequestBody descripcion,
            @Part("color")        RequestBody color,
            @Part("estado")       RequestBody estado,
            @Part("idUsuario")    RequestBody idUsuario,
            @Part MultipartBody.Part imagen          // puede ser null
    );

    /** Sobrecarga sin imagen */
    @Multipart
    @POST("registrarPuzzle")
    Call<Void> crearPuzzleSinImagen(
            @Header("Authorization") String token,
            @Part("titulo")       RequestBody titulo,
            @Part("autor")        RequestBody autor,
            @Part("tiempo")       RequestBody tiempo,
            @Part("piezas")       RequestBody piezas,
            @Part("dificultad")   RequestBody dificultad,
            @Part("descripcion")  RequestBody descripcion,
            @Part("color")        RequestBody color,
            @Part("estado")       RequestBody estado,
            @Part("idUsuario")    RequestBody idUsuario
    );

    @PUT("actualizarEstado")
    Call<Void> cambiarEstadoPuzzle(
            @Query("id_usuario") Integer id_usuario,
            @Query("id_puzzle")  Integer id_puzzle,
            @Query("tipo")       String tipo
    );
}
