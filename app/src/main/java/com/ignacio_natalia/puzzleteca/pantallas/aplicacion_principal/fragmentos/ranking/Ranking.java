package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.ranking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.ranking.RankingUsuario;
import com.ignacio_natalia.puzzleteca.repositorios.RankingRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment que muestra el ranking diario de usuarios.
 * Se actualiza automáticamente cada INTERVALO_POLLING ms (polling).
 * <p>
 * ── Diseño completamente programático (sin inflate de layouts XML) ──────
 * Top 1-2-3  → tarjeta con gradiente, medalla emoji, RatingBar y donut.
 * Posición 4+ → fila compacta con badge circular, nombre, barra de progreso rosa.
 * <p>
 * La única clase de vista no estándar es DonutProgressView, definida aquí
 * como clase privada estática. No requiere XML ni dependencias externas.
 */
public class Ranking extends Fragment {

    // ── Paleta ────────────────────────────────────────────────────────────
    private static final String C_TITULO    = "#2E7D6E";
    private static final String C_SUBTITULO = "#78909C";
    private static final String C_ROSA      = "#F06292";
    private static final String C_TEAL      = "#26A69A";
    private static final String C_NARANJA   = "#FF8A65";
    private static final String C_NOMBRE    = "#2E4057";
    private static final String C_GRIS      = "#90A4AE";
    private static final String C_BARRA_BG  = "#F8E0E8";
    private static final String C_ESTRELLA  = "#FDD835";
    private static final String C_LINEA     = "#4DB6AC";

    // Fondos tarjeta Top
    private static final String[] BG_TOP    = {"#FFF0F4", "#F0FAFA", "#FFF8F0"};
    private static final String[] BORDE_TOP = {"#F48FB1", "#80CBC4", "#FFAB40"};
    private static final String[] COLOR_POS = {C_ROSA, C_TEAL, C_NARANJA};
    private static final int[]    MEDALLAS  = {R.drawable.top1, R.drawable.top2, R.drawable.top3};

    // ── Polling ───────────────────────────────────────────────────────────
    private static final long INTERVALO_POLLING = 10_000L;
    private final Handler  handler = new Handler(Looper.getMainLooper());
    private final Runnable poller  = new Runnable() {
        @Override public void run() {
            cargarRanking();
            handler.postDelayed(this, INTERVALO_POLLING);
        }
    };

    // ── Vistas ────────────────────────────────────────────────────────────
    private RecyclerView recyclerView;
    private ProgressBar  progressBar;
    private TextView     tvVacio;

    // ── Datos ─────────────────────────────────────────────────────────────
    private RankingAdapter    adapter;
    private RankingRepositorio repositorio;

    // ═════════════════════════════════════════════════════════════════════
    //  Ciclo de vida
    // ═════════════════════════════════════════════════════════════════════

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        repositorio = new RankingRepositorio();

        // ── Layout raíz ────────────────────────────────────────────────
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);

        // ── Cabecera ────────────────────────────────────────────────────
        root.addView(crearCabecera());

        // ── ProgressBar ────────────────────────────────────────────────
        progressBar = new ProgressBar(requireContext());
        LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        pbParams.gravity = Gravity.CENTER_HORIZONTAL;
        pbParams.topMargin = dp(20);
        progressBar.setLayoutParams(pbParams);
        progressBar.setVisibility(View.GONE);
        root.addView(progressBar);

        // ── Mensaje lista vacía ─────────────────────────────────────────
        tvVacio = new TextView(requireContext());
        tvVacio.setText("Aún no hay valoraciones hoy.\n¡Valora los puzzles de la comunidad!");
        tvVacio.setTextSize(15);
        tvVacio.setGravity(Gravity.CENTER);
        tvVacio.setTextColor(Color.parseColor(C_GRIS));
        tvVacio.setPadding(dp(24), dp(40), dp(24), 0);
        tvVacio.setVisibility(View.GONE);
        root.addView(tvVacio);

        // ── RecyclerView ────────────────────────────────────────────────
        recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RankingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setPadding(dp(14), dp(8), dp(14), dp(16));
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        LinearLayout.LayoutParams rvParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        recyclerView.setLayoutParams(rvParams);
        root.addView(recyclerView);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(poller);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(poller);
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Cabecera:  ────── Ranking Diario ──────
    //                   Valoraciones de hoy
    // ═════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearCabecera() {
        LinearLayout cont = new LinearLayout(requireContext());
        cont.setOrientation(LinearLayout.VERTICAL);
        cont.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, dp(0), 0, dp(10));
        cont.setLayoutParams(p);

        // Fila con líneas laterales
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fp.setMargins(dp(20), 0, dp(20), 0);
        fila.setLayoutParams(fp);

        View lineaL = lineaDecorativa();
        View lineaR = lineaDecorativa();

        TextView tvTitulo = new TextView(requireContext());
        tvTitulo.setText("Valoraciones de hoy");
        tvTitulo.setTextSize(22);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(Color.parseColor(C_TITULO));
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tp.setMargins(dp(14), 0, dp(14), 0);
        tvTitulo.setLayoutParams(tp);

        fila.addView(lineaL);
        fila.addView(tvTitulo);
        fila.addView(lineaR);
        cont.addView(fila);

