package com.example.gabri.saudeperto.fragmentos;

import android.accounts.NetworkErrorException;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gabri.saudeperto.R;
import com.example.gabri.saudeperto.adaptadores.EstabelecimentosAdapter;
import com.example.gabri.saudeperto.atividades.MainActivity;
import com.example.gabri.saudeperto.modelos.Estabelecimento;
import com.example.gabri.saudeperto.servicos.ApiEstabelecimentosInterface;
import com.example.gabri.saudeperto.servicos.TcuApi;
import com.example.gabri.saudeperto.utils.LocalizacaoHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProximosFragment extends Fragment implements LocalizacaoHelper.LocalizacaoListener {

    private MainActivity mMainActivity;
    private ProgressBar mInicioProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EstabelecimentosAdapter mAdapter;
    private Location mLocalizacao;
    private List<Estabelecimento> mEstabelecimentos;
    private ApiEstabelecimentosInterface mServico;

    private static final String ESTABELECIMENTOS = "Estabelecimentos";
    private static final String LOCALIZACAO = "Localização";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
        mServico = TcuApi.getInstance().getServico();
        if (savedInstanceState != null) {
            mEstabelecimentos = (ArrayList<Estabelecimento>) savedInstanceState.getSerializable(ESTABELECIMENTOS);
            mLocalizacao = savedInstanceState.getParcelable(LOCALIZACAO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.azul_claro);
        mSwipeRefreshLayout.setOnRefreshListener(this::atualizarEstabelecimentos);

        mInicioProgressBar = (ProgressBar) view.findViewById(R.id.inicio_progress_bar);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mAdapter = new EstabelecimentosAdapter(mMainActivity);

        if (mEstabelecimentos != null) {
            mAdapter.atualizar(mLocalizacao, mEstabelecimentos);
            mInicioProgressBar.setVisibility(View.GONE);
        }

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mMainActivity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration separador = new DividerItemDecoration(mMainActivity, DividerItemDecoration.VERTICAL);
        separador.setDrawable(ContextCompat.getDrawable(mMainActivity, R.drawable.separador_lista));
        recyclerView.addItemDecoration(separador);

        mMainActivity.addLocalizacaoListener(this);

        return view;
    }

    private void atualizarEstabelecimentos() {
        Call<List<Estabelecimento>> call = mServico.getEstabelecimentosPorCoordenadas(
                mLocalizacao.getLatitude(),
                mLocalizacao.getLongitude(),
                100, // 100km de raio
                null, // categoria. null = todas
                100 // quantidade de resultados
        );

        call.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(Call<List<Estabelecimento>> call, Response<List<Estabelecimento>> response) {
                if (response.body() == null) {
                    onFailure(call, new NetworkErrorException("Null response from API"));
                    return;
                }
                mEstabelecimentos = response.body();
                mAdapter.atualizar(mLocalizacao, mEstabelecimentos);
                mInicioProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Estabelecimento>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mMainActivity, R.string.erro_servidor, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Estabelecimento> estabelecimentos = (ArrayList<Estabelecimento>) mAdapter.getEstabelecimentos();
        outState.putSerializable(ESTABELECIMENTOS, estabelecimentos);
        outState.putParcelable(LOCALIZACAO, mLocalizacao);
    }

    @Override
    public void onLocalizacaoChanged(Location localizacao) {
        mLocalizacao = localizacao;
        atualizarEstabelecimentos();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
