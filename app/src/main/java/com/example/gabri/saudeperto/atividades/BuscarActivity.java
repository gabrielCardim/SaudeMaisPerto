package com.example.gabri.saudeperto.atividades;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gabri.saudeperto.R;
import com.example.gabri.saudeperto.modelos.Filtro;
import com.example.gabri.saudeperto.utils.LocalizacaoHelper;


public class BuscarActivity extends AppCompatActivity {

    private EditText mNome;
    private EditText mCidade;
    private Spinner mCategoria;
    private Spinner mEstado;
    private Spinner mSus;
    private Spinner mRetencao;
    private Location mLocalizacao;

    public static final String TAG = "BuscarActivity";
    public static final String EXTRA_FILTROS = "filtros";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        mLocalizacao = getIntent().getParcelableExtra(LocalizacaoHelper.LOCALIZACAO_EXTRA);

        setupView();
        stupDropdowns();
    }

    private void setupView() {
        mNome = (EditText) findViewById(R.id.buscar_nome);
        mCidade = (EditText) findViewById(R.id.buscar_cidade);
        mCategoria = (Spinner) findViewById(R.id.buscar_categoria);
        mEstado = (Spinner) findViewById(R.id.buscar_estado);
        mSus = (Spinner) findViewById(R.id.buscar_sus);
        mRetencao = (Spinner) findViewById(R.id.buscar_retencao);
    }

    private void stupDropdowns() {
        ArrayAdapter<CharSequence> categoriaAdapter = ArrayAdapter.createFromResource(this, R.array.categorias, android.R.layout.simple_spinner_item);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategoria.setAdapter(categoriaAdapter);

        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(this, R.array.estados, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEstado.setAdapter(estadoAdapter);

        ArrayAdapter<CharSequence> susAdapter = ArrayAdapter.createFromResource(this, R.array.sus, android.R.layout.simple_spinner_item);
        susAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSus.setAdapter(susAdapter);

        ArrayAdapter<CharSequence> retencaoAdapter = ArrayAdapter.createFromResource(this, R.array.retencoes, android.R.layout.simple_spinner_item);
        retencaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRetencao.setAdapter(retencaoAdapter);
    }

    private Filtro getFiltros() {
        Filtro filtros = new Filtro();
        filtros.setNome(mNome.getText().toString());
        filtros.setCidade(mCidade.getText().toString());
        filtros.setCategoria(mCategoria.getSelectedItem().toString().equals("Qualquer um") ? null : mCategoria.getSelectedItem().toString());
        filtros.setEstado(mEstado.getSelectedItem().toString().equals("Qualquer um") ? null : mEstado.getSelectedItem().toString());
        filtros.setSus(mSus.getSelectedItem().toString().equals("Qualquer um") ? null : mSus.getSelectedItem().toString());
        filtros.setRetencao(mRetencao.getSelectedItem().toString().equals("Qualquer um") ? null : mRetencao.getSelectedItem().toString());
        return filtros;
    }

    private void aplicar() {
        Filtro filtros = getFiltros();
        Log.d(TAG, "Filtros: " + filtros.toString());
        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra(EXTRA_FILTROS, filtros);
        intent.putExtra(LocalizacaoHelper.LOCALIZACAO_EXTRA, mLocalizacao);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filtrar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_buscar:
                aplicar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
