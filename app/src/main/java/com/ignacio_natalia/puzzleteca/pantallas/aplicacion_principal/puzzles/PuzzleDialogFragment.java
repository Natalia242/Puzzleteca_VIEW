package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.repositorios.RankingRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PuzzleDialogFragment extends DialogFragment {

    private static final String ARG_PUZZLE = "puzzle";

    private Puzzle puzzle;
    private RankingRepositorio rankingRepositorio;

    // ══════════════════════════════════════════════════════════════════════
    //  Vista de estrellas personalizadas con puntas redondeadas
    // ══════════════════════════════════════════════════════════════════════
    public static class RoundedStarRatingView extends View {

        public interface OnRatingChangeListener {
            void onRatingChanged(int stars, boolean fromUser);
        }

        private static final int   NUM_STARS      = 5;
        private static final float CORNER_RADIUS  = 3.5f;   // redondeo de puntas (dp)
        private static final float STAR_SIZE_DP   = 40f;
        private static final float STAR_GAP_DP    = 8f;

        private final Paint paintFilled   = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint paintEmpty    = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint paintStroke   = new Paint(Paint.ANTI_ALIAS_FLAG);

        private int   currentRating = 0;
        private boolean enabled     = true;

        private float starSizePx;
        private float starGapPx;
        private float cornerPx;

        private OnRatingChangeListener listener;

        public RoundedStarRatingView(Context ctx) {
            super(ctx);
            init(ctx);
        }

        public RoundedStarRatingView(Context ctx, AttributeSet attrs) {
            super(ctx, attrs);
            init(ctx);
        }

        private void init(Context ctx) {
            float density = ctx.getResources().getDisplayMetrics().density;
            starSizePx    = STAR_SIZE_DP  * density;
            starGapPx     = STAR_GAP_DP   * density;
            cornerPx      = CORNER_RADIUS * density;

            paintFilled.setStyle(Paint.Style.FILL);
            paintEmpty.setStyle(Paint.Style.FILL);
            paintEmpty.setColor(Color.parseColor("#E0E0E0"));

            paintStroke.setStyle(Paint.Style.STROKE);
            paintStroke.setStrokeWidth(1.5f * density);
            paintStroke.setColor(Color.parseColor("#BDBDBD"));
        }

        @Override
        protected void onMeasure(int widthSpec, int heightSpec) {
            int totalW = (int) (NUM_STARS * starSizePx + (NUM_STARS - 1) * starGapPx);
            int totalH = (int) starSizePx;
            setMeasuredDimension(
                    resolveSize(totalW, widthSpec),
                    resolveSize(totalH, heightSpec));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float cx = starSizePx / 2f;
            float cy = starSizePx / 2f;

            for (int i = 0; i < NUM_STARS; i++) {
                float offsetX = i * (starSizePx + starGapPx);
                boolean filled = (i < currentRating);

                // Gradiente dorado para las estrellas rellenas
                if (filled) {
                    LinearGradient gradient = new LinearGradient(
                            offsetX, 0,
                            offsetX, starSizePx,
                            Color.parseColor("#FFD740"),
                            Color.parseColor("#FF8F00"),
                            Shader.TileMode.CLAMP);
                    paintFilled.setShader(gradient);
                }

                Path star = buildRoundedStar(cx + offsetX, cy, starSizePx * 0.45f, starSizePx * 0.18f);

                canvas.drawPath(star, filled ? paintFilled : paintEmpty);

                if (!filled) {
                    canvas.drawPath(star, paintStroke);
                }
            }
        }

        /**
         * Construye un camino de estrella de 5 puntas con esquinas redondeadas.
         * Alterna vértices externos (puntas) e internos (valles).
         */
        private Path buildRoundedStar(float cx, float cy, float outerR, float innerR) {
            Path path = new Path();
            int points = 5;
            double step = Math.PI * 2 / points;
            double startAngle = -Math.PI / 2; // arriba

            // Calculamos los 10 vértices de la estrella
            float[] vx = new float[points * 2];
            float[] vy = new float[points * 2];

            for (int i = 0; i < points; i++) {
                double outerAngle = startAngle + i * step;
                double innerAngle = outerAngle + step / 2.0;
                vx[i * 2]     = cx + (float)(outerR * Math.cos(outerAngle));
                vy[i * 2]     = cy + (float)(outerR * Math.sin(outerAngle));
                vx[i * 2 + 1] = cx + (float)(innerR * Math.cos(innerAngle));
                vy[i * 2 + 1] = cy + (float)(innerR * Math.sin(innerAngle));
            }

            // Construimos el path con arcos redondeados en cada vértice
            int n = vx.length;
            for (int i = 0; i < n; i++) {
                int prev = (i - 1 + n) % n;
                int next = (i + 1) % n;

                // Vector de llegada y salida
                float inDx  = vx[i] - vx[prev];
                float inDy  = vy[i] - vy[prev];
                float outDx = vx[next] - vx[i];
                float outDy = vy[next] - vy[i];

                float inLen  = (float) Math.hypot(inDx,  inDy);
                float outLen = (float) Math.hypot(outDx, outDy);

                // Radio de esquina según si es punta o valle
                boolean isTip = (i % 2 == 0);
                float r = isTip ? cornerPx * 2f : cornerPx * 0.8f;
                r = Math.min(r, Math.min(inLen, outLen) * 0.35f);

                // Puntos de tangencia
                float t1x = vx[i] - inDx  / inLen  * r;
                float t1y = vy[i] - inDy  / inLen  * r;
                float t2x = vx[i] + outDx / outLen * r;
                float t2y = vy[i] + outDy / outLen * r;

                if (i == 0) {
                    path.moveTo(t1x, t1y);
                } else {
                    path.lineTo(t1x, t1y);
                }

                // Arco cuadrático que redondea la esquina
                path.quadTo(vx[i], vy[i], t2x, t2y);
            }
            path.close();
            return path;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!enabled) return false;
            if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_MOVE) {

                float x = event.getX();
                int stars = 0;
                for (int i = 0; i < NUM_STARS; i++) {
                    float starEnd = (i + 1) * starSizePx + i * starGapPx;
                    if (x <= starEnd) {
                        stars = i + 1;
                        break;
                    }
                }
                if (stars == 0) stars = NUM_STARS;

                if (currentRating != stars) {
                    currentRating = stars;
                    invalidate();
                    if (listener != null) listener.onRatingChanged(stars, true);
                }
                return true;
            }
            return super.onTouchEvent(event);
        }

        public void setRating(int stars) {
            currentRating = Math.max(0, Math.min(NUM_STARS, stars));
            invalidate();
        }

        public int getRating() { return currentRating; }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            setAlpha(enabled ? 1f : 0.5f);
        }

        public void setOnRatingChangeListener(OnRatingChangeListener l) {
            this.listener = l;
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Fragment
    // ══════════════════════════════════════════════════════════════════════

    public static PuzzleDialogFragment newInstance(Puzzle puzzle) {
        PuzzleDialogFragment fragment = new PuzzleDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PUZZLE, puzzle);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            puzzle = (Puzzle) getArguments().getSerializable(ARG_PUZZLE);
        }

        rankingRepositorio = new RankingRepositorio();

        // ── Raíz con scroll ───────────────────────────────────────────────
        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(24), dp(24), dp(24), dp(24));
        scroll.addView(layout);

        // ── Imagen + Título superpuesto ───────────────────────────────────
        android.widget.FrameLayout imagenFrame = new android.widget.FrameLayout(requireContext());
        LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(200));
        frameParams.bottomMargin = dp(16);
        imagenFrame.setLayoutParams(frameParams);

        // Recorte redondeado para el frame entero
        android.graphics.drawable.GradientDrawable frameBg =
                new android.graphics.drawable.GradientDrawable();
        frameBg.setCornerRadius(dp(12));
        frameBg.setColor(Color.parseColor("#F5F5F5"));
        imagenFrame.setBackground(frameBg);
        imagenFrame.setClipToOutline(true);
        imagenFrame.setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(View view, android.graphics.Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), dp(12));
            }
        });

        ImageView imagen = new ImageView(requireContext());
        imagen.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);

        String url = puzzle.getImagenUrl();
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(imagen);
        } else {
            imagen.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Degradado oscuro en la parte inferior para legibilidad del título
        android.view.View gradientOverlay = new android.view.View(requireContext());
        android.widget.FrameLayout.LayoutParams gradParams =
                new android.widget.FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(80));
        gradParams.gravity = Gravity.BOTTOM;
        gradientOverlay.setLayoutParams(gradParams);
        android.graphics.drawable.GradientDrawable grad =
                new android.graphics.drawable.GradientDrawable(
                        android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{0xCC000000, 0x00000000});
        gradientOverlay.setBackground(grad);

        // Título superpuesto en la parte inferior de la imagen
        TextView titulo = new TextView(requireContext());
        titulo.setText(puzzle.getTitulo());
        titulo.setTextSize(19);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.WHITE);
        titulo.setShadowLayer(4f, 0f, 2f, 0x99000000);
        android.widget.FrameLayout.LayoutParams tituloParams =
                new android.widget.FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        tituloParams.gravity = Gravity.BOTTOM;
        tituloParams.setMargins(dp(14), 0, dp(14), dp(10));
        titulo.setLayoutParams(tituloParams);
        titulo.setMaxLines(2);
        titulo.setEllipsize(android.text.TextUtils.TruncateAt.END);

        imagenFrame.addView(imagen);
        imagenFrame.addView(gradientOverlay);
        imagenFrame.addView(titulo);

        // ── Tarjeta de datos ──────────────────────────────────────────────
        LinearLayout cardDatos = crearTarjeta();

        cardDatos.addView(crearFila("👤 Autor",       puzzle.getAutor()));
        cardDatos.addView(crearFila("📋 Descripción", puzzle.getDescripcion()));
        cardDatos.addView(crearFila("⚡ Dificultad",   String.valueOf(puzzle.getDificultad())));
        cardDatos.addView(crearFila("🧩 Piezas",      String.valueOf(puzzle.getPiezas())));
        cardDatos.addView(crearFila("⏱️ Tiempo",       puzzle.getTiempo() + " horas"));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(16);
        cardDatos.setLayoutParams(cardParams);

        // ── Tarjeta de valoración ─────────────────────────────────────────
        LinearLayout cardValoracion = crearTarjeta();
        cardValoracion.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams cardValParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardValParams.bottomMargin = dp(20);
        cardValoracion.setLayoutParams(cardValParams);

        TextView labelValorar = new TextView(requireContext());
        labelValorar.setText("Valorar este puzzle");
        labelValorar.setTextSize(14);
        labelValorar.setTypeface(null, Typeface.BOLD);
        labelValorar.setTextColor(Color.parseColor("#616161"));
        labelValorar.setPadding(0, 0, 0, dp(10));

        RoundedStarRatingView starView = new RoundedStarRatingView(requireContext());
        LinearLayout.LayoutParams starParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        starParams.bottomMargin = dp(8);
        starView.setLayoutParams(starParams);

        TextView tvEstado = new TextView(requireContext());
        tvEstado.setText("Toca las estrellas para valorar");
        tvEstado.setTextSize(12);
        tvEstado.setTextColor(Color.parseColor("#9E9E9E"));
        tvEstado.setPadding(0, dp(4), 0, 0);

        starView.setOnRatingChangeListener((stars, fromUser) -> {
            if (!fromUser) return;

            String token      = GestorSesion.obtenerToken(requireContext());
            int    idUsuario  = GestorSesion.obtenerId_usuario(requireContext());

            if (puzzle.getIdUsuario() != null && puzzle.getIdUsuario().equals(idUsuario)) {
                Toast.makeText(requireContext(),
                        "No puedes valorar tu propio puzzle",
                        Toast.LENGTH_SHORT).show();
                starView.setRating(0);
                return;
            }

            tvEstado.setText("Enviando valoración…");
            tvEstado.setTextColor(Color.parseColor("#9E9E9E"));
            starView.setEnabled(false);

            rankingRepositorio.valorarPuzzle(
                    token,
                    puzzle.getId(),
                    idUsuario,
                    stars,
                    new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call,
                                               @NonNull Response<Void> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful()) {
                                puzzle.setValoracion(stars);
                                tvEstado.setText("✅ Valorado con " + stars + " estrella" + (stars != 1 ? "s" : ""));
                                tvEstado.setTextColor(Color.parseColor("#43A047"));
                                Toast.makeText(requireContext(),
                                        "Has valorado con " + stars + " estrellas",
                                        Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 409) {
                                tvEstado.setText("Ya valoraste este puzzle");
                                tvEstado.setTextColor(Color.parseColor("#FB8C00"));
                                starView.setEnabled(false);
                                Toast.makeText(requireContext(),
                                        "Ya has valorado este puzzle",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                tvEstado.setText("Error al valorar. Inténtalo de nuevo");
                                tvEstado.setTextColor(Color.parseColor("#E53935"));
                                starView.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call,
                                              @NonNull Throwable t) {
                            if (!isAdded()) return;
                            tvEstado.setText("Sin conexión. Inténtalo de nuevo");
                            tvEstado.setTextColor(Color.parseColor("#E53935"));
                            starView.setEnabled(true);
                            Toast.makeText(requireContext(),
                                    "Error de red",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        cardValoracion.addView(labelValorar);
        cardValoracion.addView(starView);
        cardValoracion.addView(tvEstado);

        // ── Botón cerrar ──────────────────────────────────────────────────
        Button cerrar = new Button(requireContext());
        cerrar.setText("Cerrar");
        cerrar.setAllCaps(false);
        cerrar.setTextSize(15);
        cerrar.setTypeface(null, Typeface.BOLD);
        cerrar.setTextColor(Color.WHITE);

        android.graphics.drawable.GradientDrawable btnBg =
                new android.graphics.drawable.GradientDrawable();
        btnBg.setCornerRadius(dp(10));
        btnBg.setColor(Color.parseColor("#5C6BC0"));
        cerrar.setBackground(btnBg);
        cerrar.setPadding(dp(16), dp(12), dp(16), dp(12));

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cerrar.setLayoutParams(btnParams);
        cerrar.setOnClickListener(v -> dismiss());

        // ── Montaje ───────────────────────────────────────────────────────
        layout.addView(imagenFrame);
        layout.addView(cardDatos);
        layout.addView(cardValoracion);
        layout.addView(cerrar);

        return scroll;
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────

    /** Crea una tarjeta blanca con esquinas redondeadas y sombra suave. */
    private LinearLayout crearTarjeta() {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));

        android.graphics.drawable.GradientDrawable bg =
                new android.graphics.drawable.GradientDrawable();
        bg.setCornerRadius(dp(14));
        bg.setColor(Color.WHITE);
        bg.setStroke(dp(1), Color.parseColor("#EEEEEE"));
        card.setBackground(bg);

        card.setElevation(dp(2));
        return card;
    }

    /** Crea una fila etiqueta + valor dentro de una tarjeta. */
    private LinearLayout crearFila(String etiqueta, String valor) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams filaParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        filaParams.bottomMargin = dp(6);
        fila.setLayoutParams(filaParams);

        TextView tvLabel = new TextView(requireContext());
        tvLabel.setText(etiqueta + ": ");
        tvLabel.setTypeface(null, Typeface.BOLD);
        tvLabel.setTextColor(Color.parseColor("#424242"));
        tvLabel.setTextSize(14);

        TextView tvValor = new TextView(requireContext());
        tvValor.setText(valor != null ? valor : "—");
        tvValor.setTextColor(Color.parseColor("#616161"));
        tvValor.setTextSize(14);
        LinearLayout.LayoutParams valorParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tvValor.setLayoutParams(valorParams);

        fila.addView(tvLabel);
        fila.addView(tvValor);
        return fila;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // Fondo transparente para que se vea el redondeo de la tarjeta raíz
            getDialog().getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }
    }

    private int dp(int v) {
        return (int) (v * requireContext().getResources().getDisplayMetrics().density);
    }
}