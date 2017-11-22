package com.example.gabri.saudeperto.dados;

import android.util.Log;


import com.example.gabri.saudeperto.modelos.Estabelecimento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritosDAO {

    private static final FavoritosDAO ourInstance = new FavoritosDAO();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private List<Estabelecimento> mEstabelecimentos;

    private static final String TAG = "FavoritosDAO";

    public interface FavoritosListener {
        void onFavoritosChanged(List<Estabelecimento> estabelecimentos);
    }

    public static FavoritosDAO getInstance() {
        return ourInstance;
    }

    private FavoritosDAO() {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void setFavoritosListener(final FavoritosDAO.FavoritosListener listener) {
        String id = mAuth.getCurrentUser().getUid();
        DatabaseReference favoritosRef = mDatabase.getReference("favoritos").child(id);

        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Estabelecimento> estabelecimentos = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Estabelecimento estabelecimento = snapshot.getValue(Estabelecimento.class);
                    estabelecimentos.add(estabelecimento);
                }
                mEstabelecimentos = estabelecimentos;
                listener.onFavoritosChanged(estabelecimentos);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Erro ao carregar favoritos: ", databaseError.toException());
            }
        };
        favoritosRef.addValueEventListener(valueListener);
    }

    public void salvar(Estabelecimento estabelecimento) {
        String id = mAuth.getCurrentUser().getUid();
        DatabaseReference favoritosRef = mDatabase.getReference("favoritos").child(id);

        favoritosRef.child(estabelecimento.getCodUnidade()).setValue(estabelecimento);
    }

    public void remover(Estabelecimento estabelecimento) {
        String id = mAuth.getCurrentUser().getUid();
        DatabaseReference favoritosRef = mDatabase.getReference("favoritos").child(id);

        favoritosRef.child(estabelecimento.getCodUnidade()).removeValue();
    }

    public boolean estaNosFavoritos(String codUnidade) {
        if (mEstabelecimentos == null) return false;
        for (Estabelecimento estabelecimento : mEstabelecimentos) {
            if (estabelecimento.getCodUnidade().equals(codUnidade)) {
                return true;
            }
        }
        return false;
    }
}
