package com.example.gabri.saudeperto.utils;


import com.example.gabri.saudeperto.modelos.Estabelecimento;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Cluster implements ClusterItem {

    private Estabelecimento mEstabelecimento;

    public Cluster(Estabelecimento estabelecimento) {
        mEstabelecimento = estabelecimento;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(mEstabelecimento.getLat(), mEstabelecimento.getLong());
    }

    public String getTitle() {
        return mEstabelecimento.getNomeFantasia();
    }

    public String getSnippet() {
        return mEstabelecimento.getCategoriaUnidade();
    }

    public Estabelecimento getEstabelecimento() {
        return mEstabelecimento;
    }
}
