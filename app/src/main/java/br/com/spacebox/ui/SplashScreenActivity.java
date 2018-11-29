package br.com.spacebox.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;

import br.com.spacebox.R;
import br.com.spacebox.api.model.request.FileRequest;
import br.com.spacebox.ui.base.BaseActivity;
import br.com.spacebox.utils.Util;

public class SplashScreenActivity extends BaseActivity {
    private ProgressBar mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mProgress = findViewById(R.id.splash_screen_progress_bar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if(Intent.ACTION_SEND.equals(action) && type != null || (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null)){
            if (sessionManager.isLoggedIn()) {
                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if (type.startsWith("image/")) {
                        handleSendImage(intent); // Handle single image being sent
                    }
                } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                    if (type.startsWith("image/")) {
                        handleSendMultipleImages(intent); // Handle multiple images being sent
                    }
                } else {
                    startApp();
                }
            } else {
                startApp();
                toastMessage(R.string.http_error_401);
            }
        }else{
            new Thread(() -> {
                doWork();
                startApp();
                finish();
            }).start();
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            File file = new File(imageUri.toString());
            FileRequest request = new FileRequest();
            request.setFileParentId(null);
            request.setName(file.getName());
            request.setType(getMimeType(imageUri));
            request.setSize(file.length());
            request.setContent(Util.encodeFileToBase64Binary(file.getPath()));


            callAPI((cli) -> cli.file().create(sessionManager.getFullToken(), request), (response) -> {
                toastMessage(R.string.successMessage);
                startActivity(MasterActivity.class);
            });
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    private void doWork() {
        for (int progress = 0; progress <= 100; progress += 25) {
            try {
                Thread.sleep(1000);
                mProgress.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startApp() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
