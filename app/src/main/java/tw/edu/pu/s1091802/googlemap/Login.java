package tw.edu.pu.s1091802.googlemap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


public class Login extends AppCompatActivity
{

    public static final String TAG = Login.class.getSimpleName() + "My";
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("222107848046-kl8v2hsib3m3ubuki24lk11pok6evo3m.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this , gso);
        // [END config_signin]

        SignInButton btSighIn = findViewById(R.id.button_SignIn);
        btSighIn.setOnClickListener(v->{
            startActivityForResult(mGoogleSignInClient.getSignInIntent() , 200);
        });
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode , int resultCode , Intent data)
    {
        super.onActivityResult(requestCode , resultCode , data);

        if (requestCode == 200)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String result = "登入成功\nEmail：" + account.getEmail() + "\nGoogle名稱：" + account.getDisplayName();
                Log.d(TAG , "Token: " + account.getIdToken());
                TextView tvResult = findViewById(R.id.textView_Result);
                tvResult.setText(result);

                Intent intent = new Intent(Login.this , MapsActivity.class);    //從登入畫面跳到主畫面
                startActivity(intent);
                finish();
            }
            catch (ApiException e)
            {
                Log.w(TAG , "Google sign in failed" , e);
            }
        }
    }
}