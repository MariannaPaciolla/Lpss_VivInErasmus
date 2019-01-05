package com.example.marianna.vivinerasmus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //Widget
    private EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private Button mLogin;
    private TextView mSignUp;

    private final static String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Attributi dei widget
        mLogin = (Button) findViewById(R.id.btnLogin);
        mEmail = (EditText) findViewById(R.id.editEmail);
        mPassword = (EditText) findViewById(R.id.editPassword);
        mSignUp = (TextView) findViewById(R.id.textSignUp);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistratiActivity.class));
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "ATTENDI….", Toast.LENGTH_LONG).show();
                //trim: elimino gli spazi
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkEsistenzaUser(task);
                            } else {
                                Toast.makeText(LoginActivity.this, "Login fallito: username o password errata", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Completa tutti i campi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkEsistenzaUser(@NonNull final Task<AuthResult> task) {
        final String user_id = mAuth.getCurrentUser().getUid();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //restituisce true se il child user_id ha dati.
                if (dataSnapshot.hasChild(user_id)) {
                    Log.d(TAG, "Task completato: successo = " + task.isSuccessful());
                    startActivity(new Intent(LoginActivity.this, ListaActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "User non registrato.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

//TODO :signout https://firebase.google.com/docs/auth/web/password-auth