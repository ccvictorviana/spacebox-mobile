package br.com.spacebox.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.com.spacebox.R;
import br.com.spacebox.api.model.request.FileRequest;
import br.com.spacebox.ui.base.BaseActivity;

public class UploadFolderActivity extends BaseActivity {
    public static final String KEY_FILE_PARENT_ID = "KEY_FILE_PARENT_ID";
    private Long fileParentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_folder);
        setTitle(R.string.uploadTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileParentId = getIntent().getLongExtra(KEY_FILE_PARENT_ID, 0);
        fileParentId = (fileParentId == 0) ? null : fileParentId;

        findViewById(R.id.createFolderBtn).setOnClickListener(this::onCreateFolderBtn);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void onCreateFolderBtn(View v) {
        EditText name = findViewById(R.id.nameFolderText);

        FileRequest request = new FileRequest();
        request.setName(name.getText().toString());
        request.setFileParentId(fileParentId);

        callAPI((cli) -> cli.file().create(sessionManager.getFullToken(), request), (response) -> {
            setResult(Activity.RESULT_OK);
            toastMessage(R.string.successMessage);
            finish();
        });
    }
}
