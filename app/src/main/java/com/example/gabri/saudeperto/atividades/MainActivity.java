package com.example.gabri.saudeperto.atividades;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.gabri.saudeperto.R;
import com.example.gabri.saudeperto.adaptadores.ViewPagerAdapter;
import com.example.gabri.saudeperto.fragmentos.FavoritosFragment;
import com.example.gabri.saudeperto.fragmentos.MapaFragment;
import com.example.gabri.saudeperto.fragmentos.ProximosFragment;
import com.example.gabri.saudeperto.modelos.Estabelecimento;
import com.example.gabri.saudeperto.servicos.ApiEstabelecimentosInterface;
import com.example.gabri.saudeperto.servicos.TcuApi;
import com.example.gabri.saudeperto.utils.FotoHelper;
import com.example.gabri.saudeperto.utils.LocalizacaoHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ApiEstabelecimentosInterface mServico;
    private FloatingActionButton mEmergenciaFAB;
    private FloatingActionButton mLocalFAB;
    private ProgressDialog mProgessEmergencia;
    private LocalBroadcastManager mLocalBroadcastManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocalizacao;
    private List<LocalizacaoHelper.LocalizacaoListener> mLocalizacaoListeners = new ArrayList<>();

    private final static int REQUEST_CODE_FILTRAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalizacaoHelper.pedirPermissao(this);
        configurarLocalBroadcast();

        mServico = TcuApi.getInstance().getServico();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        setupView();
        setupViewPager();

        // Salva foto do usuario no armazenamento interno
        FotoHelper.setFotoUsuario(this, null, FirebaseStorage.getInstance(), FirebaseAuth.getInstance());
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocalFAB = (FloatingActionButton) findViewById(R.id.fab_local);
        mEmergenciaFAB = (FloatingActionButton) findViewById(R.id.fab_emergencia);
        mEmergenciaFAB.setOnClickListener(v -> {
            mProgessEmergencia = new ProgressDialog(MainActivity.this);
            mProgessEmergencia.setMessage("Buscando estabeleciento de urgências mais próximo");
            mProgessEmergencia.show();
            emergencia();
        });
    }

    private void setupViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapaFragment(), getString(R.string.tab_mapa));
        adapter.addFragment(new ProximosFragment(), getString(R.string.tab_proximos));
        adapter.addFragment(new FavoritosFragment(), getString(R.string.tab_favoritos));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mEmergenciaFAB.hide();
                    mLocalFAB.show();
                } else {
                    mLocalFAB.hide();
                    mEmergenciaFAB.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    private void configurarLocalBroadcast() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location localizacao = intent.getParcelableExtra(LocalizacaoHelper.LOCALIZACAO_EXTRA);
                for (LocalizacaoHelper.LocalizacaoListener localizacaoListener : mLocalizacaoListeners) {
                    localizacaoListener.onLocalizacaoChanged(localizacao);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocalizacaoHelper.LOCALIZACAO_ACTION);
        mLocalBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void addLocalizacaoListener(LocalizacaoHelper.LocalizacaoListener listener) {
        mLocalizacaoListeners.add(listener);
    }

    public void atualizarLocalizacao() {
        onConnected(new Bundle());
    }

    private void emergencia() {
        Call<List<Estabelecimento>> call = mServico.getEstabelecimentosPorCoordenadas(
                mLocalizacao.getLatitude(),
                mLocalizacao.getLongitude(),
                100, // 100km de raio
                "URGÊNCIA", // categoria
                1 // quantidade de resultados
        );

        call.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(Call<List<Estabelecimento>> call, Response<List<Estabelecimento>> response) {
                if (response.body() == null) {
                    onFailure(call, new Exception("Null response from API"));
                    return;
                }
                List<Estabelecimento> estabelecimentos = response.body();
                Intent intent = new Intent(MainActivity.this, DetalhesActivity.class);
                Estabelecimento estabelecimento = estabelecimentos.get(0);
                intent.putExtra(DetalhesActivity.EXTRA_ESTABELECIMENTO, estabelecimento);
                startActivity(intent);
                mProgessEmergencia.dismiss();
            }

            @Override
            public void onFailure(Call<List<Estabelecimento>> call, Throwable t) {
                t.printStackTrace();
                mProgessEmergencia.dismiss();
                Toast.makeText(MainActivity.this, "Erro ao tentar se comunicar com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setFABonClickListener(View.OnClickListener listener) {
        mLocalFAB.setOnClickListener(listener);
    }

    public void habilitarLocalFAB() { mLocalFAB.show(); }

    public void desabilitarLocalFAB() { mLocalFAB.hide(); }

    private void filtrar() {
        Intent it = new Intent(MainActivity.this, BuscarActivity.class);
        it.putExtra(LocalizacaoHelper.LOCALIZACAO_EXTRA, mLocalizacao);
        startActivity(it);
    }

    private void sobre() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
        dialogo.setMessage(R.string.main_dlg_sobre)
                .setPositiveButton(R.string.btn_ok, (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    private void sair() {
        FirebaseAuth.getInstance().signOut();
        Intent it = new Intent(MainActivity.this, LoginActivity.class);
        finish();
        startActivity(it);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        atualizarLocalizacao();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LocalizacaoHelper.REQUEST_LOCALIZACAO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                } else {
                    LocalizacaoHelper.alertarLocalizacaoNegada(this);
                }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocalizacao = LocalizacaoHelper.getLocalizacao(this, mGoogleApiClient);
        if (mLocalizacao != null) {
            Intent intent = new Intent(LocalizacaoHelper.LOCALIZACAO_ACTION);
            intent.putExtra(LocalizacaoHelper.LOCALIZACAO_EXTRA, mLocalizacao);
            mLocalBroadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.erro_servidor, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filtrar:
                filtrar();
                return true;
            case R.id.action_sobre:
                sobre();
                return true;
            case R.id.action_sair:
                sair();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_FILTRAR) {
            // TODO
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}