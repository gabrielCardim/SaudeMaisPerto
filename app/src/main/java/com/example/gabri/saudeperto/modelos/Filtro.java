package com.example.gabri.saudeperto.modelos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Filtro implements Serializable {

    private String mNome;
    private String mCategoria;
    private String mCidade;
    private String mEstado;
    private String mSus;
    private String mRetencao;
    private Map<String, String> estados;

    public Filtro() {
        estados = new HashMap<>();
        estados.put("Acre", "AC");
        estados.put("Alagoas", "AL");
        estados.put("Amapá", "AP");
        estados.put("Amazonas", "AM");
        estados.put("Bahia", "BA");
        estados.put("Ceará", "CE");
        estados.put("Distrito Federal", "DF");
        estados.put("Espírito Santo", "ES");
        estados.put("Goiás", "GO");
        estados.put("Maranhão", "MA");
        estados.put("Mato Grosso", "MT");
        estados.put("Mato Grosso do Sul", "MS");
        estados.put("Minas Gerais", "MG");
        estados.put("Pará", "PA");
        estados.put("Paraíba", "PB");
        estados.put("Paraná", "PR");
        estados.put("Pernambuco", "PE");
        estados.put("Piauí", "PI");
        estados.put("Rio de Janeiro", "RJ");
        estados.put("Rio Grande do Norte", "RN");
        estados.put("Rio Grande do Sul", "RS");
        estados.put("Rondônia", "RO");
        estados.put("Roraima", "RR");
        estados.put("Santa Catarina", "SC");
        estados.put("São Paulo", "SP");
        estados.put("Sergipe", "SE");
        estados.put("Tocantins", "TO");
    }

    public String getNome() {
        return mNome;
    }

    public void setNome(String nome) {
        mNome = nome;
    }

    public String getCategoria() {
        return mCategoria;
    }

    public void setCategoria(String categoria) {
        mCategoria = categoria;
    }

    public String getCidade() {
        return mCidade;
    }

    public void setCidade(String cidade) {
        mCidade = cidade;
    }

    public String getEstado() {
        return mEstado;
    }

    public void setEstado(String estado) {
        mEstado = estados.get(estado);
    }

    public String getSus() {
        return mSus;
    }

    public void setSus(String sus) {
        mSus = sus;
    }

    public String getRetencao() {
        return mRetencao;
    }

    public void setRetencao(String retencao) {
        mRetencao = retencao;
    }

    @Override
    public String toString() {
        return "Filtro{" +
                "mNome='" + mNome + '\'' +
                ", mCategoria='" + mCategoria + '\'' +
                ", mCidade='" + mCidade + '\'' +
                ", mEstado='" + mEstado + '\'' +
                ", mSus='" + mSus + '\'' +
                ", mRetencao='" + mRetencao + '\'' +
                '}';
    }
}