//        TextView tvSub = new TextView(requireContext());
//        tvSub.setText("Valoraciones de hoy");
//        tvSub.setTextSize(12);
//        tvSub.setTextColor(Color.parseColor(C_SUBTITULO));
//        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        sp.gravity = Gravity.CENTER_HORIZONTAL;
//        sp.topMargin = dp(3);
//        tvSub.setLayoutParams(sp);
//        cont.addView(tvSub);

        return cont;
    }

    private View lineaDecorativa() {
        View v = new View(requireContext());
        v.setBackgroundColor(Color.parseColor(C_LINEA));
        v.setAlpha(0.45f);
        v.setLayoutParams(new LinearLayout.LayoutParams(0, dp(1), 1f));
        return v;
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Carga de datos
    // ═════════════════════════════════════════════════════════════════════

    private void cargarRanking() {
        if (!isAdded()) return;
        String token = GestorSesion.obtenerToken(requireContext());

        if (adapter.getItemCount() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            tvVacio.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }

        repositorio.obtenerRankingDiario(token, new Callback<List<RankingUsuario>>() {
            @Override
            public void onResponse(@NonNull Call<List<RankingUsuario>> call,
                                   @NonNull Response<List<RankingUsuario>> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    adapter.actualizar(response.body());
                    recyclerView.setVisibility(View.VISIBLE);
                    tvVacio.setVisibility(View.GONE);
                } else {
                    adapter.actualizar(new ArrayList<>());
                    recyclerView.setVisibility(View.GONE);
                    tvVacio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RankingUsuario>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (adapter.getItemCount() == 0) {
                    tvVacio.setText("Error de conexión. Reintentando…");
                    tvVacio.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Adapter  (dos tipos de ítem: PODIUM y NORMAL)
    // ═════════════════════════════════════════════════════════════════════

    private class RankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_PODIUM = 0;   // fila única con los 3 primeros
        private static final int TYPE_NORMAL = 1;   // posiciones 4+

        private final List<RankingUsuario> datos;

        RankingAdapter(List<RankingUsuario> datos) {
            this.datos = datos;
        }

        void actualizar(List<RankingUsuario> nuevos) {
            datos.clear();
            datos.addAll(nuevos);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (datos.isEmpty()) return 0;
            // El podio (top 1-3) ocupa 1 sola celda; el resto son ítems individuales
            int rest = Math.max(datos.size() - 3, 0);
            return 1 + rest;
        }

        @Override
        public int getItemViewType(int pos) {
            return pos == 0 ? TYPE_PODIUM : TYPE_NORMAL;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int tipo) {
            return tipo == TYPE_PODIUM
                    ? new VHPodium(crearItemPodium(parent))
                    : new VHNormal(crearItemNormal(parent));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
            if (h instanceof VHPodium) {
                bindPodium((VHPodium) h);
            } else {
                // posición real en datos: el podio ocupa slot 0, así que
                // posición 1 del RecyclerView → datos[3], posición 2 → datos[4], etc.
                int dataIdx = position - 1 + 3;
                RankingUsuario u = datos.get(dataIdx);
                int pos = dataIdx + 1;
                bindNormal((VHNormal) h, u, pos);
            }
        }

        // ─────────────────────────────────────────────────────────────
        //  Bind Podium — rellena las 3 tarjetas (orden visual: 2°|1°|3°)
        // ─────────────────────────────────────────────────────────────

        @SuppressLint("SetTextI18n")
        private void bindPodium(VHPodium h) {
            // Orden visual: columna izq=2°, columna centro=1°, columna der=3°
            // dataIdx: datos[0]=1°, datos[1]=2°, datos[2]=3°
            int[]       dataIdxs   = {1, 0, 2};          // qué dato va en cada columna
            int[]       paletaIdxs = {1, 0, 2};          // índice en arrays de paleta
            LinearLayout[] cards   = {h.card2, h.card1, h.card3};

            // Pedestal: 1° sin margen extra, 2° baja 20dp, 3° baja 36dp
            int[] pedestalDp = {20, 0, 36};              // top margin por columna

            for (int col = 0; col < 3; col++) {
                int dataIdx   = dataIdxs[col];
                int pi        = paletaIdxs[col];
                LinearLayout card = cards[col];

                if (dataIdx >= datos.size()) {
                    card.setVisibility(View.INVISIBLE);
                    continue;
                }
                card.setVisibility(View.VISIBLE);

                RankingUsuario u   = datos.get(dataIdx);
                int posNum         = dataIdx + 1;        // 1, 2 ó 3

                if (posNum == 1) {
                    card.setScaleX(1.08f);
                    card.setScaleY(1.08f);
                    card.setElevation(dp(6));
                }

                // Fondo tarjeta
                GradientDrawable fondo = new GradientDrawable();
                fondo.setColor(Color.parseColor(BG_TOP[pi]));
                fondo.setCornerRadius(dp(20));
                fondo.setStroke(dp(2), Color.parseColor(BORDE_TOP[pi]));
                card.setBackground(fondo);

                // Círculo de medalla
                View viewCirculo = card.findViewWithTag("medallaCirculo_" + posNum);
                GradientDrawable circ = new GradientDrawable();
                circ.setShape(GradientDrawable.OVAL);
                circ.setColor(Color.parseColor(COLOR_POS[pi]));
                circ.setAlpha(200);
                viewCirculo.setBackground(circ);

                // Medalla
                ImageView ivMedalla = card.findViewWithTag("medalla_" + posNum);
                ivMedalla.setImageResource(MEDALLAS[pi]);

                // Posición
                TextView tvPos = card.findViewWithTag("posicion_" + posNum);
                tvPos.setText(String.valueOf(posNum));
                tvPos.setTextColor(Color.parseColor(COLOR_POS[pi]));

                // Nombre
                TextView tvNombre = card.findViewWithTag("nombre_" + posNum);
                tvNombre.setText(u.getNombre() + " " + u.getApellido());

                // Rating
                float media = safe(u.getMediaDiaria());
                RatingBar rb = card.findViewWithTag("rating_" + posNum);
                rb.setRating(Math.min(media, 5f));

                // Donut
                float pct    = media / 5f;
                int   pctInt = Math.round(pct * 100);
                int colorArco = Color.parseColor(COLOR_POS[pi]);

                DonutProgressView donut = card.findViewWithTag("donutRing_" + posNum);
                donut.setPorcentaje(pct);
                donut.setColorArco(colorArco);

                TextView tvPct = card.findViewWithTag("porcentaje_" + posNum);
                tvPct.setText(pctInt + "%");
                tvPct.setTextColor(colorArco);

                // Total y media
                long total = u.getTotalValoraciones() != null ? u.getTotalValoraciones() : 0L;
                TextView tvTotal = card.findViewWithTag("total_" + posNum);
                tvTotal.setText(formatNum(total) + " valoraciones");

                TextView tvMedia = card.findViewWithTag("media_" + posNum);
                tvMedia.setText(String.format(Locale.getDefault(), "Media: %.2f ⭐", media));

                // Pedestal: ajustamos el margen superior de cada tarjeta
                card.setTranslationY(dp(pedestalDp[col]));
            }
        }

        // ─────────────────────────────────────────────────────────────
        //  Bind Normal (pos ≥ 4)
        // ─────────────────────────────────────────────────────────────

        private void bindNormal(VHNormal h, RankingUsuario u, int pos) {

            // Badge circular teal
            GradientDrawable badge = new GradientDrawable();
            badge.setShape(GradientDrawable.OVAL);
            badge.setColor(Color.parseColor(C_TEAL));
            h.viewBadge.setBackground(badge);

            h.tvPosicion.setText(String.valueOf(pos));
            h.tvNombre.setText(u.getNombre() + " " + u.getApellido());

            float media  = safe(u.getMediaDiaria());
            float pct    = media / 5f;
            int   pctInt = Math.round(pct * 100);

            h.ratingBar.setRating(Math.min(media, 5f));
            h.tvPorcentaje.setText(pctInt + "%");

            final float pctFinal = pct;
            h.barraPista.post(() -> {
                int total    = h.barraPista.getWidth();
                int minWidth = dp(h.barraPista, 4);
                int fill     = Math.max(Math.round(total * pctFinal), minWidth);
                ViewGroup.LayoutParams lp = h.barraFill.getLayoutParams();
                lp.width = fill;
                h.barraFill.setLayoutParams(lp);
            });

            long totalVal = u.getTotalValoraciones() != null ? u.getTotalValoraciones() : 0L;
            h.tvTotal.setText(String.format(Locale.getDefault(),
                    "%s valoraciones · Media: %.2f ⭐", formatNum(totalVal), media));
        }

        // ─────────────────────────────────────────────────────────────
        //  Construcción de vista — PODIO (fila horizontal 2°|1°|3°)
        // ─────────────────────────────────────────────────────────────

        private View crearItemPodium(ViewGroup parent) {

            // Contenedor horizontal; alineado por abajo → efecto pedestal
            LinearLayout row = new LinearLayout(parent.getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.BOTTOM);
            RecyclerView.LayoutParams rp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rp.setMargins(dp(4), dp(4), dp(4), dp(50));
            row.setLayoutParams(rp);

            // Orden visual: 2° | 1° | 3°
            int[]   posiciones = {2, 1, 3};
            float[] pesos      = {1f, 1.1f, 1f};   // columna central ligeramente más ancha

            for (int col = 0; col < 3; col++) {
                int posNum = posiciones[col];
                LinearLayout card = crearTarjetaTop(parent.getContext(), posNum);

                LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, pesos[col]);
                cp.setMargins(dp(3), 0, dp(3), 0);
                card.setLayoutParams(cp);
                card.setTag("podio_" + posNum);
                row.addView(card);
            }

            return row;
        }

        // ── Tarjeta individual del podio ───────────────────────────────

        private LinearLayout crearTarjetaTop(Context ctx, int posNum) {

            LinearLayout card = new LinearLayout(ctx);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(Gravity.CENTER_HORIZONTAL);
            card.setPadding(dp(6), dp(10), dp(6), dp(12));
            card.setTranslationZ(dp(2));

            // ── Círculo + medalla ──────────────────────────────────────
            FrameLayout frameMedalla = new FrameLayout(ctx);
            LinearLayout.LayoutParams fmp = new LinearLayout.LayoutParams(dp(50), dp(50));
            fmp.setMargins(0, 0, 0, dp(4));
            frameMedalla.setLayoutParams(fmp);

            View viewCirculo = new View(ctx);
            viewCirculo.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            viewCirculo.setTag("medallaCirculo_" + posNum);

            ImageView ivMedalla = new ImageView(ctx);
            ivMedalla.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            ivMedalla.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ivMedalla.setTag("medalla_" + posNum);

            ivMedalla.setScaleX(1.5f);
            ivMedalla.setScaleY(1.5f);
            ivMedalla.setClipToOutline(false);

            frameMedalla.addView(viewCirculo);
            frameMedalla.addView(ivMedalla);
            card.addView(frameMedalla);

            // ── Fila posición + nombre ─────────────────────────────────
            LinearLayout filaNombre = new LinearLayout(ctx);
            filaNombre.setOrientation(LinearLayout.HORIZONTAL);
            filaNombre.setGravity(Gravity.CENTER);
            filaNombre.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView tvPos = new TextView(ctx);
            tvPos.setTextSize(18);
            tvPos.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pp.setMargins(0, 0, dp(4), 0);
            tvPos.setLayoutParams(pp);
            tvPos.setTag("posicion_" + posNum);

            TextView tvNombre = new TextView(ctx);
            tvNombre.setTextSize(12);
            tvNombre.setTypeface(null, Typeface.BOLD);
            tvNombre.setTextColor(Color.parseColor(C_NOMBRE));
            tvNombre.setMaxLines(1);
            tvNombre.setEllipsize(TextUtils.TruncateAt.END);
            tvNombre.setTag("nombre_" + posNum);

            filaNombre.addView(tvPos);
            filaNombre.addView(tvNombre);
            card.addView(filaNombre);

            // ── RatingBar ──────────────────────────────────────────────
            RatingBar rb = new RatingBar(ctx, null, android.R.attr.ratingBarStyleSmall);
            rb.setNumStars(5);
            rb.setStepSize(0.5f);
            rb.setIsIndicator(true);
            rb.getProgressDrawable().setColorFilter(
                    Color.parseColor(C_ESTRELLA), PorterDuff.Mode.SRC_IN);
            LinearLayout.LayoutParams rbp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rbp.setMargins(0, dp(3), 0, dp(6));
            rb.setLayoutParams(rbp);
            rb.setTag("rating_" + posNum);
            card.addView(rb);

            // ── Donut ──────────────────────────────────────────────────
            FrameLayout frameDonut = new FrameLayout(ctx);
            LinearLayout.LayoutParams dp80 = new LinearLayout.LayoutParams(dp(78), dp(78));
            dp80.setMargins(0, dp(4), 0, dp(4));
            frameDonut.setLayoutParams(dp80);

            DonutProgressView donut = new DonutProgressView(ctx);
            donut.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            donut.setTag("donutRing_" + posNum);

            TextView tvPct = new TextView(ctx);
            tvPct.setTextSize(15);
            tvPct.setTypeface(null, Typeface.BOLD);
            tvPct.setGravity(Gravity.CENTER);
            tvPct.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            tvPct.setTag("porcentaje_" + posNum);

            frameDonut.addView(donut);
            frameDonut.addView(tvPct);
            card.addView(frameDonut);

            // ── Total valoraciones ─────────────────────────────────────
            TextView tvTotal = new TextView(ctx);
            tvTotal.setTextSize(10);
            tvTotal.setTextColor(Color.parseColor(C_GRIS));
            tvTotal.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tp.setMargins(0, dp(2), 0, 0);
            tvTotal.setLayoutParams(tp);
            tvTotal.setTag("total_" + posNum);
            card.addView(tvTotal);

            // ── Media numérica ─────────────────────────────────────────
            TextView tvMedia = new TextView(ctx);
            tvMedia.setTextSize(10);
            tvMedia.setTextColor(Color.parseColor(C_GRIS));
            tvMedia.setGravity(Gravity.CENTER_HORIZONTAL);
            tvMedia.setTag("media_" + posNum);
            card.addView(tvMedia);

            return card;
        }

        // ─────────────────────────────────────────────────────────────
        //  Construcción de vista — ÍTEM NORMAL (posiciones 4+)
        // ─────────────────────────────────────────────────────────────

        private View crearItemNormal(ViewGroup parent) {

            LinearLayout card = new LinearLayout(parent.getContext());
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(14), dp(14), dp(16), dp(14));
            card.setElevation(dp(3));
            RecyclerView.LayoutParams rp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rp.setMargins(dp(2), dp(2), dp(2), dp(6));
            card.setLayoutParams(rp);
            GradientDrawable fondoCard = new GradientDrawable();
            fondoCard.setColor(Color.WHITE);
            fondoCard.setCornerRadius(dp(100));
            fondoCard.setStroke(dp(1), Color.parseColor("#E8EDF2"));
            card.setBackground(fondoCard);

            // ── Badge circular con número de posición ───────────────────
            FrameLayout frameBadge = new FrameLayout(parent.getContext());
            LinearLayout.LayoutParams bfp = new LinearLayout.LayoutParams(dp(44), dp(44));
            bfp.setMargins(0, 0, dp(14), 0);
            frameBadge.setLayoutParams(bfp);

            View viewBadge = new View(parent.getContext());
            viewBadge.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            viewBadge.setTag("badge");

            TextView tvPos = new TextView(parent.getContext());
            tvPos.setTextSize(18);
            tvPos.setTypeface(null, Typeface.BOLD);
            tvPos.setTextColor(Color.WHITE);
            tvPos.setGravity(Gravity.CENTER);
            tvPos.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            tvPos.setTag("posicion");

            frameBadge.addView(viewBadge);
            frameBadge.addView(tvPos);
            card.addView(frameBadge);

            // ── Bloque central (columna vertical) ──────────────────────
            LinearLayout centro = new LinearLayout(parent.getContext());
            centro.setOrientation(LinearLayout.VERTICAL);
            centro.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            // Nombre
            TextView tvNombre = new TextView(parent.getContext());
            tvNombre.setTextSize(15);
            tvNombre.setTypeface(null, Typeface.BOLD);
            tvNombre.setTextColor(Color.parseColor(C_NOMBRE));
            tvNombre.setMaxLines(1);
            tvNombre.setEllipsize(TextUtils.TruncateAt.END);
            tvNombre.setTag("nombre");
            centro.addView(tvNombre);

            // Fila: RatingBar + barra de progreso
            LinearLayout filaBar = new LinearLayout(parent.getContext());
            filaBar.setOrientation(LinearLayout.HORIZONTAL);
            filaBar.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams fbp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fbp.setMargins(0, dp(5), 0, 0);
            filaBar.setLayoutParams(fbp);

            RatingBar rb = new RatingBar(parent.getContext(), null, android.R.attr.ratingBarStyleSmall);
            rb.setNumStars(5);
            rb.setStepSize(0.5f);
            rb.setIsIndicator(true);
            rb.getProgressDrawable().setColorFilter(
                    Color.parseColor(C_ESTRELLA), PorterDuff.Mode.SRC_IN);
            LinearLayout.LayoutParams rbp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rbp.setMargins(0, 0, dp(10), 0);
            rb.setLayoutParams(rbp);
            rb.setTag("rating");
            filaBar.addView(rb);

            // Pista de la barra
            FrameLayout barraPista = new FrameLayout(parent.getContext());
            GradientDrawable pistaDraw = new GradientDrawable();
            pistaDraw.setColor(Color.parseColor(C_BARRA_BG));
            pistaDraw.setCornerRadius(dp(8));
            barraPista.setBackground(pistaDraw);
            barraPista.setLayoutParams(new LinearLayout.LayoutParams(0, dp(10), 1f));
            barraPista.setTag("barraPista");

            // Relleno de la barra
            View barraFill = new View(parent.getContext());
            GradientDrawable fillDraw = new GradientDrawable();
            fillDraw.setColor(Color.parseColor(C_ROSA));
            fillDraw.setCornerRadius(dp(8));
            barraFill.setBackground(fillDraw);
            FrameLayout.LayoutParams ffp = new FrameLayout.LayoutParams(
                    dp(10), FrameLayout.LayoutParams.MATCH_PARENT);
            ffp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            barraFill.setLayoutParams(ffp);
            barraFill.setTag("barraFill");

            barraPista.addView(barraFill);
            filaBar.addView(barraPista);
            centro.addView(filaBar);

            // Total + media
            TextView tvTotal = new TextView(parent.getContext());
            tvTotal.setTextSize(11);
            tvTotal.setTextColor(Color.parseColor(C_GRIS));
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tp.setMargins(0, dp(3), 0, 0);
            tvTotal.setLayoutParams(tp);
            tvTotal.setTag("total");
            centro.addView(tvTotal);
            card.addView(centro);

            // ── Porcentaje a la derecha ─────────────────────────────────
            TextView tvPct = new TextView(parent.getContext());
            tvPct.setTextSize(14);
            tvPct.setTypeface(null, Typeface.BOLD);
            tvPct.setTextColor(Color.parseColor(C_ROSA));
            LinearLayout.LayoutParams pctP = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pctP.setMargins(dp(8), 0, 0, 0);
            tvPct.setLayoutParams(pctP);
            tvPct.setTag("porcentaje");
            card.addView(tvPct);

            return card;
        }

        // ─────────────────────────────────────────────────────────────
        //  ViewHolders
        // ─────────────────────────────────────────────────────────────

        class VHPodium extends RecyclerView.ViewHolder {
            LinearLayout card1, card2, card3;   // slot 1°, 2°, 3°

            VHPodium(View v) {
                super(v);
                card1 = v.findViewWithTag("podio_1");
                card2 = v.findViewWithTag("podio_2");
                card3 = v.findViewWithTag("podio_3");
            }
        }

        class VHNormal extends RecyclerView.ViewHolder {
            View     viewBadge, barraFill, barraPista;
            TextView tvPosicion, tvNombre, tvPorcentaje, tvTotal;
            RatingBar ratingBar;

            VHNormal(View v) {
                super(v);
                viewBadge   = v.findViewWithTag("badge");
                tvPosicion  = v.findViewWithTag("posicion");
                tvNombre    = v.findViewWithTag("nombre");
                ratingBar   = v.findViewWithTag("rating");
                barraPista  = v.findViewWithTag("barraPista");
                barraFill   = v.findViewWithTag("barraFill");
                tvPorcentaje = v.findViewWithTag("porcentaje");
                tvTotal     = v.findViewWithTag("total");
            }
        }

        // ─────────────────────────────────────────────────────────────
        //  Helpers
        // ─────────────────────────────────────────────────────────────

        private float safe(Double d) {
            return d != null ? d.floatValue() : 0f;
        }

        private String formatNum(long n) {
            return String.format(Locale.getDefault(), "%,d", n).replace(',', '.');
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Vista interna: DonutProgressView
    //  Dibuja un anillo de progreso con Canvas. Sin XML, sin dependencias.
    // ═════════════════════════════════════════════════════════════════════

    private static class DonutProgressView extends View {

        private final android.graphics.Paint pistaPaint =
                new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        private final android.graphics.Paint arcoPaint  =
                new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        private android.graphics.RectF rect;
        private float porcentaje = 0f;

        DonutProgressView(android.content.Context ctx) {
            super(ctx);
            float grosor = 13f * ctx.getResources().getDisplayMetrics().density;

            pistaPaint.setColor(Color.parseColor("#E8ECF0"));
            pistaPaint.setStyle(android.graphics.Paint.Style.STROKE);
            pistaPaint.setStrokeWidth(grosor);
            pistaPaint.setStrokeCap(android.graphics.Paint.Cap.ROUND);

            arcoPaint.setColor(Color.parseColor(C_ROSA));
            arcoPaint.setStyle(android.graphics.Paint.Style.STROKE);
            arcoPaint.setStrokeWidth(grosor);
            arcoPaint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
        }

        void setPorcentaje(float pct) {
            this.porcentaje = Math.max(0f, Math.min(1f, pct));
            invalidate();
        }

        void setColorArco(int color) {
            arcoPaint.setColor(color);
            invalidate();
        }

        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
            float m = pistaPaint.getStrokeWidth() / 2f + 2f;
            rect = new android.graphics.RectF(m, m, w - m, h - m);
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            if (rect == null) return;
            canvas.drawArc(rect, 0f,   360f,              false, pistaPaint);
            canvas.drawArc(rect, -90f, 360f * porcentaje, false, arcoPaint);
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Utilidad
    // ═════════════════════════════════════════════════════════════════════

    private int dp(int v) {
        return (int) (v * requireContext().getResources().getDisplayMetrics().density);
    }

    private int dp(View view, int v) {
        return (int) (v * view.getContext().getResources().getDisplayMetrics().density);
    }
}