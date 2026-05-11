package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Post;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment del Foro.
 *
 * NUEVO: al crear un post el usuario puede vincular uno de sus puzzles.
 * Esto muestra un spinner con los puzzles propios y añade la info del puzzle
 * seleccionado al contenido del post.
 */
public class Foro extends Fragment {

    private static final int COLOR_PRIMARIO   = Color.parseColor("#F06292");
    private static final int COLOR_SECUNDARIO = Color.parseColor("#26A69A");
    private static final int COLOR_TEXTO      = Color.parseColor("#37474F");
    private static final int COLOR_TEXTO_LEVE = Color.parseColor("#78909C");
    private static final int COLOR_CARD_BORDE = Color.parseColor("#F8BBD9");

    private ForoViewModel viewModel;
    private LinearLayout contenedor;
    private ProgressBar progressBar;

    private ActivityResultLauncher<Intent> launcherGaleria;
    private ActivityResultLauncher<Uri>    launcherCamara;

    private File imagenSeleccionada = null;
    private String mimeSeleccionado = null;
    private Uri uriCamaraTemp = null;
    private ImageView previewDialog = null;

    // ── Puzzles del usuario (para el selector) ────────────────────────────────
    private final PuzzleRepositorio puzzleRepositorio = new PuzzleRepositorio();
    private List<Puzzle> misPuzzles = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launcherGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri == null) return;
                        File f = uriAFile(uri);
                        if (f != null) {
                            imagenSeleccionada = f;
                            actualizarPreviewDialog(uri);
                        } else {
                            Toast.makeText(requireContext(),
                                    "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        launcherCamara = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                exito -> {
                    if (exito && uriCamaraTemp != null) {
                        File f = uriAFile(uriCamaraTemp);
                        if (f != null) {
                            imagenSeleccionada = f;
                            actualizarPreviewDialog(uriCamaraTemp);
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(ForoViewModel.class);

        LinearLayout raiz = new LinearLayout(requireContext());
        raiz.setOrientation(LinearLayout.VERTICAL);
        raiz.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Cabecera
        LinearLayout cabecera = new LinearLayout(requireContext());
        cabecera.setOrientation(LinearLayout.HORIZONTAL);
        cabecera.setGravity(Gravity.CENTER_VERTICAL);
        cabecera.setPadding(dp(16), dp(16), dp(16), dp(8));

        TextView titulo = new TextView(requireContext());
        titulo.setText("💬 Foro");
        titulo.setTextSize(22);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(COLOR_TEXTO);
        LinearLayout.LayoutParams tituloParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titulo.setLayoutParams(tituloParams);
        cabecera.addView(titulo);

        Button btnCrear = new Button(requireContext());
        btnCrear.setText("+ Post");
        btnCrear.setAllCaps(false);
        btnCrear.setTextSize(14);
        btnCrear.setTextColor(Color.WHITE);
        btnCrear.setTypeface(null, Typeface.BOLD);
        GradientDrawable fondoBtn = new GradientDrawable();
        fondoBtn.setColor(COLOR_PRIMARIO);
        fondoBtn.setCornerRadius(dp(50));
        btnCrear.setBackground(fondoBtn);
        btnCrear.setPadding(dp(20), dp(10), dp(20), dp(10));
        btnCrear.setElevation(6f);
        btnCrear.setOnClickListener(v -> cargarMisPuzzlesYMostrarDialog());
        cabecera.addView(btnCrear);
        raiz.addView(cabecera);

        progressBar = new ProgressBar(requireContext());
        progressBar.setVisibility(View.GONE);
        LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pbParams.gravity = Gravity.CENTER_HORIZONTAL;
        progressBar.setLayoutParams(pbParams);
        raiz.addView(progressBar);

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        scroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity instanceof AppPrincipal) {
                if (scrollY > oldScrollY) ((AppPrincipal) activity).ocultarBarra();
                else                       ((AppPrincipal) activity).mostrarBarra();
            }
            ScrollView sv = (ScrollView) v;
            View child = sv.getChildAt(0);
            if (child != null) {
                int diff = child.getBottom() - (sv.getHeight() + scrollY);
                if (diff <= dp(80)) viewModel.cargarMasPosts(obtenerToken());
            }
        });

        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(dp(14), dp(8), dp(14), dp(90));
        scroll.addView(contenedor);
        raiz.addView(scroll);

        viewModel.getPosts().observe(getViewLifecycleOwner(), this::mostrarPosts);
        viewModel.getError().observe(getViewLifecycleOwner(), this::mostrarError);
        viewModel.getCargando().observe(getViewLifecycleOwner(), cargando ->
                progressBar.setVisibility(Boolean.TRUE.equals(cargando) ? View.VISIBLE : View.GONE));
        viewModel.getPostCreado().observe(getViewLifecycleOwner(), creado -> {
            if (Boolean.TRUE.equals(creado)) {
                Toast.makeText(requireContext(), "✅ Post publicado", Toast.LENGTH_SHORT).show();
                imagenSeleccionada = null;
            }
        });

        viewModel.cargarPosts(obtenerToken());
        return raiz;
    }

    // =========================================================================
    // Carga puzzles del usuario y abre el dialog
    // =========================================================================

    /**
     * Carga los puzzles del usuario en segundo plano y luego abre el dialog de post.
     * Si la carga falla o no hay puzzles, abre el dialog igualmente (sin selector).
     */
    private void cargarMisPuzzlesYMostrarDialog() {
        imagenSeleccionada = null;
        int idUsuario = GestorSesion.obtenerId_usuario(requireContext());
        String token  = obtenerToken();

        puzzleRepositorio.misPuzzles(token, idUsuario, new retrofit2.Callback<List<Puzzle>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<Puzzle>> call,
                                   @NonNull retrofit2.Response<List<Puzzle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    misPuzzles = response.body();
                } else {
                    misPuzzles = new ArrayList<>();
                }
                mostrarDialogCrearPost();
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<List<Puzzle>> call,
                                  @NonNull Throwable t) {
                misPuzzles = new ArrayList<>();
                mostrarDialogCrearPost();
            }
        });
    }

    // =========================================================================
    // Dialog crear post — con selector de puzzle
    // =========================================================================

    private void mostrarDialogCrearPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("✏️ Nuevo post");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(12), dp(20), dp(8));

        // Campo de texto
        EditText inputTexto = new EditText(requireContext());
        inputTexto.setHint("¿Qué quieres compartir?");
        inputTexto.setMinLines(3);
        inputTexto.setMaxLines(6);
        inputTexto.setTextColor(COLOR_TEXTO);
        inputTexto.setHintTextColor(COLOR_TEXTO_LEVE);
        GradientDrawable fondoInput = new GradientDrawable();
        fondoInput.setColor(Color.parseColor("#FFF6F9"));
        fondoInput.setCornerRadius(dp(12));
        fondoInput.setStroke(dp(1), COLOR_CARD_BORDE);
        inputTexto.setBackground(fondoInput);
        inputTexto.setPadding(dp(12), dp(10), dp(12), dp(10));
        layout.addView(inputTexto);

        espacio(layout, dp(12));

        // ── Selector de puzzle ────────────────────────────────────────────────
        if (!misPuzzles.isEmpty()) {
            TextView tvPuzzleLabel = new TextView(requireContext());
            tvPuzzleLabel.setText("🧩 Vincular puzzle (opcional)");
            tvPuzzleLabel.setTextSize(13);
            tvPuzzleLabel.setTextColor(COLOR_TEXTO_LEVE);
            tvPuzzleLabel.setPadding(0, 0, 0, dp(4));
            layout.addView(tvPuzzleLabel);

            // Opciones: "Ninguno" + puzzles del usuario
            List<String> opciones = new ArrayList<>();
            opciones.add("— Sin puzzle —");
            for (Puzzle p : misPuzzles) {
                String label = p.getTitulo();
                if (p.getPiezas() != null) label += " · " + p.getPiezas() + " piezas";
                if (p.getDificultad() != null) label += " · " + p.getDificultad().name();
                opciones.add(label);
            }

            Spinner puzzleSpinner = new Spinner(requireContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, opciones);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            puzzleSpinner.setAdapter(adapter);
            GradientDrawable spBg = new GradientDrawable();
            spBg.setColor(Color.WHITE);
            spBg.setCornerRadius(dp(10));
            spBg.setStroke(dp(1), COLOR_CARD_BORDE);
            puzzleSpinner.setBackground(spBg);
            puzzleSpinner.setPadding(dp(10), dp(8), dp(10), dp(8));
            LinearLayout.LayoutParams spParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            spParams.setMargins(0, 0, 0, dp(12));
            puzzleSpinner.setLayoutParams(spParams);
            layout.addView(puzzleSpinner);

            // Cuando el usuario confirma el dialog usamos esta referencia
            layout.setTag(puzzleSpinner);
        }

        // ── Botones de imagen ────────────────────────────────────────────────

        builder.setView(layout);
        AlertDialog dialog = builder.create();


        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Publicar", (d, which) -> {
            String texto    = inputTexto.getText().toString().trim();
            int idUsuario   = GestorSesion.obtenerId_usuario(requireContext());
            String token    = obtenerToken();

            // Detectar si se seleccionó un puzzle
            Spinner sp = (Spinner) layout.getTag();
            Puzzle puzzleSeleccionado = null;
            if (sp != null && sp.getSelectedItemPosition() > 0) {
                puzzleSeleccionado = misPuzzles.get(sp.getSelectedItemPosition() - 1);
                String infoPuzzle = construirInfoPuzzle(puzzleSeleccionado);
                texto = texto.isEmpty() ? infoPuzzle : texto + "\n\n" + infoPuzzle;
            }

            if (texto.isEmpty() && imagenSeleccionada == null
                    && (puzzleSeleccionado == null
                    || puzzleSeleccionado.getImagenUrl() == null
                    || puzzleSeleccionado.getImagenUrl().isBlank())) {
                Toast.makeText(requireContext(),
                        "Escribe algo o añade una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            final String textoFinal = texto;

            // Si el usuario NO ha seleccionado su propia imagen pero el puzzle tiene una,
            // la descargamos en background y luego enviamos el post.
            if (imagenSeleccionada == null
                    && puzzleSeleccionado != null
                    && puzzleSeleccionado.getImagenUrl() != null
                    && !puzzleSeleccionado.getImagenUrl().isBlank()) {

                final String urlImagen = puzzleSeleccionado.getImagenUrl();
                progressBar.setVisibility(View.VISIBLE);

                new Thread(() -> {

                    ImagenDescargada img = descargarImagenComoFile(urlImagen);

                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        viewModel.crearPost(
                                token,
                                idUsuario,
                                textoFinal,
                                img != null ? img.file : null,
                                img != null ? img.mime : null
                        );
                    });
                }).start();

            } else {
                viewModel.crearPost(token, idUsuario, textoFinal, imagenSeleccionada, mimeSeleccionado);
            }
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", (d, which) -> {
            imagenSeleccionada = null;
            previewDialog = null;
        });

        dialog.setOnDismissListener(d -> previewDialog = null);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(COLOR_PRIMARIO);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(COLOR_TEXTO_LEVE);
    }

    /**
     * Construye un bloque de texto descriptivo con la info del puzzle seleccionado.
     */
    private String construirInfoPuzzle(Puzzle p) {
        StringBuilder sb = new StringBuilder();
        sb.append("🧩 Puzzle: ").append(p.getTitulo() != null ? p.getTitulo() : "Sin título");
        if (p.getAutor() != null && !p.getAutor().isBlank())
            sb.append("\n✍️ Autor: ").append(p.getAutor());
        if (p.getPiezas() != null)
            sb.append("\n🔢 Piezas: ").append(p.getPiezas());
        if (p.getDificultad() != null)
            sb.append("\n⚡ Dificultad: ").append(p.getDificultad().name());
        if (p.getTiempo() != null)
            sb.append("\n⏱️ Tiempo: ").append(p.getTiempo()).append("h");
        if (p.getDescripcion() != null && !p.getDescripcion().isBlank())
            sb.append("\n📝 ").append(p.getDescripcion());
        return sb.toString();
    }

    private void actualizarPreviewDialog(Uri uri) {
        if (previewDialog == null) return;
        previewDialog.setImageURI(uri);
        previewDialog.setVisibility(View.VISIBLE);
    }

    // =========================================================================
    // Renderizado de posts
    // =========================================================================

    private void mostrarPosts(List<Post> posts) {
        contenedor.removeAllViews();
        if (posts == null || posts.isEmpty()) {
            LinearLayout vacio = new LinearLayout(requireContext());
            vacio.setOrientation(LinearLayout.VERTICAL);
            vacio.setGravity(Gravity.CENTER);
            vacio.setPadding(0, dp(60), 0, 0);

            TextView emoji = new TextView(requireContext());
            emoji.setText("💬");
            emoji.setTextSize(48);
            emoji.setGravity(Gravity.CENTER);

            TextView msg = new TextView(requireContext());
            msg.setText("Todavía no hay posts.\n¡Sé el primero!");
            msg.setGravity(Gravity.CENTER);
            msg.setTextSize(16);
            msg.setTextColor(COLOR_TEXTO_LEVE);
            LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            msgParams.setMargins(0, dp(12), 0, 0);
            msg.setLayoutParams(msgParams);

            vacio.addView(emoji);
            vacio.addView(msg);
            contenedor.addView(vacio);
            return;
        }
        for (Post post : posts) {
            contenedor.addView(crearTarjeta(post));
        }
    }

    @SuppressLint("SetTextI18n")
    private View crearTarjeta(Post post) {
        LinearLayout tarjeta = new LinearLayout(requireContext());
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(dp(20));
        fondo.setStroke(dp(1), COLOR_CARD_BORDE);
        tarjeta.setBackground(fondo);
        tarjeta.setElevation(4f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(14));
        tarjeta.setLayoutParams(params);

        LinearLayout cabecera = new LinearLayout(requireContext());
        cabecera.setOrientation(LinearLayout.HORIZONTAL);
        cabecera.setPadding(dp(14), dp(14), dp(14), dp(6));
        cabecera.setGravity(Gravity.CENTER_VERTICAL);

        TextView avatar = new TextView(requireContext());
        String nombre = post.getNombreUsuario() != null ? post.getNombreUsuario() : "U";
        avatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
        avatar.setTextSize(16);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(Color.WHITE);
        avatar.setGravity(Gravity.CENTER);
        int avatarSize = dp(38);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarParams.setMargins(0, 0, dp(10), 0);
        avatar.setLayoutParams(avatarParams);
        GradientDrawable avatarFondo = new GradientDrawable();
        avatarFondo.setShape(GradientDrawable.OVAL);
        avatarFondo.setColor(COLOR_SECUNDARIO);
        avatar.setBackground(avatarFondo);
        cabecera.addView(avatar);

        LinearLayout infoCol = new LinearLayout(requireContext());
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView autorTv = new TextView(requireContext());
        autorTv.setText(nombre);
        autorTv.setTypeface(null, Typeface.BOLD);
        autorTv.setTextSize(14);
        autorTv.setTextColor(COLOR_TEXTO);
        infoCol.addView(autorTv);

        if (post.getFechaCreacion() != null && !post.getFechaCreacion().isEmpty()) {
            TextView fechaTv = new TextView(requireContext());
            String fecha = post.getFechaCreacion().replace("T", " ");
            if (fecha.length() > 16) fecha = fecha.substring(0, 16);
            fechaTv.setText(fecha);
            fechaTv.setTextSize(11);
            fechaTv.setTextColor(COLOR_TEXTO_LEVE);
            infoCol.addView(fechaTv);
        }
        cabecera.addView(infoCol);

        int idUsuarioSesion = GestorSesion.obtenerId_usuario(requireContext());
        if (post.getIdUsuario() != null && post.getIdUsuario() == idUsuarioSesion) {
            ImageButton btnEliminar = new ImageButton(requireContext());
            btnEliminar.setImageResource(android.R.drawable.ic_menu_delete);
            btnEliminar.setBackgroundColor(Color.TRANSPARENT);
            btnEliminar.setColorFilter(COLOR_PRIMARIO);
            btnEliminar.setOnClickListener(v -> confirmarEliminar(post.getId(), idUsuarioSesion));
            cabecera.addView(btnEliminar);
        }
        tarjeta.addView(cabecera);

        View separador = new View(requireContext());
        separador.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1)));
        separador.setBackgroundColor(Color.parseColor("#FCE4EC"));
        tarjeta.addView(separador);

        if (post.getImagenUrl() != null && !post.getImagenUrl().isEmpty()) {
            ImageView imagen = new ImageView(requireContext());
            imagen.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(220)));
            imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this)
                    .load(post.getImagenUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(imagen);
            tarjeta.addView(imagen);
        }

        if (post.getContenido() != null && !post.getContenido().isEmpty()) {
            TextView contenidoTv = new TextView(requireContext());
            contenidoTv.setText(post.getContenido());
            contenidoTv.setPadding(dp(14), dp(10), dp(14), dp(6));
            contenidoTv.setTextSize(15);
            contenidoTv.setTextColor(COLOR_TEXTO);
            tarjeta.addView(contenidoTv);
        }

        LinearLayout acciones = new LinearLayout(requireContext());
        acciones.setOrientation(LinearLayout.HORIZONTAL);
        acciones.setPadding(dp(14), dp(10), dp(14), dp(14));
        acciones.setGravity(Gravity.CENTER_VERTICAL);
        acciones.addView(crearBotonLikes(post));
        tarjeta.addView(acciones);

        return tarjeta;
    }

    @SuppressLint("SetTextI18n")
    private LinearLayout crearBotonLikes(Post post) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        GradientDrawable fondoLike = new GradientDrawable();
        fondoLike.setColor(Color.parseColor("#FFF0F5"));
        fondoLike.setCornerRadius(dp(20));
        fondoLike.setStroke(dp(1), Color.parseColor("#F8BBD9"));
        layout.setBackground(fondoLike);
        layout.setPadding(dp(12), dp(6), dp(14), dp(6));

        ImageView icon = new ImageView(requireContext());
        icon.setLayoutParams(new LinearLayout.LayoutParams(dp(20), dp(20)));

        TextView txt = new TextView(requireContext());
        txt.setPadding(dp(6), 0, 0, 0);
        txt.setTextColor(COLOR_TEXTO_LEVE);
        txt.setTypeface(null, Typeface.BOLD);
        txt.setTextSize(13);

        final boolean[] liked = {false};
        final int[] count = {post.getTotalLikes()};
        actualizarLikeUI(icon, txt, liked[0], count[0]);

        layout.setOnClickListener(v -> {
            liked[0] = !liked[0];
            count[0] += liked[0] ? 1 : -1;
            actualizarLikeUI(icon, txt, liked[0], count[0]);
            viewModel.toggleLike(obtenerToken(), post.getId(), liked[0]);
        });

        layout.addView(icon);
        layout.addView(txt);
        return layout;
    }

    private void actualizarLikeUI(ImageView icon, TextView txt, boolean liked, int count) {
        icon.setImageResource(liked ? R.drawable.like : R.drawable.no_like);
        icon.setColorFilter(liked ? COLOR_PRIMARIO : COLOR_TEXTO_LEVE);
        txt.setText(String.valueOf(count));
        txt.setTextColor(liked ? COLOR_PRIMARIO : COLOR_TEXTO_LEVE);
    }

    private void confirmarEliminar(Integer idPost, int idUsuario) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar post")
                .setMessage("¿Seguro que quieres eliminar este post?")
                .setPositiveButton("Eliminar", (d, w) ->
                        viewModel.eliminarPost(obtenerToken(), idPost, idUsuario))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =========================================================================
    // Utilidades
    // =========================================================================
    private class ImagenDescargada {
        File file;
        String mime;

        ImagenDescargada(File f, String m) {
            file = f;
            mime = m;
        }
    }

    /** Descarga una URL de imagen a un File temporal en caché (ejecutar en hilo secundario) */
    private ImagenDescargada descargarImagenComoFile(String url) {
        try {
            java.net.URL urlObj = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urlObj.openConnection();
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(15_000);
            conn.connect();

            if (conn.getResponseCode() != 200) return null;

            String mime = conn.getContentType();

            // Determinar extensión real
            String extension = ".jpg";
            if (mime != null) {
                if (mime.contains("png")) extension = ".png";
                else if (mime.contains("webp")) extension = ".webp";
                else if (mime.contains("gif")) extension = ".gif";
            }

            File temp = File.createTempFile("puzzle_post_img_", extension,
                    requireContext().getCacheDir());

            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(temp);

            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) {
                fos.write(buf, 0, n);
            }

            is.close();
            fos.close();
            conn.disconnect();

            return new ImagenDescargada(temp, mime);

        } catch (Exception e) {
            return null;
        }
    }

    private File uriAFile(Uri uri) {
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            if (is == null) return null;
            File temp = File.createTempFile("post_img_", ".jpg", requireContext().getCacheDir());
            FileOutputStream fos = new FileOutputStream(temp);
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) fos.write(buf, 0, n);
            is.close();
            fos.close();
            return temp;
        } catch (Exception e) {
            return null;
        }
    }

    private String obtenerMimeType(Uri uri) {
        return requireContext().getContentResolver().getType(uri);
    }

    private Button crearBotonImagen(String texto) {
        Button btn = new Button(requireContext());
        btn.setText(texto);
        btn.setAllCaps(false);
        btn.setTextSize(13);
        btn.setTextColor(COLOR_TEXTO);
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(dp(12));
        fondo.setStroke(dp(1), COLOR_CARD_BORDE);
        btn.setBackground(fondo);
        btn.setPadding(dp(8), dp(8), dp(8), dp(8));
        return btn;
    }

    private void espacio(LinearLayout parent, int alturaPx) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, alturaPx));
        parent.addView(v);
    }

    private void mostrarError(String msg) {
        if (msg == null) return;
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
    }

    private int dp(int v) {
        return (int) (v * requireContext().getResources().getDisplayMetrics().density);
    }

    private String obtenerToken() {
        return GestorSesion.obtenerToken(requireContext());
    }
}
