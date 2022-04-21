package com.example.sqlightnews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class LogInActivity extends AppCompatActivity {

    Intent intent;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    CheckBox showPassword;
    EditText txtLogin, txtPassword;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initialize();
        showPassword.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LogInActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Авторизация")
                .setSubtitle("Прислоните палец")
                .setNegativeButtonText("Отмена")
                .build();
    }

    private void initialize() {
        showPassword = findViewById(R.id.showPassword);
        txtLogin = findViewById(R.id.txtLogin);
        txtPassword = findViewById(R.id.txtPassword);
        databaseHelper = new DatabaseHelper(this);
    }

    public void enterClick(View view) {
        Cursor res = databaseHelper.getData(txtLogin.getText().toString().trim(), txtPassword.getText().toString().trim());
        if (res.getCount() == 0) {
            Toast.makeText(LogInActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {
            if (res.getString(7).equals("Администратор")) {
                intent = new Intent(LogInActivity.this, AllNewsActivityAdministrator.class).putExtra("Id", res.getInt(0));
            } else {
                intent = new Intent(LogInActivity.this, AllNewsActivity.class);
            }
        }
        biometricPrompt.authenticate(promptInfo);
    }

    public void registrationClick(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }
}