package com.example.gabri.saudeperto.servicos;


import com.example.gabri.saudeperto.modelos.Estabelecimento;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiEstabelecimentosInterface {

    String BASE_URL = "http://mobile-aceite.tcu.gov.br/mapa-da-saude/";
    int QUANTIDADE = 100;

    @GET("rest/estabelecimentos?quantidade=" + QUANTIDADE)
    Call<List<Estabelecimento>> getTodosEstabelecimentos(
            @Query("nomeFantasia") String nome,
            @Query("municipio") String municipio,
            @Query("uf") String uf,
            @Query("categoria") String categoria,
            @Query("vinculoSus") String vinculoSus,
            @Query("retencao") String retencao
    );

    @GET("rest/estabelecimentos/latitude/{latitude}/longitude/{longitude}/raio/{raio}" + QUANTIDADE)
    Call<List<Estabelecimento>> getEstabelecimentosPorCoordenadas(
            @Path("latitude") double latitude,
            @Path("longitude") double longitude,
            @Path("raio") float raio,
            @Query("categoria") String categoria,
            @Query("quantidade") int quantidade
    );

    @GET("rest/estabelecimentos/unidade/{codUnidade}")
    Call<Estabelecimento> getEstabelecimento(@Path("codUnidade") String codUnidade);
}
