package com.example.aptask;

import static android.os.Build.VERSION_CODES.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.lang.String;
//import java.lang.String;*/


import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends AppCompatActivity {
     //Button loginbtn,gsignbtn;
      //private ActivityMainBinding Binding;
      private static final int RC_SIGN_IN=100;
      private GoogleSignInClient googleSignInClient;
      private FirebaseAuth firebaseAuth;

      private static final String TAG="GOOGLE_SIGN_IN_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        Button loginbtn=findViewById(R.id.buttonLogin);
        Button gsignbtn=findViewById(R.id.gsign);

GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build();

            //configure google sign in
        /*GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                                .build();*/


        googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);

        //firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

            gsignbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //begin gsigning
                    Log.d(TAG,"onClick:Begin Google Signin");
                    Intent intent1 =googleSignInClient.getSignInIntent();
                    startActivityForResult(intent1,RC_SIGN_IN);
                }
            });


         loginbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(getApplicationContext(), ctask.class);
                 startActivity(intent);
             }
         });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
              if (requestCode==RC_SIGN_IN){
                  Log.d(TAG,"onActivityResult:Google intent result");
                  Task<GoogleSignInAccount> accountTask=GoogleSignIn.getSignedInAccountFromIntent(data);
                  try {

                  GoogleSignInAccount account=accountTask.getResult(ApiException.class);
                  firebaseAuthWithGooogleAccount(account);

                  }

                  catch (Exception e)
                  {
                      Log.d(TAG,"onActivityResult:"+e.getMessage());
                  }

              }
    }

    private void firebaseAuthWithGooogleAccount(GoogleSignInAccount account) {
        Log.d(TAG,"firebaseAuthWithGoogleAccount:Begin firebase auth with google account");
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG,"onSuccess:Logged In");

                        //get loggedin user
                        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

                        String uid=firebaseUser.getUid();
                        String email=firebaseUser.getEmail();

                        Log.d(TAG,"onSuccess:Email:"+email);
                        Log.d(TAG,"onSuccess:UID:"+uid);


                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            Toast.makeText(login.this, "Account Created...\n"+email, Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Log.d(TAG,"onSuccess:Existing user...\n"+email);
                            Toast.makeText(login.this, "Existing user..\n"+email, Toast.LENGTH_SHORT).show();

                        }
                        //start profile activity
//start profile activity
                        /*startActivity (new Intent( package Context: login. this, ProfileActivity.class));
                        finish();*/
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailureLogin failed"+e.getMessage());
                    }
                });

    }
}