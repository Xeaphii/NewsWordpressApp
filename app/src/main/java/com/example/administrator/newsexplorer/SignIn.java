package com.example.administrator.newsexplorer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Administrator on 7/7/2015.
 */
public class SignIn extends Activity {
    EditText Email,Password;
    Button SignInClick;
    StorageSharedPref sharedStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Error","");
//        try {
//            PackageInfo info = null;
//            try {
//                info = getPackageManager().getPackageInfo(
//                        "com.example.administrator.socialbot", PackageManager.GET_SIGNATURES);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:",
//                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
        sharedStorage = new StorageSharedPref(SignIn.this);
        if(sharedStorage.GetPrefs("user_id",null)!=null){
            if(sharedStorage.GetPrefs("confirm_user",null).equals("0")){
                Intent intent = new Intent(this, ConfirmRegistration.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // This closes the login screen so it's not on the back stack
            }
        }

        setContentView(R.layout.ui_parse_login_fragment);
        //getActionBar().setHomeButtonEnabled(true);
        Email= (EditText) findViewById(R.id.login_username_input);

        Password= (EditText) findViewById(R.id.login_password_input);

        SignInClick= (Button) findViewById(R.id.parse_login_button);
        SignInClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if (Password.getText().toString().length() > 6) {

                        if (!Email.getText().toString().equals("")) {

                            new SignUpTask(SignIn.this).execute(new String[]{Email.getText().toString(), Password.getText().toString()});

                        } else {
                            Toast.makeText(getApplicationContext(), "Number should'nt be empty", Toast.LENGTH_LONG).show();
                        }


                    } else {
                        Toast.makeText(getApplicationContext(), "Password must be of greater than 6 characters", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection present", Toast.LENGTH_LONG).show();
                }
            }
        });
        ((Button) findViewById(R.id.parse_signup_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, Signup.class);
                startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.parse_login_help)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

    }

    class SignUpTask extends AsyncTask<String, Void, Integer> {

        private ProgressDialog dialog;
        Context context;
        public SignUpTask(Context c) {
            dialog = new ProgressDialog(c);
            context= c;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Progress start");
            this.dialog.show();
        }

        @Override
        protected Integer doInBackground(String... urls) {

                try {
                    //------------------>>
                    HttpGet httppost = new HttpGet(("http://ghanchidarpan.org/news/SignIn.php?proj_email=" +
                            encodeHTML(urls[0]) +
                            "&proj_password=" +
                            encodeHTML(urls[1])).replaceAll(" ", "%20") );
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(httppost);

                    // StatusLine stat = response.getStatusLine();
                    int status = response.getStatusLine().getStatusCode();

                    if (status == 200) {
                        HttpEntity entity = response.getEntity();
                        String data = EntityUtils.toString(entity);
                        if(data.equals("404:")){
                            return 404;
                        }else{
                            sharedStorage.StorePrefs("user_id",data.split(":")[1].trim());
                            sharedStorage.StorePrefs("confirm_user",data.split(":")[2].trim());
                            return 200;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }

            return 0;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(success==200){
              //  sharedStorage.StorePrefs("login_cred","1");
                showHomeListActivity();
            }else if(success==404){
                Toast.makeText(context,"Wrong Password or email",Toast.LENGTH_LONG).show();
            }else if(success==0){
                Toast.makeText(context,"Some error occurred",Toast.LENGTH_LONG).show();
            }
        }
    }


    public static String encodeHTML(String s)
    {
        StringBuffer out = new StringBuffer();
        for(int i=0; i<s.length(); i++)
        {
            char c = s.charAt(i);
            if(c > 127 || c=='"' || c=='<' || c=='>')
            {
                out.append("&#"+(int)c+";");
            }
            else
            {
                out.append(c);
            }
        }
        return out.toString();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void showHomeListActivity() {
        if(sharedStorage.GetPrefs("confirm_user",null).equals("0")){
            Intent intent = new Intent(this, ConfirmRegistration.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // This closes the login screen so it's not on the back stack
        }
    }

}