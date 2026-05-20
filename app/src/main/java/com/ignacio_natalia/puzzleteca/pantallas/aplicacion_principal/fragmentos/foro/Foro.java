package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro;

import android.annotation.SuppressLint;
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
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.post.Post;
import com.ignacio_natalia.puzzleteca.modelos.clases.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.PuzzleDialogFragment;
import com.ignacio_natalia.puzzleteca.pantallas.inicio.PantallaInicio;
import com.ignacio_natalia.puzzleteca.repositorios.PostRepositorio;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;
import com.ignacio_natalia.puzzleteca.utilidades.UtilidadesSesion;
import com.ignacio_natalia.puzzleteca.modelos.comentarios.Comentario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Foro extends Fragment {


    private ForoViewModel viewModel;
    private LinearLayout contenedor;
    private ProgressBar progressBar;

    private ActivityResultLauncher<Intent> launcherGaleria;
    private ActivityResultLauncher<Uri>    launcherCamara;

    private File imagenSeleccionada = null;
    private final String mimeSeleccionado = null;
    private final Uri uriCamaraTemp = null;
    private ImageView previewDialog = null;

    // ── Puzzles del usuario (para el selector) ────────────────────────────────
    private final PuzzleRepositorio puzzleRepositorio = new PuzzleRepositorio();

    private final PostRepositorio postRepositorio = new PostRepositorio();
    private List<Puzzle> misPuzzles = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(ForoViewModel.class);
        boolean invitado = GestorSesion.esInvitado(requireContext());


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
        titulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        LinearLayout.LayoutParams tituloParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titulo.setLayoutParams(tituloParams);
        cabecera.addView(titulo);


        if (!invitado) {

            Button btnCrear = new Button(requireContext());
            btnCrear.setText("+ Post");
            btnCrear.setAllCaps(false);
            btnCrear.setTextSize(14);
            btnCrear.setTextColor(Color.WHITE);
            btnCrear.setTypeface(null, Typeface.BOLD);

            GradientDrawable fondoBtn = new GradientDrawable();
            fondoBtn.setColor(ContextCompat.getColor(requireContext(), R.color.app_rosa));
            fondoBtn.setCornerRadius(dp(50));

            btnCrear.setBackground(fondoBtn);
            btnCrear.setPadding(dp(20), dp(10), dp(20), dp(10));
            btnCrear.setElevation(6f);

            btnCrear.setOnClickListener(v -> cargarMisPuzzlesYMostrarDialog());

            cabecera.addView(btnCrear);

        } else {

            LinearLayout banner = new LinearLayout(requireContext());
            banner.setOrientation(LinearLayout.HORIZONTAL);
            banner.setGravity(Gravity.CENTER_VERTICAL);
            banner.setPadding(dp(16), dp(10), dp(16), dp(10));

            GradientDrawable fondoBanner = new GradientDrawable();
            fondoBanner.setColor(ContextCompat.getColor(requireContext(), R.color.app_naranja_fondo));
            fondoBanner.setCornerRadius(dp(12));
            fondoBanner.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_naranja_borde));

            banner.setBackground(fondoBanner);

            LinearLayout.LayoutParams bannerParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

            bannerParams.setMargins(dp(14), dp(4), dp(14), dp(8));

            banner.setLayoutParams(bannerParams);

            TextView textoBanner = new TextView(requireContext());
            textoBanner.setText("🔒 ");
            textoBanner.setTextSize(14);
            textoBanner.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_naranja_text));

            banner.addView(textoBanner);

            TextView textoInfo = new TextView(requireContext());
            textoInfo.setText("Para publicar, ");
            textoInfo.setTextSize(14);
            textoInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_naranja_text));

            banner.addView(textoInfo);

            TextView linkLogin = new TextView(requireContext());
            linkLogin.setText("inicia sesión");
            linkLogin.setTextSize(14);
            linkLogin.setTypeface(null, Typeface.BOLD);
            linkLogin.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_rosa));

            linkLogin.setPaintFlags(
                    linkLogin.getPaintFlags()
                            | android.graphics.Paint.UNDERLINE_TEXT_FLAG
            );

            linkLogin.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), PantallaInicio.class);

                intent.setFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                );

                startActivity(intent);
            });

            banner.addView(linkLogin);

            cabecera.addView(banner);
        }

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

    @SuppressLint("SetTextI18n")
    private void mostrarDialogCrearPost() {
        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(
                    android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        // ── Raíz de la tarjeta ────────────────────────────────────────────────
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(16));
        GradientDrawable rootBg = new GradientDrawable();
        rootBg.setColor(Color.WHITE);
        rootBg.setCornerRadius(dp(20));
        root.setBackground(rootBg);

        // ── Encabezado centrado (consistente con UtilidadesSesion) ────────────
        TextView tvTitulo = new TextView(requireContext());
        tvTitulo.setText("Nuevo post");
        tvTitulo.setTextSize(19);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        tvTitulo.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titLP.bottomMargin = dp(16);
        tvTitulo.setLayoutParams(titLP);
        root.addView(tvTitulo);

        // ── Campo de texto ────────────────────────────────────────────────────
        EditText inputTexto = new EditText(requireContext());
        inputTexto.setHint("¿Qué quieres compartir?");
        inputTexto.setMinLines(3);
        inputTexto.setMaxLines(6);
        inputTexto.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        inputTexto.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        GradientDrawable fondoInput = new GradientDrawable();
        fondoInput.setColor(ContextCompat.getColor(requireContext(), R.color.app_fondo_input));
        fondoInput.setCornerRadius(dp(12));
        fondoInput.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_rosa_light));
        inputTexto.setBackground(fondoInput);
        inputTexto.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams inputLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        inputLP.bottomMargin = dp(14);
        inputTexto.setLayoutParams(inputLP);
        root.addView(inputTexto);

        // ── Selector de puzzle ────────────────────────────────────────────────
        if (!misPuzzles.isEmpty()) {
            TextView tvPuzzleLabel = new TextView(requireContext());
            tvPuzzleLabel.setText("🧩  Vincular puzzle (opcional)");
            tvPuzzleLabel.setTextSize(13);
            tvPuzzleLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            tvPuzzleLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
            LinearLayout.LayoutParams lblLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lblLP.bottomMargin = dp(6);
            tvPuzzleLabel.setLayoutParams(lblLP);
            root.addView(tvPuzzleLabel);

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
            spBg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_rosa_light));
            puzzleSpinner.setBackground(spBg);
            puzzleSpinner.setPadding(dp(10), dp(8), dp(10), dp(8));
            LinearLayout.LayoutParams spLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            spLP.bottomMargin = dp(14);
            puzzleSpinner.setLayoutParams(spLP);
            root.addView(puzzleSpinner);

            root.setTag(puzzleSpinner);
        }

        // ── Divisor ───────────────────────────────────────────────────────────
        View divisor = new View(requireContext());
        LinearLayout.LayoutParams divLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        divLP.setMargins(0, dp(4), 0, dp(14));
        divisor.setLayoutParams(divLP);
        divisor.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_borde));
        root.addView(divisor);

        // ── Botón Publicar ────────────────────────────────────────────────────
        TextView btnPublicar = new TextView(requireContext());
        btnPublicar.setText("📤  Publicar");
        btnPublicar.setTextColor(Color.WHITE);
        btnPublicar.setTextSize(15);
        btnPublicar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnPublicar.setGravity(Gravity.CENTER);
        btnPublicar.setPadding(0, dp(14), 0, dp(14));
        LinearLayout.LayoutParams pubLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pubLP.bottomMargin = dp(10);
        btnPublicar.setLayoutParams(pubLP);
        GradientDrawable pubBg = new GradientDrawable();
        pubBg.setColor(ContextCompat.getColor(requireContext(), R.color.app_rosa));
        pubBg.setCornerRadius(dp(12));
        btnPublicar.setBackground(pubBg);
        btnPublicar.setClickable(true);
        btnPublicar.setFocusable(true);

        // ── Botón Cancelar ────────────────────────────────────────────────────
        TextView btnCancelar = new TextView(requireContext());
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        btnCancelar.setTextSize(14);
        btnCancelar.setGravity(Gravity.CENTER);
        btnCancelar.setPadding(0, dp(12), 0, dp(12));
        btnCancelar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable cancelBg = new GradientDrawable();
        cancelBg.setColor(ContextCompat.getColor(requireContext(), R.color.app_fondo_cancelar));
        cancelBg.setCornerRadius(dp(12));
        btnCancelar.setBackground(cancelBg);
        btnCancelar.setClickable(true);
        btnCancelar.setFocusable(true);

        root.addView(btnPublicar);
        root.addView(btnCancelar);

        // ── Lógica de publicar ────────────────────────────────────────────────
        btnPublicar.setOnClickListener(v -> {
            String texto  = inputTexto.getText().toString().trim();
            int idUsuario = GestorSesion.obtenerId_usuario(requireContext());
            String token  = obtenerToken();

            Spinner sp = (Spinner) root.getTag();
            Puzzle puzzleSeleccionado = null;
            if (sp != null && sp.getSelectedItemPosition() > 0) {
                puzzleSeleccionado = misPuzzles.get(sp.getSelectedItemPosition() - 1);
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
            final Puzzle puzzleFinal = puzzleSeleccionado;

            if (imagenSeleccionada == null
                    && puzzleFinal != null
                    && puzzleFinal.getImagenUrl() != null
                    && !puzzleFinal.getImagenUrl().isBlank()) {

                final String urlImagen = puzzleFinal.getImagenUrl();
                progressBar.setVisibility(View.VISIBLE);
                dialog.dismiss();

                new Thread(() -> {
                    ImagenDescargada img = descargarImagenComoFile(urlImagen);
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        viewModel.crearPost(token, idUsuario, textoFinal,
                                img != null ? img.file : null,
                                img != null ? img.mime : null);
                    });
                }).start();
            } else {
                viewModel.crearPost(token, idUsuario, textoFinal,
                        imagenSeleccionada, mimeSeleccionado);
                dialog.dismiss();
            }
        });

        btnCancelar.setOnClickListener(v -> {
            imagenSeleccionada = null;
            previewDialog = null;
            dialog.dismiss();
        });

        dialog.setOnDismissListener(d -> previewDialog = null);
        dialog.setContentView(root);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        if (dialog.getWindow() != null) {
            int screenW = requireContext().getResources().getDisplayMetrics().widthPixels;
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int)(screenW * 0.92f),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
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
            msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
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
        fondo.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_rosa_light));
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
        avatarFondo.setColor(ContextCompat.getColor(requireContext(), R.color.app_teal));
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
        autorTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        infoCol.addView(autorTv);

        if (post.getFechaCreacion() != null && !post.getFechaCreacion().isEmpty()) {
            TextView fechaTv = new TextView(requireContext());
            String fecha = post.getFechaCreacion().replace("T", " ");
            if (fecha.length() > 16) fecha = fecha.substring(0, 16);
            fechaTv.setText(fecha);
            fechaTv.setTextSize(11);
            fechaTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
            infoCol.addView(fechaTv);
        }
        cabecera.addView(infoCol);

        int idUsuarioSesion = GestorSesion.obtenerId_usuario(requireContext());
        if (post.getIdUsuario() != null && post.getIdUsuario() == idUsuarioSesion) {
            ImageButton btnEliminar = new ImageButton(requireContext());
            btnEliminar.setImageResource(android.R.drawable.ic_menu_delete);
            btnEliminar.setBackgroundColor(Color.TRANSPARENT);
            btnEliminar.setColorFilter(ContextCompat.getColor(requireContext(), R.color.app_rosa));
            btnEliminar.setOnClickListener(v -> confirmarEliminar(post.getId(), idUsuarioSesion));
            cabecera.addView(btnEliminar);
        }
        tarjeta.addView(cabecera);

        View separador = new View(requireContext());
        separador.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1)));
        separador.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_rosa_soft));
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
            contenidoTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
            tarjeta.addView(contenidoTv);
        }

        LinearLayout acciones = new LinearLayout(requireContext());
        acciones.setOrientation(LinearLayout.HORIZONTAL);
        acciones.setPadding(dp(14), dp(10), dp(14), dp(8));
        acciones.setGravity(Gravity.CENTER_VERTICAL);

        acciones.addView(crearBotonLikes(post));

        // =========================================================================
        // BOTÓN COMENTARIOS
        // =========================================================================

        LinearLayout btnComentarios = new LinearLayout(requireContext());
        btnComentarios.setOrientation(LinearLayout.HORIZONTAL);
        btnComentarios.setGravity(Gravity.CENTER_VERTICAL);

        GradientDrawable fondoComentarios = new GradientDrawable();
        fondoComentarios.setColor(ContextCompat.getColor(requireContext(), R.color.app_fondo_card));
        fondoComentarios.setCornerRadius(dp(20));

        btnComentarios.setBackground(fondoComentarios);

        LinearLayout.LayoutParams btnComParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        btnComParams.setMargins(dp(10), 0, 0, 0);

        btnComentarios.setLayoutParams(btnComParams);

        btnComentarios.setPadding(dp(12), dp(6), dp(12), dp(6));

        TextView iconComentario = new TextView(requireContext());
        iconComentario.setText("💬");
        iconComentario.setTextSize(14);

        TextView txtComentario = new TextView(requireContext());
        txtComentario.setText("Comentarios");
        txtComentario.setTextSize(13);
        txtComentario.setTypeface(null, Typeface.BOLD);
        txtComentario.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        txtComentario.setPadding(dp(6), 0, 0, 0);

        btnComentarios.addView(iconComentario);
        btnComentarios.addView(txtComentario);

        acciones.addView(btnComentarios);

        tarjeta.addView(acciones);

        // =========================================================================
        // CONTENEDOR COMENTARIOS
        // =========================================================================

        LinearLayout comentariosContainer = new LinearLayout(requireContext());
        comentariosContainer.setOrientation(LinearLayout.VERTICAL);
        comentariosContainer.setPadding(dp(14), dp(4), dp(14), dp(12));
        comentariosContainer.setVisibility(View.GONE);

        tarjeta.addView(comentariosContainer);

        // =========================================================================
        // INPUT COMENTARIO
        // =========================================================================

        LinearLayout escribirLayout = new LinearLayout(requireContext());
        escribirLayout.setOrientation(LinearLayout.HORIZONTAL);
        escribirLayout.setGravity(Gravity.CENTER_VERTICAL);

        EditText inputComentario = new EditText(requireContext());

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        inputComentario.setLayoutParams(inputParams);

        inputComentario.setHint("Escribe un comentario...");
        inputComentario.setTextSize(13);

        GradientDrawable fondoInputComentario = new GradientDrawable();
        fondoInputComentario.setColor(ContextCompat.getColor(requireContext(), R.color.app_fondo_pagina));
        fondoInputComentario.setCornerRadius(dp(16));
        fondoInputComentario.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_borde_gris));

        inputComentario.setBackground(fondoInputComentario);
        inputComentario.setPadding(dp(12), dp(8), dp(12), dp(8));

        Button btnEnviarComentario = new Button(requireContext());
        btnEnviarComentario.setText("Enviar");
        btnEnviarComentario.setAllCaps(false);
        btnEnviarComentario.setTextColor(Color.WHITE);

        GradientDrawable fondoEnviar = new GradientDrawable();
        fondoEnviar.setColor(ContextCompat.getColor(requireContext(), R.color.app_rosa));
        fondoEnviar.setCornerRadius(dp(20));

        btnEnviarComentario.setBackground(fondoEnviar);

        LinearLayout.LayoutParams enviarParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        enviarParams.setMargins(dp(8), 0, 0, 0);

        btnEnviarComentario.setLayoutParams(enviarParams);

        escribirLayout.addView(inputComentario);
        escribirLayout.addView(btnEnviarComentario);

        comentariosContainer.addView(escribirLayout);

        // =========================================================================
        // LISTA COMENTARIOS
        // =========================================================================

        LinearLayout listaComentarios = new LinearLayout(requireContext());
        listaComentarios.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams listaParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        listaParams.setMargins(0, dp(10), 0, 0);

        listaComentarios.setLayoutParams(listaParams);

        comentariosContainer.addView(listaComentarios);

        // =========================================================================
        // OBSERVER COMENTARIOS
        // =========================================================================

        viewModel.getComentariosPorPost().observe(getViewLifecycleOwner(), mapa -> {

            if (mapa == null) return;

            List<Comentario> comentarios = mapa.get(post.getId());

            listaComentarios.removeAllViews();

            if (comentarios == null || comentarios.isEmpty()) {

                TextView vacio = new TextView(requireContext());
                vacio.setText("Todavía no hay comentarios");
                vacio.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
                vacio.setTextSize(12);

                listaComentarios.addView(vacio);

                return;
            }

            for (Comentario comentario : comentarios) {

                LinearLayout item = new LinearLayout(requireContext());
                item.setOrientation(LinearLayout.VERTICAL);

                GradientDrawable fondoComentario = new GradientDrawable();
                fondoComentario.setColor(ContextCompat.getColor(requireContext(), R.color.app_fondo_pagina));
                fondoComentario.setCornerRadius(dp(12));

                item.setBackground(fondoComentario);

                LinearLayout.LayoutParams itemParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                itemParams.setMargins(0, 0, 0, dp(8));

                item.setLayoutParams(itemParams);

                item.setPadding(dp(10), dp(8), dp(10), dp(8));

                TextView autorComentario = new TextView(requireContext());

                String autor =
                        comentario.getNombreUsuario() != null
                                ? comentario.getNombreUsuario()
                                : "Usuario";

                autorComentario.setText(autor);

                autorComentario.setTypeface(null, Typeface.BOLD);
                autorComentario.setTextSize(12);
                autorComentario.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_teal));

                item.addView(autorComentario);

                TextView contenidoComentario = new TextView(requireContext());

                contenidoComentario.setText(comentario.getContenido());

                contenidoComentario.setTextSize(13);
                contenidoComentario.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));

                LinearLayout.LayoutParams contenidoParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                contenidoParams.setMargins(0, dp(2), 0, 0);

                contenidoComentario.setLayoutParams(contenidoParams);

                item.addView(contenidoComentario);

                listaComentarios.addView(item);
            }
        });

        // =========================================================================
        // MOSTRAR / OCULTAR COMENTARIOS
        // =========================================================================

        final boolean[] comentariosVisibles = {false};

        btnComentarios.setOnClickListener(v -> {

            comentariosVisibles[0] = !comentariosVisibles[0];

            comentariosContainer.setVisibility(
                    comentariosVisibles[0]
                            ? View.VISIBLE
                            : View.GONE
            );

            if (comentariosVisibles[0]) {

                viewModel.cargarComentarios(
                        obtenerToken(),
                        post.getId()
                );
            }
        });

        // =========================================================================
        // ENVIAR COMENTARIO
        // =========================================================================

        btnEnviarComentario.setOnClickListener(v -> {

            String texto = inputComentario.getText().toString().trim();

            if (texto.isEmpty()) {
                Toast.makeText(
                        requireContext(),
                        "Escribe un comentario",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Comentario comentario = new Comentario();

            comentario.setContenido(texto);

            comentario.setIdUsuario(
                    GestorSesion.obtenerId_usuario(requireContext())
            );

            comentario.setIdPost(post.getId());

            viewModel.crearComentario(
                    obtenerToken(),
                    comentario
            );

            inputComentario.setText("");
        });

        tarjeta.setOnClickListener(v -> {
            if (post.getId() == null) return;

            String token = obtenerToken();
            // Mostramos un loading mientras cargamos
            progressBar.setVisibility(View.VISIBLE);

            // Llamada al backend para obtener el post con el puzzle
            // Necesitas inyectar PostRepositorio en Foro
            postRepositorio.obtenerPost(token, post.getId(), new retrofit2.Callback<Post>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<Post> call,
                                       @NonNull retrofit2.Response<Post> response) {
                    progressBar.setVisibility(View.GONE);
                    if (!response.isSuccessful() || response.body() == null) return;

                    Post postCompleto = response.body();
                    Puzzle puzzle = postCompleto.getPuzzle();

                    if (puzzle == null) return; // post sin puzzle vinculado

                    PuzzleDialogFragment dialog = PuzzleDialogFragment.newInstance(puzzle);
                    dialog.show(getParentFragmentManager(), "puzzle_dialog");
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<Post> call,
                                      @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error al cargar el puzzle", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return tarjeta;
    }

    @SuppressLint("SetTextI18n")
    private LinearLayout crearBotonLikes(Post post) {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        GradientDrawable fondoLike = new GradientDrawable();
        fondoLike.setColor(ContextCompat.getColor(requireContext(), R.color.app_rosa_fondo));
        fondoLike.setCornerRadius(dp(20));
        fondoLike.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_rosa_light));
        layout.setBackground(fondoLike);
        layout.setPadding(dp(12), dp(6), dp(14), dp(6));

        ImageView icon = new ImageView(requireContext());
        icon.setLayoutParams(new LinearLayout.LayoutParams(dp(20), dp(20)));

        TextView txt = new TextView(requireContext());
        txt.setPadding(dp(6), 0, 0, 0);
        txt.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
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
        txt.setText(String.valueOf(count));
    }

    private void confirmarEliminar(Integer idPost, int idUsuario) {
        UtilidadesSesion.mostrarDialogoPersonalizado(
                requireContext(),
                "🗑️",
                "Eliminar post",
                "¿Seguro que quieres eliminar este post?\nEsta acción no se puede deshacer.",
                "Eliminar",
                "#E53935",
                () -> viewModel.eliminarPost(obtenerToken(), idPost, idUsuario)
        );
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