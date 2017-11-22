package com.example.gabri.saudeperto.atividades;

import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gabri.saudeperto.R;
import com.example.gabri.saudeperto.adaptadores.EstabelecimentosAdapter;
import com.example.gabri.saudeperto.modelos.Estabelecimento;
import com.example.gabri.saudeperto.modelos.Filtro;
import com.example.gabri.saudeperto.servicos.ApiEstabelecimentosInterface;
import com.example.gabri.saudeperto.servicos.TcuApi;
import com.example.gabri.saudeperto.utils.LocalizacaoHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultadosActivity extends AppCompatActivity {

    private ProgressBar mInicioProgressBar;
    private LinearLayout mListaVazia;
    private ApiEstabelecimentosInterface mServico;
    private EstabelecimentosAdapter mAdapter;
    private Filtro mFiltros;
    private Location mLocalizacao;

    private ArrayList<Estabelecimento> mEstabelecimentos;

    private static final String TAG = "ResultadosAvtivity";
    private static final String ESTABELECIMENTOS = "Estabelecimentos";
    private static final String LOCALIZACAO = "Localização";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        mServico = TcuApi.getInstance().getServico();
        mFiltros = (Filtro) getIntent().getSerializableExtra(BuscarActivity.EXTRA_FILTROS);

        setupView(savedInstanceState);
    }

    private void setupView(Bundle savedInstanceState) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setEnabled(false);

        mInicioProgressBar = (ProgressBar) findViewById(R.id.inicio_progress_bar);
        mListaVazia = (LinearLayout) findViewById(R.id.lista_vazia);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        TextView listaVaziaMensagem = (TextView) findViewById(R.id.lista_vazia_mensagem);

        mAdapter = new EstabelecimentosAdapter(this);
        listaVaziaMensagem.setText(R.string.nenhum_resultado_mensagem);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration separador = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        separador.setDrawable(ContextCompat.getDrawable(this, R.drawable.separador_lista));
        recyclerView.addItemDecoration(separador);

        if (savedInstanceState == null) {
            mLocalizacao = getIntent().getParcelableExtra(LocalizacaoHelper.LOCALIZACAO_EXTRA);
            atualizarEstabelecimentos();
        } else {
            mLocalizacao = savedInstanceState.getParcelable(LOCALIZACAO);
            mEstabelecimentos = (ArrayList<Estabelecimento>) savedInstanceState.getSerializable(ESTABELECIMENTOS);
            mEstabelecimentos = mEstabelecimentos == null ? new ArrayList<>() : mEstabelecimentos;
            mListaVazia.setVisibility(mEstabelecimentos.size() > 0 ? View.GONE : View.VISIBLE);
            mAdapter.atualizar(mLocalizacao, mEstabelecimentos);
            mInicioProgressBar.setVisibility(View.GONE);
        }
    }

    private void atualizarEstabelecimentos() {
        Call<List<Estabelecimento>> call = mServico.getTodosEstabelecimentos(
                mFiltros.getNome(),
                mFiltros.getCidade(),
                mFiltros.getEstado(),
                mFiltros.getCategoria(),
                mFiltros.getSus(),
                mFiltros.getRetencao()
        );

        call.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(Call<List<Estabelecimento>> call, Response<List<Estabelecimento>> response) {
                if (response.body() == null) {
                    onFailure(call, new Exception("Null response from API"));
                    return;
                }
                if (response.body().size() == 0) {
                    onNotFound();
                    return;
                }

                mEstabelecimentos = (ArrayList<Estabelecimento>) response.body();
                Log.d(TAG, "Resposta da API: " + mEstabelecimentos.toString());

                if (mLocalizacao != null) {
                    Collections.sort(mEstabelecimentos, (est1, est2) ->
                            est1.getDistancia(mLocalizacao.getLatitude(), mLocalizacao.getLongitude())
                                    .compareTo(est2.getDistancia(mLocalizacao.getLatitude(), mLocalizacao.getLongitude())));
                }

                mAdapter.atualizar(mLocalizacao, mEstabelecimentos);
                mInicioProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Estabelecimento>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ResultadosActivity.this, "Erro ao tentar se comunicar com o servidor", Toast.LENGTH_SHORT).show();
                finish();
            }

            void onNotFound() {
                mListaVazia.setVisibility(View.VISIBLE);
                mInicioProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ESTABELECIMENTOS, mEstabelecimentos);
        outState.putParcelable(LOCALIZACAO, mLocalizacao);
    }
}
