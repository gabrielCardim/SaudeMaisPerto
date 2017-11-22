package com.example.gabri.saudeperto.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;


import com.example.gabri.saudeperto.R;
import com.example.gabri.saudeperto.fragmentos.FavoritosFragment;

import com.example.gabri.saudeperto.modelos.Estabelecimento;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Acoes {

    public static final int LIGAR = 0;
    public static final int ABRIR_NO_GMAPS = 1;
    public static final int COMPARTILHAR = 2;
    public static final int COPIAR_TELEFONE = 3;
    public static final int COPIAR_ENDEREÇO = 4;
    public static final int ADICIONAR_OU_REMOVER_FAVORITOS = 5;

    private static List<String> longPressNames = Arrays.asList(
            "Ligar",
            "Abrir no Mapa",
            "Compartilhar",
            "Copiar Telefone",
            "Copiar Endereço",
            "Adicionar aos Favoritos",
            "Remover dos Favoritos"
    );

    @NonNull
    public static CharSequence[] getLongPressNames(Class fragmentClass) {
        List<String> names = new ArrayList<>(longPressNames);
        if (fragmentClass == FavoritosFragment.class) {
            names.remove("Adicionar aos Favoritos");
        } else {
            names.remove("Remover dos Favoritos");
        }
        CharSequence[] namesArray = new CharSequence[names.size()];
        return names.toArray(namesArray);
    }

    public static void abrirMapa(Context context, Estabelecimento estabelecimento) {
        String nome = estabelecimento.getNomeFantasia();
        String lat = Double.toString(estabelecimento.getLat());
        String lon = Double.toString(estabelecimento.getLong());
        String url = MessageFormat.format("geo:{0},{1}?q={0},{1}({2})", lat, lon, nome);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    public static void direcoesDoMapa(Context context, Estabelecimento estabelecimento) {
        String lat = Double.toString(estabelecimento.getLat());
        String lon = Double.toString(estabelecimento.getLong());
        String uriStr = MessageFormat.format("google.navigation:q={0},{1}", lat, lon);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriStr));
        context.startActivity(intent);
    }

    public static void ligar(Context context, Estabelecimento estabelecimento) {
        Uri uri = Uri.parse("tel:" + estabelecimento.getTelefone());
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    public static void compartilharTexto(Context context, Estabelecimento estabelecimento) {
        CharSequence titulo = context.getResources().getText(R.string.compartilhar_txt);
        String nome = estabelecimento.getNomeFantasia();
        String nomeEncoded = "";

        try {
            nomeEncoded = URLEncoder.encode(nome, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String lat = Double.toString(estabelecimento.getLat());
        String lon = Double.toString(estabelecimento.getLong());
        String texto = MessageFormat.format("{0} - http://maps.google.com/maps?q={1},{2}+({3})", nome, lat, lon, nomeEncoded);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, texto);
        intent.setType("text/plain");
        context.startActivity(Intent.createChooser(intent, titulo));
    }
}
