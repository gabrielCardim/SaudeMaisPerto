package com.example.gabri.saudeperto.atividades;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.gabri.saudeperto.R;
import com.example.gabri.saudeperto.utils.FotoHelper;
import com.example.gabri.saudeperto.utils.Validacao;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class CadastroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private EditText mNomeEditText;
    private EditText mEmailEditText;
    private EditText mSenhaEditText;
    private EditText mConfirmarSenhaEditText;
    private ImageView mFotoImageView;
    private FloatingActionButton mEditarFotoButton;
    private Button mCriarContaButton;
    private Button mLoginButton;
    private ProgressDialog mProgressDialog;

    private static final String STORAGE_URL = "gs://app-civico.appspot.com";
    private static final String DIRETORIO_FOTOS = "fotos";
    private static final String EXTENSAO_FOTOS = ".png";

    private static final String KEY_FOTO_PERFIL = "foto";

    private static final String EXTRA_EMAIL = "email";
    private static final String EXTRA_SENHA = "senha";

    private static final int REQUEST_CODE_ESCOLHER_FOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        mNomeEditText = (EditText) findViewById(R.id.cadastro_et_nome);
        mEmailEditText = (EditText) findViewById(R.id.cadastro_et_email);
        mSenhaEditText = (EditText) findViewById(R.id.cadastro_et_senha);
        mConfirmarSenhaEditText = (EditText) findViewById(R.id.cadastro_et_confirmar_senha);
        mFotoImageView = (ImageView) findViewById(R.id.cadastro_iv_foto);
        mEditarFotoButton = (FloatingActionButton) findViewById(R.id.cadastro_btn_editar_foto);
        mCriarContaButton = (Button) findViewById(R.id.cadastro_btn_cadastrar);
        mLoginButton = (Button) findViewById(R.id.cadastro_btn_login);

        mFotoImageView.setOnClickListener(v -> trocarFoto());
        mEditarFotoButton.setOnClickListener(v -> trocarFoto());
        mCriarContaButton.setOnClickListener(v -> cadastrar());
        mLoginButton.setOnClickListener(v -> finish());
        mConfirmarSenhaEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mCriarContaButton.performClick();
                return true;
            }
            return false;
        });

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl(STORAGE_URL);

        mAuthListener = firebaseAuth -> {
            FirebaseUser usuario = firebaseAuth.getCurrentUser();
            // Usuário entrou
            if (usuario != null) {
                // BUG do Firebase: Não atualizou o perfil
                if (usuario.getDisplayName() == null) {
                    Log.d("PERFIL", "NAO ATUALIZOU AINDA");
                    uploadFotoDoPerfil(usuario);
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(CadastroActivity.this, R.string.cadastro_toast_conta_criada,
                            Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                }
            }
        };

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FOTO_PERFIL)) {
            // Carrega foto do usuário circular
            Drawable fotoCircular = FotoHelper.imagemCircular(getResources(), savedInstanceState.getParcelable(KEY_FOTO_PERFIL));
            mFotoImageView.setImageDrawable(fotoCircular);
        } else {
            // Carrega foto padrão circular
            Drawable fotoCircular = FotoHelper.imagemCircular(getResources(), R.drawable.usuario);
            mFotoImageView.setImageDrawable(fotoCircular);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mEmailEditText.setText(extras.getString(EXTRA_EMAIL, ""));
            mSenhaEditText.setText(extras.getString(EXTRA_SENHA, ""));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ESCOLHER_FOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap novaFoto = extras.getParcelable("data");
                Drawable fotoCircular = FotoHelper.imagemCircular(getResources(), novaFoto);
                mFotoImageView.setImageDrawable(fotoCircular);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bitmap foto = ((RoundedBitmapDrawable) mFotoImageView.getDrawable()).getBitmap();
        outState.putParcelable(KEY_FOTO_PERFIL, foto);
    }

    public static Intent newIntent(Context contexto, String email, String senha) {
        Intent intent = new Intent(contexto, CadastroActivity.class);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_SENHA, senha);
        return intent;
    }

    private void uploadFotoDoPerfil(final FirebaseUser usuario) {
        final String CAMINHO_COMPLETO = DIRETORIO_FOTOS + "/" + usuario.getUid() + EXTENSAO_FOTOS;

        StorageReference fotoRef = mStorageRef.child(CAMINHO_COMPLETO);

        byte[] data = FotoHelper.imageViewToByteArray(mFotoImageView);

        UploadTask uploadTask = fotoRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                atualizarPerfil(usuario, downloadUrl);
        });

    }

    private void atualizarPerfil(FirebaseUser usuario, Uri foto) {
        String nome = mNomeEditText.getText().toString();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .setPhotoUri(foto)
                .build();

        usuario.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reautenticar();
                    }
                });
    }

    private void reautenticar() {
        FirebaseAuth.getInstance().signOut();

        String email = mEmailEditText.getText().toString();
        String senha = mSenhaEditText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, senha);
    }

    private boolean validar() {

        Boolean valido = true;

        TextInputLayout nomeWrapper = (TextInputLayout) findViewById(R.id.cadastro_et_nome_wrapper);
        TextInputLayout emailWrapper = (TextInputLayout) findViewById(R.id.cadastro_et_email_wrapper);
        TextInputLayout senhaWrapper = (TextInputLayout) findViewById(R.id.cadastro_et_senha_wrapper);
        TextInputLayout confirmarSenhaWrapper = (TextInputLayout) findViewById(R.id.cadastro_et_confirmar_senha_wrapper);

        String nome = mNomeEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String senha = mSenhaEditText.getText().toString();
        String confirmarSenha = mConfirmarSenhaEditText.getText().toString();

        if (!Validacao.nome(nome)) {
            valido = false;
            nomeWrapper.setError(getString(R.string.erro_campo_obrigatorio));
        } else {
            nomeWrapper.setErrorEnabled(false);
        }

        if (!Validacao.email(email)) {
            valido = false;
            emailWrapper.setError(getString(R.string.erro_email));
        } else {
            emailWrapper.setErrorEnabled(false);
        }

        if (!Validacao.senha(senha)) {
            valido = false;
            senhaWrapper.setError(getString(R.string.erro_tamanho_senha));
        } else {
            senhaWrapper.setErrorEnabled(false);
        }

        if (!Validacao.confirmarSenha(senha, confirmarSenha)) {
            valido = false;
            confirmarSenhaWrapper.setError(getString(R.string.erro_confirmar_senha));
        } else {
            confirmarSenhaWrapper.setErrorEnabled(false);
        }

        return valido;
    }

    public void cadastrar() {
        mProgressDialog = ProgressDialog.show(CadastroActivity.this, "", getString(R.string.cadastro_pdlg_criando_conta), true);

        Boolean valido = validar();

        if (!valido) {
            mProgressDialog.dismiss();
        } else {
            String email = mEmailEditText.getText().toString();
            String senha = mSenhaEditText.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, task -> {
                        if (!task.isSuccessful()) {
                            String erro = getString(R.string.erro_firebase_cadastro_generico);
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                erro = getString(R.string.erro_firebase_cadastro_tamanho_senha);
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                erro = getString(R.string.erro_firebase_cadastro_email);
                            } catch(FirebaseAuthUserCollisionException e) {
                                erro = getString(R.string.erro_firebase_cadastro_email_usado);
                            } catch (FirebaseNetworkException e) {
                                erro = getString(R.string.erro_firebase_cadastro_internet);
                            } catch (Exception e) {
                                Log.e("AUTH", e.getMessage());
                            }
                            mProgressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
                            builder.setTitle(R.string.cadastro_erro_titulo)
                                    .setMessage(erro)
                                    .setPositiveButton(R.string.btn_ok, (dialog, id) -> dialog.cancel())
                                    .create()
                                    .show();
                        }
                    });
        }
    }

    public void trocarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE_ESCOLHER_FOTO);
    }
}
