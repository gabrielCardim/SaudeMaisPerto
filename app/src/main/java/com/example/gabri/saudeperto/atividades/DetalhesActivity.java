package com.example.gabri.saudeperto.atividades;

import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.example.gabri.saudeperto.R;
import com.example.gabri.saudeperto.dados.AvaliacoesDAO;
import com.example.gabri.saudeperto.dados.FavoritosDAO;
import com.example.gabri.saudeperto.modelos.Avaliacao;
import com.example.gabri.saudeperto.modelos.Estabelecimento;
import com.example.gabri.saudeperto.utils.Acoes;
import com.example.gabri.saudeperto.utils.FotoHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class DetalhesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private Estabelecimento mEstabelecimento;
    private AvaliacoesDAO mAvaliacoesDAO;

    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;
    private FrameLayout mMapLoadingBackground;

    private FloatingActionButton mFloatingActionButton;
    private RatingBar mRatingBar;
    private RatingBar mMiniRatingBar;

    private Button mLigarButton;
    private Button mCompartilharButton;
    private Button mSalvarButton;

    private TextView mQuantidadeAvaliacoes;
    private TextView mNomeTextView;
    private TextView mEnderecoTextView;
    private TextView mTipoTextView;
    private TextView mRetencaoTextView;
    private TextView mTelefoneTextView;
    private TextView mTurnoTextView;

    private ImageView mTemSus;
    private ImageView mTemUrgencia;
    private ImageView mTemAmbulatorial;
    private ImageView mTemCentroCirurgico;
    private ImageView mTemObstetra;
    private ImageView mTemNeonatal;
    private ImageView mTemDialise;
    private ImageView mFotoImageView;

    public static final String EXTRA_ESTABELECIMENTO = "Estabelecimento";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAvaliacoesDAO = AvaliacoesDAO.getInstance();

        mEstabelecimento = (Estabelecimento) getIntent().getSerializableExtra(EXTRA_ESTABELECIMENTO);

        setupCoordinatorLayout(mEstabelecimento.getNomeFantasia());

        setupView();
        bindView();

        setupFABBehavior();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.detalhes_mapa);
        mapFragment.getMapAsync(this);
    }

    private void setupView() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_rotas);
        mRatingBar = (RatingBar) findViewById(R.id.detalhes_rating_bar);
        mMiniRatingBar = (RatingBar) findViewById(R.id.detalhes_mini_rating_bar);
        mQuantidadeAvaliacoes = (TextView) findViewById(R.id.detalhes_quantidade_avaliacoes);
        mLigarButton = (Button) findViewById(R.id.detalhes_btn_ligar);
        mCompartilharButton = (Button) findViewById(R.id.detalhes_btn_compartilhar);
        mSalvarButton = (Button) findViewById(R.id.detalhes_btn_salvar);
        mMapLoadingBackground = (FrameLayout) findViewById(R.id.map_background);
        mNomeTextView = (TextView) findViewById(R.id.detalhes_tv_nome);
        mEnderecoTextView = (TextView) findViewById(R.id.detalhes_tv_endereco);
        mTipoTextView = (TextView) findViewById(R.id.detalhes_tv_tipo);
        mRetencaoTextView = (TextView) findViewById(R.id.detalhes_tv_retencao);
        mTelefoneTextView = (TextView) findViewById(R.id.detalhes_tv_telefone);
        mTurnoTextView = (TextView) findViewById(R.id.detalhes_tv_turno);
        mTemSus = (ImageView) findViewById(R.id.detalhes_ico_sus);
        mTemUrgencia = (ImageView) findViewById(R.id.detalhes_ico_urgencia);
        mTemAmbulatorial = (ImageView) findViewById(R.id.detalhes_ico_ambulatorial);
        mTemCentroCirurgico = (ImageView) findViewById(R.id.detalhes_ico_centro_cirurgico);
        mTemObstetra = (ImageView) findViewById(R.id.detalhes_ico_obstetra);
        mTemNeonatal = (ImageView) findViewById(R.id.detalhes_ico_neonatal);
        mTemDialise = (ImageView) findViewById(R.id.detalhes_ico_dialise);

        mFotoImageView = (ImageView) findViewById(R.id.detalhes_foto_iv);
    }

    private void bindView() {
        mMiniRatingBar.setVisibility(View.GONE);
        mAvaliacoesDAO.setNotaListener(mEstabelecimento, new AvaliacoesDAO.NotaListener() {
            @Override
            public void onNotasChanged(List<Avaliacao> avaliacoes) {
                int quantidadeAvaliacoes = avaliacoes.size();
                Float media = mAvaliacoesDAO.calcularMedia(avaliacoes);
                String quantidadeTexto;
                int visibilidade;
                if (quantidadeAvaliacoes == 0) {
                    visibilidade = View.GONE;
                    quantidadeTexto = getString(R.string.detalhes_nenhuma_avaliacao);
                } else {
                    visibilidade = View.VISIBLE;
                    quantidadeTexto = getResources().getQuantityString(R.plurals.detalhes_quantidade_avaliacoes, quantidadeAvaliacoes, quantidadeAvaliacoes);
                }
                mMiniRatingBar.setRating(media);
                mMiniRatingBar.setVisibility(visibilidade);
                mQuantidadeAvaliacoes.setText(quantidadeTexto);
            }

            @Override
            public void onNotaUsuarioChanged(Avaliacao avaliacao) {
                mRatingBar.setRating(avaliacao.getNota());
            }
        });
        mFloatingActionButton.setOnClickListener(v -> Acoes.direcoesDoMapa(DetalhesActivity.this, mEstabelecimento));
        mRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> mAvaliacoesDAO.salvar(rating, mEstabelecimento));
        mLigarButton.setOnClickListener(v -> Acoes.ligar(DetalhesActivity.this, mEstabelecimento));
        mCompartilharButton.setOnClickListener(v -> Acoes.compartilharTexto(DetalhesActivity.this, mEstabelecimento));

        if (FavoritosDAO.getInstance().estaNosFavoritos(mEstabelecimento.getCodUnidade())) {
            configurarFavorito();
        } else {
            configurarNaoFavorito();
        }

        mNomeTextView.setText(mEstabelecimento.getNomeFantasia());
        mEnderecoTextView.setText(mEstabelecimento.getEndereco());
        mTipoTextView.setText(mEstabelecimento.getTipoUnidade());
        mRetencaoTextView.setText(mEstabelecimento.getRetencao());
        mTelefoneTextView.setText(mEstabelecimento.getTelefone());
        mTurnoTextView.setText(mEstabelecimento.getTurnoAtendimento());

        mTemSus.setImageResource(getBooleanImageView(mEstabelecimento.temVinculoSus()));
        mTemUrgencia.setImageResource(getBooleanImageView(mEstabelecimento.temAtendimentoUrgencia()));
        mTemAmbulatorial.setImageResource(getBooleanImageView(mEstabelecimento.temAtendimentoAmbulatorial()));
        mTemCentroCirurgico.setImageResource(getBooleanImageView(mEstabelecimento.temCentroCirurgico()));
        mTemObstetra.setImageResource(getBooleanImageView(mEstabelecimento.temObstetra()));
        mTemNeonatal.setImageResource(getBooleanImageView(mEstabelecimento.temNeoNatal()));
        mTemDialise.setImageResource(getBooleanImageView(mEstabelecimento.temDialise()));

        Drawable placeholderCircular = FotoHelper.imagemCircular(getResources(), R.drawable.usuario, 8);
        mFotoImageView.setImageDrawable(placeholderCircular);

        FotoHelper.setFotoUsuario(this, mFotoImageView, mStorage, mAuth);
    }

    private void configurarFavorito() {
        Drawable topDrawable = ContextCompat.getDrawable(this, R.drawable.ic_favorite);
        mSalvarButton.setText(getString(R.string.acao_remover));
        mSalvarButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, topDrawable, null, null);
        mSalvarButton.setOnClickListener(v -> {
            FavoritosDAO.getInstance().remover(mEstabelecimento);
            String nome = mEstabelecimento.getNomeFantasia();
            Snackbar.make(mCoordinatorLayout, getString(R.string.remover_dos_favoritos, nome), Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_desfazer, view -> {
                        FavoritosDAO.getInstance().salvar(mEstabelecimento);
                        configurarFavorito();
                    }).show();
            configurarNaoFavorito();
        });
    }

    private void configurarNaoFavorito() {
        Drawable topDrawable = ContextCompat.getDrawable(this, R.drawable.ic_favorite_border);
        mSalvarButton.setText(getString(R.string.acao_salvar));
        mSalvarButton.setCompoundDrawablesRelativeWithIntrinsicBounds(null, topDrawable, null, null);
        mSalvarButton.setOnClickListener(v -> {
            FavoritosDAO.getInstance().salvar(mEstabelecimento);
            String nome = mEstabelecimento.getNomeFantasia();
            Snackbar.make(mCoordinatorLayout, getString(R.string.add_aos_favoritos, nome), Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_desfazer, view -> {
                        FavoritosDAO.getInstance().remover(mEstabelecimento);
                        configurarNaoFavorito();
                    }).show();
            configurarFavorito();
        });
    }

    private int getBooleanImageView(boolean verdadeiro) {
        if (verdadeiro) {
            return R.drawable.ic_done;
        }
        return R.drawable.ic_clear;
    }

    private void setupFABBehavior() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Not collapsed
                if (verticalOffset == 0) {
                    mFloatingActionButton.show();
                }
                // Collapsed
                else {
                    mFloatingActionButton.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                        @Override
                        public void onHidden(FloatingActionButton fab) {
                            super.onShown(fab);
                            fab.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        UiSettings mapConfig = map.getUiSettings();
        mapConfig.setCompassEnabled(false);
        mapConfig.setMapToolbarEnabled(false);

        LatLng posicao = new LatLng(mEstabelecimento.getLat(), mEstabelecimento.getLong());

        CameraPosition posicaoDaCamera = new CameraPosition.Builder()
                .target(posicao)
                .zoom(14)
                .build();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(posicaoDaCamera));
        map.addMarker(new MarkerOptions()
                .position(posicao)
                .title(mEstabelecimento.getNomeFantasia()));

        map.setOnMapLoadedCallback(() -> mMapLoadingBackground.setVisibility(View.GONE));

    }

    private void setupCoordinatorLayout(String titulo) {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_detalhes);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(titulo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
