package com.example.gabri.saudeperto.fragmentos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gabri.saudeperto.R;
import com.example.gabri.saudeperto.atividades.DetalhesActivity;
import com.example.gabri.saudeperto.atividades.MainActivity;
import com.example.gabri.saudeperto.modelos.Estabelecimento;
import com.example.gabri.saudeperto.servicos.ApiEstabelecimentosInterface;
import com.example.gabri.saudeperto.servicos.TcuApi;
import com.example.gabri.saudeperto.utils.Cluster;
import com.example.gabri.saudeperto.utils.ClusterRenderer;
import com.example.gabri.saudeperto.utils.LocalizacaoHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapaFragment extends Fragment implements OnMapReadyCallback, LocalizacaoHelper.LocalizacaoListener, GoogleMap.OnCameraMoveListener {

    private MainActivity mMainActivity;

    private FrameLayout mMapaWrapper;
    private TextView mMapaTexto;
    private ProgressBar mMapaProgressBar;
    private MapView mMapView;

    private GoogleMap mMap;
    private ApiEstabelecimentosInterface mServico;
    private Location mLocalizacao;
    private List<Estabelecimento> mEstabelecimentos;
    private ClusterManager<Cluster> mClusterManager;
    private ClusterRenderer mClusterRenderer;

    private boolean mAlterandoLocal = false;

    private static final String TAG = "MapaFragment";
    private static final String ESTABALECIMENTOS = "Estabelecimentos";
    private static final String LOCALIZACAO = "Localização";
    private static final int RAIO = 5; // km

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) this.getActivity();
        if (savedInstanceState != null) {
            mEstabelecimentos = (ArrayList<Estabelecimento>) savedInstanceState.getSerializable(ESTABALECIMENTOS);
            mLocalizacao = savedInstanceState.getParcelable(LOCALIZACAO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        mServico = TcuApi.getInstance().getServico();

        mMapaWrapper = (FrameLayout) view.findViewById(R.id.mapa_wrapper);
        mMapaTexto = (TextView) view.findViewById(R.id.mapa_texto);
        mMapaProgressBar = (ProgressBar) view.findViewById(R.id.mapa_progressbar);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(mMainActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMainActivity.setFABonClickListener(v -> onMapClick());

        mMapView.getMapAsync(this);

        mMainActivity.addLocalizacaoListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ESTABALECIMENTOS, (ArrayList<Estabelecimento>) mEstabelecimentos);
        outState.putParcelable(LOCALIZACAO, mLocalizacao);
    }

    private void carregarEstabelecimentos() {
        Call<List<Estabelecimento>> call = mServico.getEstabelecimentosPorCoordenadas(
                mLocalizacao.getLatitude(),
                mLocalizacao.getLongitude(),
                RAIO, // raio
                null, // categoria
                10000 // quantidade de resultados
        );

        call.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(Call<List<Estabelecimento>> call, Response<List<Estabelecimento>> response) {
                if (response.body() == null) {
                    onFailure(call, new Exception("Null response from API"));
                    return;
                }
                mEstabelecimentos = response.body();
                configurarMapa();
            }

            @Override
            public void onFailure(Call<List<Estabelecimento>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mMainActivity, "Erro ao tentar se comunicar com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarMapa() {
        if (ActivityCompat.checkSelfPermission(mMainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "PERMISSÃO NEGADA");
            return;
        }

        mMap.clear();
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(mLocalizacao.getLatitude(), mLocalizacao.getLongitude()))
                .radius(RAIO * 1000) // raio em metros
                .strokeColor(ContextCompat.getColor(mMainActivity, R.color.azul_claro))
                .fillColor(ContextCompat.getColor(mMainActivity, R.color.azul_claro_transparente)));
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);

        configurarClusters();
        configurarCamera();

        Log.d(TAG, "Mapa configurado");
    }

    private void configurarClusters() {
        mClusterManager = new ClusterManager<>(mMainActivity, mMap);
        mClusterRenderer = new ClusterRenderer(mMainActivity, mMap, mClusterManager);
        mClusterManager.setRenderer(mClusterRenderer);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(cluster -> {
            Context context = mMainActivity;
            Estabelecimento estabelecimento = cluster.getEstabelecimento();
            Intent it = new Intent(context, DetalhesActivity.class);
            it.putExtra(DetalhesActivity.EXTRA_ESTABELECIMENTO, estabelecimento);
            context.startActivity(it);
        });
        adicionarMarcadores();
    }

    private void adicionarMarcadores() {
        mClusterManager.clearItems();
        List<Cluster> clusters = new ArrayList<>();
        for (Estabelecimento estabelecimento : mEstabelecimentos) {
            Cluster item = new Cluster(estabelecimento);
            clusters.add(item);
        }
        mClusterManager.addItems(clusters);
        mClusterManager.cluster();
    }

    private void configurarCamera() {
        mMapaProgressBar.setVisibility(View.GONE);
        mMapaWrapper.setVisibility(View.GONE);
        mMainActivity.habilitarLocalFAB();
        CameraPosition posicaoCamera = new CameraPosition.Builder()
                .target(new LatLng(mLocalizacao.getLatitude(), mLocalizacao.getLongitude()))
                .zoom(12f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(posicaoCamera));
    }

    public void onMapClick() {
        if (mMap == null) return;
        if (mAlterandoLocal) {
            mAlterandoLocal = false;
            mMap.setOnMapClickListener(null);
            mMapaWrapper.setVisibility(View.GONE);
        } else {
            mAlterandoLocal = true;
            mMapaProgressBar.setVisibility(View.INVISIBLE);
            mMapaTexto.setVisibility(View.VISIBLE);
            mMapaWrapper.setVisibility(View.VISIBLE);
            mMap.setOnMapClickListener(latLng -> {
                mAlterandoLocal = false;
                mMainActivity.desabilitarLocalFAB();
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.setOnMapClickListener(null);
                mMapaTexto.setVisibility(View.INVISIBLE);
                mMapaProgressBar.setVisibility(View.VISIBLE);
                mLocalizacao.setLatitude(latLng.latitude);
                mLocalizacao.setLongitude(latLng.longitude);
                carregarEstabelecimentos();
            });
        }
    }

    @Override
    public void onLocalizacaoChanged(Location localizacao) {
        if (mLocalizacao == null) {
            mLocalizacao = localizacao;
            if (mEstabelecimentos == null) {
                carregarEstabelecimentos();
            } else {
                configurarMapa();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocalizacao != null) configurarMapa();
    }

    @Override
    public void onCameraMove() {
        // Hack necessário porque...
        // o ClusterRenderer tem um bug que não retira o cluster
        // dos marcadores que têm a mesma localização
        // https://github.com/googlemaps/android-maps-utils/issues/384
        Float zoomAtual = mMap.getCameraPosition().zoom;
        if (zoomAtual >= 16) {
            mClusterRenderer.setMinClusterSize(9999); // Desabilita clustering
        } else {
            mClusterRenderer.setMinClusterSize(2); // Habilita clustering
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}