package com.example.gabri.saudeperto.servicos;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TcuApi {

    private static final TcuApi ourInstance = new TcuApi();
    private ApiEstabelecimentosInterface mServico;

    public static TcuApi getInstance() {
        return ourInstance;
    }

    private TcuApi() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiEstabelecimentosInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        mServico = retrofit.create(ApiEstabelecimentosInterface.class);
    }

    public ApiEstabelecimentosInterface getServico() { return mServico; }
}
