package com.example.gabri.saudeperto.utils;



import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.gabri.saudeperto.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;


public class LocalizacaoHelper {

    public static final int REQUEST_LOCALIZACAO = 0;
    public static final String LOCALIZACAO_ACTION = "com.example.gabri.saudeperto.localizacao";
    public static final String LOCALIZACAO_EXTRA = "localizacao";

    public interface LocalizacaoListener {
        void onLocalizacaoChanged(Location localizacao);
    }

    public static boolean pedirPermissao(Activity activity) {
        return pedirPermissao(activity, null);
    }

    public static boolean pedirPermissao(Fragment fragment) {
        return pedirPermissao(fragment.getActivity(), fragment);
    }

    private static boolean pedirPermissao(Activity activity, Fragment fragment) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return false;
            }
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (fragment == null) {
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_LOCALIZACAO);
            } else {
                fragment.requestPermissions(permissions, REQUEST_LOCALIZACAO);
            }
            return false;
        }
        return true;
    }

    @Nullable
    public static Location getLocalizacao(Activity activity, GoogleApiClient googleApiClient) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        return null;
    }

    public static void alertarLocalizacaoNegada(final Activity activity) {
        Log.d("LOCALIZAÇÂO", "Não permitido");

        new AlertDialog.Builder(activity)
                .setTitle(R.string.main_dlg_titulo_localizacao_negada)
                .setMessage(R.string.main_dlg_mensagem_localizacao_negada)
                .setPositiveButton("OK", (dialog, which) -> pedirPermissao(activity))
                .show();
    }

    @NonNull
    public static Double calcularDistancia(LatLng origem, LatLng destino) {
        return SphericalUtil.computeDistanceBetween(origem, destino);
    }

    public static String formatarMetros(double metros) {
            if (metros < 1000) {
                return ((int) metros) + "m";
            } else if (metros < 10000) {
                return formatarDecimal(metros / 1000, 1) + "km";
            } else {
                return ((int) (metros / 1000)) + "km";
            }
    }

    private static String formatarDecimal(double val, int dec) {
        int factor = (int) Math.pow(10, dec);

        int front = (int) (val);
        int back = (int) Math.abs(val * (factor)) % factor;

        return front + "," + back;
    }
}
