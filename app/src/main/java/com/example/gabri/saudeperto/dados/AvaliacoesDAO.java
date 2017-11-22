package com.example.gabri.saudeperto.dados;

import android.util.Log;


import com.example.gabri.saudeperto.modelos.Avaliacao;
import com.example.gabri.saudeperto.modelos.Estabelecimento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AvaliacoesDAO {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private static final AvaliacoesDAO ourInstance = new AvaliacoesDAO();
    public static AvaliacoesDAO getInstance() {
        return ourInstance;
    }
    private static final String TAG = "AvaliacoesDAO";

    public interface NotaListener {
        void onNotasChanged(List<Avaliacao> avaliacoes);
        void onNotaUsuarioChanged(Avaliacao avaliacao);
    }

    private AvaliacoesDAO() {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void onError(DatabaseError databaseError) {
        Log.e(TAG, "Erro ao carregar avaliações: ", databaseError.toException());
    }

    public void setNotaListener(Estabelecimento estabelecimento, final NotaListener listener) {
        if (mAuth.getCurrentUser() == null) return;

        DatabaseReference avaliacoesRef = mDatabase.getReference("avaliacoes");

        ValueEventListener notaUsuarioListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Avaliacao avaliacao = dataSnapshot.getValue(Avaliacao.class);
                if (avaliacao != null) listener.onNotaUsuarioChanged(avaliacao);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                onError(databaseError);
            }
        };

        avaliacoesRef
                .child(estabelecimento.getCodUnidade())
                .child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(notaUsuarioListener);

        ValueEventListener notasListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Avaliacao> avaliacoes = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Avaliacao avaliacao = child.getValue(Avaliacao.class);
                    avaliacoes.add(avaliacao);
                }
                listener.onNotasChanged(avaliacoes);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                onError(databaseError);
            }
        };

        avaliacoesRef
                .child(estabelecimento.getCodUnidade())
                .addValueEventListener(notasListener);
    }

    public void salvar(Float nota, Estabelecimento estabelecimento) {
        DatabaseReference avaliacoesRef = mDatabase.getReference("avaliacoes");

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuarioId(mAuth.getCurrentUser().getUid());
        avaliacao.setCodEstabelecimento(estabelecimento.getCodUnidade());
        avaliacao.setNota(nota);
        avaliacoesRef
                .child(avaliacao.getCodEstabelecimento())
                .child(avaliacao.getUsuarioId())
                .setValue(avaliacao);
    }

    public float calcularMedia(List<Avaliacao> avaliacoes) {
        Float total = 0f;
        int quantidadeAvaliacoes = avaliacoes.size();
        for (Avaliacao avaliacao : avaliacoes) {
            total += avaliacao.getNota();
        }
        return total / quantidadeAvaliacoes;
    }

}
