package example.co.mz.uberclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private  final String TAG = getClass().getSimpleName();
    @BindView(R.id.button_reg)
    public Button register;
    @BindView(R.id.button_login)
    public Button login;
    private DatabaseReference dataRef;

    private FirebaseAuth auth;

    public final static String PERSON = "PERSON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();

        dataRef = FirebaseDatabase.getInstance().getReference(PERSON);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this,MapsActivity.class));
            finish();
        }
    }

    @OnClick(R.id.button_reg)
    public void registerUser(View view) {
        showRegisterDialog();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_register, null, false);


        final EditText nome = view.findViewById(R.id.sign_in_nome);
        final EditText email = view.findViewById(R.id.sign_in_email);
        final EditText chave = view.findViewById(R.id.sign_in_password);

        alertBuilder.setView(view);

        alertBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertBuilder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(nome.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(chave.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter pass", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email.getText().toString(), chave.getText().toString())
                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String id = authResult.getUser().getUid();

                                Person person = new Person();
                                person.setName(nome.getText().toString());
                                person.setEmail(email.getText().toString());
                                person.setId(id);

                                dataRef.child(id)
                                        .setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, " Authenticated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure() called with: e = [" + e + "]");
                                    }
                                });




                            }
                        })
                        .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Failed Authentication", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onFailure() called with: e = [" + e + "]");
                            }
                        });


            }
        });

        alertBuilder.create().show();
    }

    @OnClick(R.id.button_login)
    public void loginUser(View view) {
        showLoginDialog();
    }

    private void showLoginDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.layout_login, null, false);

        final EditText email = view.findViewById(R.id.log_in_email);
        final EditText chave = view.findViewById(R.id.log_in_password);

        alertBuilder.setView(view);

        alertBuilder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(chave.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter pass", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email.getText().toString(), chave.getText().toString())
                        .addOnSuccessListener(MainActivity.this,
                                new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        startActivity(new Intent(MainActivity.this, MapsActivity.class));
                                        finish();
                                    }
                                })
                        .addOnFailureListener(MainActivity.this,
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


            }
        });
        alertBuilder.create().show();

    }


}
