package com.ignacio_natalia.puzzleteca.modelos.paginacion;

import com.google.gson.annotations.SerializedName;
import com.ignacio_natalia.puzzleteca.modelos.post.Post;
import java.util.List;

/**
 * Wrapper que mapea la respuesta paginada de Spring Boot (Page<Post>).
 * Spring devuelve un JSON con un campo "content" que contiene la lista real.
 */
public class PaginacionPost {

    @SerializedName("content")
    private List<Post> content;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("last")
    private boolean last;

    @SerializedName("number")
    private int number; // página actual (0-indexed)

    public List<Post> getContent()    { return content; }
    public int getTotalPages()        { return totalPages; }
    public long getTotalElements()    { return totalElements; }
    public boolean isLast()           { return last; }
    public int getNumber()            { return number; }
}