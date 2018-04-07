package com.neklien.proximatetestandroid.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neklien.proximatetestandroid.AppDelegate;
import com.neklien.proximatetestandroid.R;
import com.neklien.proximatetestandroid.helpers.retrofit.LoginBody;
import com.neklien.proximatetestandroid.helpers.retrofit.RestApi;
import com.neklien.proximatetestandroid.helpers.retrofit.ServerResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;

    private Call<ServerResponse> call;

    private ProgressBar progressBar;

    private AlertDialog alertDialog;

    private Boolean isAttached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email_al);
        etPassword = findViewById(R.id.et_password_al);

        progressBar = findViewById(R.id.pb_al);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    public void sendLogin(View view) {
        if (!validateEditText(this, etEmail)) {
            return;
        }

        if (!validateEditText(this, etPassword)) {
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            Toast.makeText(this, "Correo electrónico inválido.", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        attemptLogin(etEmail.getText().toString(), etPassword.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(alertDialog != null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    public static boolean validateEditText(Context context, EditText editText) {
        String text = editText.getText().toString();

        if (text.trim().isEmpty()) {
            Toast.makeText(context, "Campo " + editText.getHint().toString() + " vacío.", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return false;
        }

        return true;
    }

    private void attemptLogin(String email, String password) {
        showActivityIndicator();

        final RestApi restApi = ((AppDelegate) getApplication()).getRestApi();

        LoginBody loginBody = new LoginBody();
        loginBody.setEmail(email);
        loginBody.setPassword(password);

        call = restApi.sendLogin(loginBody);

        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                hideActivityIndicator();

                if (response.isSuccessful()) {
                    showAlertMessage(response.body().getMessage());
                } else {
                    showAlertMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    hideActivityIndicator();
                    showAlertMessage("Ocurrió un error, por favor reintente más tarde.");
                }
            }
        });
    }

    private void showActivityIndicator() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideActivityIndicator() {
        progressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showAlertMessage(String message) {
        if (alertDialog == null) {
            AlertDialog.Builder builderDialog = new AlertDialog.Builder(this)
                    .setTitle("Aviso")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = (getSharedPreferences("Preferences", Context.MODE_PRIVATE)).edit();

                            editor.putString("email_preferences", etEmail.getText().toString());
                            editor.putString("password_preferences", etPassword.getText().toString());
                            editor.putString("token_preferences", "aToken");

                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                            finish();
                        }
                    });

            if(isAttached) {
                alertDialog = builderDialog.show();
            }
        }
    }
}
