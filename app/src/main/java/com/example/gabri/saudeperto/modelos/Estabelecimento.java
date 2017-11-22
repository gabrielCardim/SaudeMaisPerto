package com.example.gabri.saudeperto.modelos;




import com.example.gabri.saudeperto.utils.LocalizacaoHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Estabelecimento implements Serializable {

    private static final String SIM = "Sim";
    private static final String NAO = "Não";

    @Exclude
    private Float notaMedia;
    @Exclude
    private String endereco;
    @SerializedName("bairro")
    @Expose
    private String bairro;
    @SerializedName("categoriaUnidade")
    @Expose
    private String categoriaUnidade;
    @SerializedName("cep")
    @Expose
    private String cep;
    @SerializedName("cidade")
    @Expose
    private String cidade;
    @SerializedName("cnpj")
    @Expose
    private String cnpj;
    @SerializedName("codCnes")
    @Expose
    private Integer codCnes;
    @SerializedName("codIbge")
    @Expose
    private Integer codIbge;
    @SerializedName("codUnidade")
    @Expose
    private String codUnidade;
    @SerializedName("descricaoCompleta")
    @Expose
    private String descricaoCompleta;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("esferaAdministrativa")
    @Expose
    private String esferaAdministrativa;
    @SerializedName("fluxoClientela")
    @Expose
    private String fluxoClientela;
    @SerializedName("grupo")
    @Expose
    private String grupo;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("logradouro")
    @Expose
    private String logradouro;
    @SerializedName("long")
    @Expose
    private double _long;
    @SerializedName("natureza")
    @Expose
    private String natureza;
    @SerializedName("nomeFantasia")
    @Expose
    private String nomeFantasia;
    @SerializedName("numero")
    @Expose
    private String numero;
    @SerializedName("origemGeografica")
    @Expose
    private String origemGeografica;
    @SerializedName("retencao")
    @Expose
    private String retencao;
    @SerializedName("telefone")
    @Expose
    private String telefone;
    @SerializedName("temAtendimentoAmbulatorial")
    @Expose
    private String temAtendimentoAmbulatorial;
    @SerializedName("temAtendimentoUrgencia")
    @Expose
    private String temAtendimentoUrgencia;
    @SerializedName("temCentroCirurgico")
    @Expose
    private String temCentroCirurgico;
    @SerializedName("temDialise")
    @Expose
    private String temDialise;
    @SerializedName("temNeoNatal")
    @Expose
    private String temNeoNatal;
    @SerializedName("temObstetra")
    @Expose
    private String temObstetra;
    @SerializedName("tipoUnidade")
    @Expose
    private String tipoUnidade;
    @SerializedName("tipoUnidadeCnes")
    @Expose
    private String tipoUnidadeCnes;
    @SerializedName("turnoAtendimento")
    @Expose
    private String turnoAtendimento;
    @SerializedName("uf")
    @Expose
    private String uf;
    @SerializedName("vinculoSus")
    @Expose
    private String vinculoSus;

    public String getNotaMediaFormatada() {
        if (this.notaMedia == null) return null;
        return String.format("%.0f", this.notaMedia);
    }

    public Float getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(Float notaMedia) {
        this.notaMedia = notaMedia;
    }

    public Double getDistancia(double latitude, double longitude) {
        LatLng localEstabelecimento = new LatLng(this.lat, this._long);
        LatLng localUsuario = new LatLng(latitude, longitude);
        return LocalizacaoHelper.calcularDistancia(localEstabelecimento, localUsuario);
    }

    public String getDistanciaFormatada(double latitude, double longitude) {
        Double metros = getDistancia(latitude, longitude);
        return LocalizacaoHelper.formatarMetros(metros);
    }

    public String getEndereco() {
        if (logradouro == null || cidade == null || uf == null) {
            return "";
        }
        return logradouro + ", " + numero + " - " + bairro + ". " + cidade + " - " + uf;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCategoriaUnidade() {
        return categoriaUnidade;
    }

    public void setCategoriaUnidade(String categoriaUnidade) {
        this.categoriaUnidade = categoriaUnidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Integer getCodCnes() {
        return codCnes;
    }

    public void setCodCnes(Integer codCnes) {
        this.codCnes = codCnes;
    }

    public Integer getCodIbge() {
        return codIbge;
    }

    public void setCodIbge(Integer codIbge) {
        this.codIbge = codIbge;
    }

    public String getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(String codUnidade) {
        this.codUnidade = codUnidade;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = descricaoCompleta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEsferaAdministrativa() {
        return esferaAdministrativa;
    }

    public void setEsferaAdministrativa(String esferaAdministrativa) {
        this.esferaAdministrativa = esferaAdministrativa;
    }

    public String getFluxoClientela() {
        return fluxoClientela;
    }

    public void setFluxoClientela(String fluxoClientela) {
        this.fluxoClientela = fluxoClientela;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public double getLong() {
        return _long;
    }

    public void setLong(Double _long) {
        this._long = _long;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getOrigemGeografica() {
        return origemGeografica;
    }

    public void setOrigemGeografica(String origemGeografica) {
        this.origemGeografica = origemGeografica;
    }

    public String getRetencao() {
        return retencao;
    }

    public void setRetencao(String retencao) {
        this.retencao = retencao;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean temAtendimentoAmbulatorial() {
        return temAtendimentoAmbulatorial.equals(SIM);
    }

    public void setTemAtendimentoAmbulatorial(String temAtendimentoAmbulatorial) {
        this.temAtendimentoAmbulatorial = temAtendimentoAmbulatorial;
    }

    public boolean temAtendimentoUrgencia() {
        return temAtendimentoUrgencia.equals(SIM);
    }

    public void setTemAtendimentoUrgencia(String temAtendimentoUrgencia) {
        this.temAtendimentoUrgencia = temAtendimentoUrgencia;
    }

    public boolean temCentroCirurgico() {
        return temCentroCirurgico.equals(SIM);
    }

    public void setTemCentroCirurgico(String temCentroCirurgico) {
        this.temCentroCirurgico = temCentroCirurgico;
    }

    public boolean temDialise() {
        return temDialise.equals(SIM);
    }

    public void setTemDialise(String temDialise) {
        this.temDialise = temDialise;
    }

    public boolean temNeoNatal() {
        return temNeoNatal.equals(SIM);
    }

    public void setTemNeoNatal(String temNeoNatal) {
        this.temNeoNatal = temNeoNatal;
    }

    public boolean temObstetra() {
        return temObstetra.equals(SIM);
    }

    public void setTemObstetra(String temObstetra) {
        this.temObstetra = temObstetra;
    }

    public String getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(String tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    public String getTipoUnidadeCnes() {
        return tipoUnidadeCnes;
    }

    public void setTipoUnidadeCnes(String tipoUnidadeCnes) {
        this.tipoUnidadeCnes = tipoUnidadeCnes;
    }

    public String getTemAtendimentoAmbulatorial() {
        return temAtendimentoAmbulatorial;
    }

    public String getTemAtendimentoUrgencia() {
        return temAtendimentoUrgencia;
    }

    public String getTemCentroCirurgico() {
        return temCentroCirurgico;
    }

    public String getTemDialise() {
        return temDialise;
    }

    public String getTemNeoNatal() {
        return temNeoNatal;
    }

    public String getTemObstetra() {
        return temObstetra;
    }

    public String getVinculoSus() {
        return vinculoSus;
    }

    public String getTurnoAtendimento() {
        return turnoAtendimento;
    }

    public void setTurnoAtendimento(String turnoAtendimento) {
        this.turnoAtendimento = turnoAtendimento;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public boolean temVinculoSus() {
        return vinculoSus.equals(SIM);
    }

    public void setVinculoSus(String vinculoSus) {
        this.vinculoSus = vinculoSus;
    }

    @Override
    public String toString() {
        return "Estabelecimento{" +
                "endereco='" + endereco + '\'' +
                ", bairro='" + bairro + '\'' +
                ", categoriaUnidade='" + categoriaUnidade + '\'' +
                ", cep='" + cep + '\'' +
                ", cidade='" + cidade + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", codCnes=" + codCnes +
                ", codIbge=" + codIbge +
                ", codUnidade='" + codUnidade + '\'' +
                ", descricaoCompleta='" + descricaoCompleta + '\'' +
                ", email='" + email + '\'' +
                ", esferaAdministrativa='" + esferaAdministrativa + '\'' +
                ", fluxoClientela='" + fluxoClientela + '\'' +
                ", grupo='" + grupo + '\'' +
                ", lat=" + lat +
                ", logradouro='" + logradouro + '\'' +
                ", _long=" + _long +
                ", natureza='" + natureza + '\'' +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                ", numero='" + numero + '\'' +
                ", origemGeografica='" + origemGeografica + '\'' +
                ", retencao='" + retencao + '\'' +
                ", telefone='" + telefone + '\'' +
                ", temAtendimentoAmbulatorial='" + temAtendimentoAmbulatorial + '\'' +
                ", temAtendimentoUrgencia='" + temAtendimentoUrgencia + '\'' +
                ", temCentroCirurgico='" + temCentroCirurgico + '\'' +
                ", temDialise='" + temDialise + '\'' +
                ", temNeoNatal='" + temNeoNatal + '\'' +
                ", temObstetra='" + temObstetra + '\'' +
                ", tipoUnidade='" + tipoUnidade + '\'' +
                ", tipoUnidadeCnes='" + tipoUnidadeCnes + '\'' +
                ", turnoAtendimento='" + turnoAtendimento + '\'' +
                ", uf='" + uf + '\'' +
                ", vinculoSus='" + vinculoSus + '\'' +
                '}';
    }
}