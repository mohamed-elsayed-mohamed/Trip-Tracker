package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    EditText emailTxt,passwordTxt;
    Button login;
    TextView register;

    private static final int RC_SIGN_IN = 1000;
    private FirebaseAuth mAuth;
    com.google.android.gms.common.SignInButton googleBtn;
    GoogleSignInClient mGoogleSignInClient;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();



        // Session Manager
        session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        if(session.isLoggedIn()){
            // check firebase user-email is exists or not --> this happend in state of deleting user from firebase


                Intent intent = new Intent(Login.this,MainActivity.class);
                startActivity(intent);

        }

        emailTxt = findViewById(R.id.emailEditTxt);
        passwordTxt = findViewById(R.id.passwordEditTxt);
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.registerTxt);
        googleBtn = findViewById(R.id.googleBtn);






        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(Login.this, Register.class);
                startActivity(registerIntent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();

                loginAuthentication(email,password);

            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

    }

    private void loginAuthentication(String email, String password) {

        if(!email.isEmpty() )
        {
            if(!password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Login.this, "Authentication succeeded.",
                                            Toast.LENGTH_SHORT).show();

                                    int index = email.indexOf('@');
                                    String username = email.substring(0,index);
                                    username = username.substring(0, 1).toUpperCase() + username.substring(1);
                                    Log.i("tag",username);

                                    session.createLoginSession(email,username,"");


                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
//
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Wrong email or password !",
                                            Toast.LENGTH_SHORT).show();
//
                                }
                            }
                        });
            }
            else
            {
                passwordTxt.setError("Please enter password!");
            }
        }
        else
        {
            emailTxt.setError("Please enter email!");
        }
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Login.this, "login with google succeeded", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Login.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            Log.i("tag",""+user.getPhotoUrl());
                            Log.i("tag",""+user.getDisplayName());

                            session.createLoginSession(user.getEmail(),user.getDisplayName(),user.getPhotoUrl().toString());
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "signInWithCredential:failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}