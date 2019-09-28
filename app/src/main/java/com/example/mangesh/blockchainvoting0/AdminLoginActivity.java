package com.example.mangesh.blockchainvoting0;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AdminLoginActivity extends AppCompatActivity implements FingerprintHandler.FingerprintHelperListener{

    public TextView textView;
    private ImageView imageView;
    private Intent intent;
    FingerprintHandler fingerprintHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        intent = new Intent(AdminLoginActivity.this, AdminActivity.class);

        textView = findViewById(R.id.tvInfo);
        imageView = findViewById(R.id.imageView);

        fingerprintHandler = new FingerprintHandler(AdminLoginActivity.this, intent);

        fingerprintHandler.keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintHandler.fingerprintManager =
                (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        try {
            fingerprintHandler.generateKey();
        } catch (FingerprintHandler.FingerprintException e) {
            e.printStackTrace();
        }

        if (fingerprintHandler.initCipher()) {
            //If the cipher is initialized successfully, then create a CryptoObject instance//
            fingerprintHandler.cryptoObject = new FingerprintManager.CryptoObject(fingerprintHandler.cipher);

            // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
            // for starting the authentication process (via the startAuth method) and processing the authentication process events//

            fingerprintHandler.startAuth(fingerprintHandler.fingerprintManager, fingerprintHandler.cryptoObject);

        }

    }

    @Override
    public void authenticationFailed(String error) {

        Toast.makeText(AdminLoginActivity.this, "Authentication failed!!!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void authenticationSuccess(String message) {

        Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
        startActivity(intent);
        finish();

    }

}