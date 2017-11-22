package com.example.gabri.saudeperto.fragmentos;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.example.gabri.saudeperto.R;
import com.example.gabri.saudeperto.adaptadores.EstabelecimentosAdapter;
import com.example.gabri.saudeperto.atividades.MainActivity;
import com.example.gabri.saudeperto.dados.FavoritosDAO;
import com.example.gabri.saudeperto.modelos.Estabelecimento;
import com.example.gabri.saudeperto.utils.LocalizacaoHelper;

import java.util.Collections;
import java.util.List;

public class FavoritosFragment extends Fragment implements FavoritosDAO.FavoritosListener, LocalizacaoHelper.LocalizacaoListener {

    private MainActivity mMainActivity;
    private ProgressBar mInicioProgressBar;
    private LinearLayout mListaVazia;
    private EstabelecimentosAdapter mAdapter;
    private Location mLocalizacao;
    private List<Estabelecimento> mEstabelecimentos;

    private static final String TAG = "FavoritosFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view, container, false);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setEnabled(false);

        mInicioProgressBar = (ProgressBar) view.findViewById(R.id.inicio_progress_bar);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mListaVazia = (LinearLayout) view.findViewById(R.id.lista_vazia);

        mAdapter = new EstabelecimentosAdapter(mMainActivity);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mMainActivity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration separador = new DividerItemDecoration(mMainActivity, DividerItemDecoration.VERTICAL);
        separador.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.separador_lista));

        recyclerView.addItemDecoration(separador);

        mMainActivity.addLocalizacaoListener(this);
        FavoritosDAO.getInstance().setFavoritosListener(this);

        return view;
    }

    @Override
    public void onLocalizacaoChanged(Location localizacao) {
        mLocalizacao = localizacao;
        atualizarEstabelecimentos();
    }

    @Override
    public void onFavoritosChanged(List<Estabelecimento> estabelecimentos) {
        mEstabelecimentos = estabelecimentos;
        atualizarEstabelecimentos();
    }

    private void atualizarEstabelecimentos() {
        if (mEstabelecimentos == null) return;

        mListaVazia.setVisibility(mEstabelecimentos.size() > 0 ? View.GONE : View.VISIBLE);

        if (mLocalizacao != null) {
            Collections.sort(mEstabelecimentos, (est1, est2) ->
                    est1.getDistancia(mLocalizacao.getLatitude(), mLocalizacao.getLongitude())
                    .compareTo(est2.getDistancia(mLocalizacao.getLatitude(), mLocalizacao.getLongitude())));
        }

        mAdapter.setFragmentClass(FavoritosFragment.class);
        mAdapter.atualizar(mLocalizacao, mEstabelecimentos);
        mInicioProgressBar.setVisibility(View.GONE);
    }
}
