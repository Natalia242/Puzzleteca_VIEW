package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ignacio_natalia.puzzleteca.modelos.RankingUsuario;
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
 */
public class Ranking extends Fragment {

    // ── Polling ──────────────────────────────────────────────────────────
    private static final long INTERVALO_POLLING = 10_000L; // 10 segundos
    private final Handler  handler  = new Handler(Looper.getMainLooper());
    private final Runnable poller   = new Runnable() {
        @Override public void run() {
            cargarRanking();
            handler.postDelayed(this, INTERVALO_POLLING);
        }
    };

    // ── Vistas ────────────────────────────────────────────────────────────
    private RecyclerView  recyclerView;
    private ProgressBar   progressBar;
    private TextView      tvVacio;
    private ImageView     imgTitulo;

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
        root.setBackgroundColor(Color.parseColor("#F5F5F5"));

        // ── Imagen de título ───────────────────────────────────────────
        imgTitulo = new ImageView(requireContext());
        int resId = requireContext().getResources().getIdentifier(
                "titulo_ranking_diario_recortado", "drawable",
                requireContext().getPackageName());
        if (resId != 0) imgTitulo.setImageResource(resId);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(80));
        imgParams.setMargins(0, dp(12), 0, dp(8));
        imgTitulo.setLayoutParams(imgParams);
        imgTitulo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        root.addView(imgTitulo);

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

        // ── Mensaje lista vacía ────────────────────────────────────────
        tvVacio = new TextView(requireContext());
        tvVacio.setText("Aún no hay valoraciones hoy.\n¡Valora los puzzles de la comunidad!");
        tvVacio.setTextSize(15);
        tvVacio.setGravity(Gravity.CENTER);
        tvVacio.setTextColor(Color.GRAY);
        tvVacio.setPadding(dp(24), dp(40), dp(24), 0);
        tvVacio.setVisibility(View.GONE);
        root.addView(tvVacio);

        // ── RecyclerView ───────────────────────────────────────────────
        recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RankingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        LinearLayout.LayoutParams rvParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        recyclerView.setLayoutParams(rvParams);
        root.addView(recyclerView);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Inicia el polling: primera carga inmediata + repetición cada 10s
        handler.post(poller);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Detiene el polling cuando el fragment no está visible
        handler.removeCallbacks(poller);
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Carga de datos
    // ═════════════════════════════════════════════════════════════════════

    private void cargarRanking() {
        if (!isAdded()) return;

        String token = GestorSesion.obtenerToken(requireContext());

        // Sólo mostramos el spinner en la primera carga (cuando la lista está vacía)
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
                    List<RankingUsuario> lista = response.body();
                    adapter.actualizar(lista);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvVacio.setVisibility(View.GONE);
                } else {
                    // 204 o lista vacía
                    adapter.actualizar(new ArrayList<>());
                    recyclerView.setVisibility(View.GONE);
                    tvVacio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RankingUsuario>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                // Si ya había datos previos, los mantiene; si no, muestra vacío
                if (adapter.getItemCount() == 0) {
                    tvVacio.setText("Error de conexión. Reintentando…");
                    tvVacio.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Adapter + ViewHolder internos
    // ═════════════════════════════════════════════════════════════════════

    private class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.VH> {

        private final List<RankingUsuario> datos;

        RankingAdapter(List<RankingUsuario> datos) { this.datos = datos; }

        void actualizar(List<RankingUsuario> nuevos) {
            datos.clear();
            datos.addAll(nuevos);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(crearItemView(parent));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            RankingUsuario usuario = datos.get(position);
            int pos = position + 1;

            // Medalla para los 3 primeros
            String medalla = pos == 1 ? "🥇 " : pos == 2 ? "🥈 " : pos == 3 ? "🥉 " : pos + ". ";

            holder.tvNombre.setText(medalla + usuario.getNombre() + " " + usuario.getApellido());
            holder.tvMedia.setText(String.format(Locale.getDefault(), "⭐ %.2f", usuario.getMediaDiaria()));
            holder.tvTotal.setText(usuario.getTotalValoraciones() + " valoraciones");

            // Color de fondo alternado
            int bg = position % 2 == 0 ? Color.WHITE : Color.parseColor("#F0F4FF");
            holder.card.setBackgroundColor(bg);
        }

        @Override
        public int getItemCount() { return datos.size(); }

        // ── Item view construido programáticamente ──────────────────────
        private View crearItemView(ViewGroup parent) {
            LinearLayout card = new LinearLayout(parent.getContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(16), dp(14), dp(16), dp(14));
            card.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView tvNombre = new TextView(parent.getContext());
            tvNombre.setTextSize(16);
            tvNombre.setTypeface(null, Typeface.BOLD);
            tvNombre.setTextColor(Color.parseColor("#212121"));
            tvNombre.setTag("nombre");

            LinearLayout fila = new LinearLayout(parent.getContext());
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setPadding(0, dp(4), 0, 0);

            TextView tvMedia = new TextView(parent.getContext());
            tvMedia.setTextSize(14);
            tvMedia.setTextColor(Color.parseColor("#F57C00")); // naranja estrella
            tvMedia.setTag("media");

            TextView tvTotal = new TextView(parent.getContext());
            tvTotal.setTextSize(13);
            tvTotal.setTextColor(Color.GRAY);
            tvTotal.setPadding(dp(16), 0, 0, 0);
            tvTotal.setTag("total");

            fila.addView(tvMedia);
            fila.addView(tvTotal);

            card.addView(tvNombre);
            card.addView(fila);

            // Divisor sutil
            View divisor = new View(parent.getContext());
            divisor.setBackgroundColor(Color.parseColor("#E0E0E0"));
            divisor.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1));
            card.addView(divisor);

            return card;
        }

        class VH extends RecyclerView.ViewHolder {
            LinearLayout card;
            TextView tvNombre, tvMedia, tvTotal;

            VH(View v) {
                super(v);
                card     = (LinearLayout) v;
                tvNombre = v.findViewWithTag("nombre");
                tvMedia  = v.findViewWithTag("media");
                tvTotal  = v.findViewWithTag("total");
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    //  Utilidad
    // ═════════════════════════════════════════════════════════════════════

    private int dp(int v) {
        return (int) (v * requireContext().getResources().getDisplayMetrics().density);
    }
}