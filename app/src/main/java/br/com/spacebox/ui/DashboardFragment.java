package br.com.spacebox.ui;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.filepicker_manager.filter.entity.BaseFile;
import br.com.filepicker_manager.ui.AudioPickActivity;
import br.com.filepicker_manager.ui.ImagePickActivity;
import br.com.filepicker_manager.ui.NormalFilePickActivity;
import br.com.filepicker_manager.ui.VideoPickActivity;
import br.com.spacebox.R;
import br.com.spacebox.api.model.request.FileFilterRequest;
import br.com.spacebox.api.model.request.FileRequest;
import br.com.spacebox.api.model.response.FileSummaryResponse;
import br.com.spacebox.api.model.response.FilesResponse;
import br.com.filepicker_manager.Constant;
import br.com.spacebox.utils.Util;

import static android.app.Activity.RESULT_OK;
import static br.com.filepicker_manager.ui.ImagePickActivity.IS_NEED_CAMERA;
import static br.com.filepicker_manager.ui.ImagePickActivity.IS_NEED_IMAGE_PAGER;

public class DashboardFragment extends BaseFragment {
    private ListView myListView;
    private List<Long> openedFolders;
    private View mContentView, dashboardManagerBGLayout;

    FloatingActionButton dashboardManagerBtn, newFolderBtn, uploadFileBtn,
            uploadVideoBtn, uploadAudioBtn, uploadImageBtn, refreshFolderBtn;
    ConstraintLayout newFolderLayout, uploadFileLayout, refreshFolderLayout,
            uploadImageLayout, uploadVideoLayout, uploadAudioLayout;
    boolean isFABOpen = false;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openedFolders = new ArrayList<>();
        synchronize(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        myListView = mContentView.findViewById(R.id.fileListView);
        myListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        dashboardManagerBGLayout = mContentView.findViewById(R.id.dashboardManagerBGLayout);
        newFolderLayout = mContentView.findViewById(R.id.newFolderLayout);
        uploadFileLayout = mContentView.findViewById(R.id.uploadFileLayout);
        uploadImageLayout = mContentView.findViewById(R.id.uploadImageLayout);
        uploadVideoLayout = mContentView.findViewById(R.id.uploadVideoLayout);
        uploadAudioLayout = mContentView.findViewById(R.id.uploadAudioLayout);
        refreshFolderLayout = mContentView.findViewById(R.id.refreshFolderLayout);

        dashboardManagerBtn = mContentView.findViewById(R.id.dashboardManagerBtn);
        newFolderBtn = mContentView.findViewById(R.id.newFolderBtn);
        uploadFileBtn = mContentView.findViewById(R.id.uploadFileBtn);
        uploadImageBtn = mContentView.findViewById(R.id.uploadImageBtn);
        uploadVideoBtn = mContentView.findViewById(R.id.uploadVideoBtn);
        uploadAudioBtn = mContentView.findViewById(R.id.uploadAudioBtn);
        refreshFolderBtn = mContentView.findViewById(R.id.refreshFolderBtn);

        addListenerDashboardMenu();

        mContentView.setOnClickListener(view -> closeFABMenu());

        return mContentView;
    }

    private void addListenerDashboardMenu() {
        dashboardManagerBtn.setOnClickListener((view) -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });

        dashboardManagerBGLayout.setOnClickListener(view -> closeFABMenu());

        refreshFolderBtn.setOnClickListener((view) -> {
            synchronize(getLastFolderId());
            closeFABMenu();
        });

        newFolderBtn.setOnClickListener((view) -> {
            Intent intent = new Intent(getContext(), UploadFolderActivity.class);
            intent.putExtra(UploadFolderActivity.KEY_FILE_PARENT_ID, getLastFolderId());
            startActivityForResult(intent, 999);
            closeFABMenu();
        });

        uploadImageBtn.setOnClickListener((view) -> {
            Intent imagePickerIntent = new Intent(getContext(), ImagePickActivity.class);
            imagePickerIntent.putExtra(IS_NEED_IMAGE_PAGER, false);
            imagePickerIntent.putExtra(Constant.MAX_NUMBER, 5);
            startActivityForResult(imagePickerIntent, Constant.REQUEST_CODE_PICK_IMAGE);
            closeFABMenu();
        });

        uploadVideoBtn.setOnClickListener((view) -> {
            Intent intent2 = new Intent(getContext(), VideoPickActivity.class);
            intent2.putExtra(Constant.MAX_NUMBER, 5);
            startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
            closeFABMenu();
        });

        uploadAudioBtn.setOnClickListener((view) -> {
            Intent intent3 = new Intent(getContext(), AudioPickActivity.class);
            intent3.putExtra(Constant.MAX_NUMBER, 5);
            startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
            closeFABMenu();
        });

        uploadFileBtn.setOnClickListener((view) -> {
            Intent filePickerIntent = new Intent(getContext(), NormalFilePickActivity.class);
            filePickerIntent.putExtra(Constant.MAX_NUMBER, 5);
            filePickerIntent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{
                    ".xlsx", ".xls", ".doc", ".docX", ".ppt", ".pptx", ".pdf", ".html"
            });
            startActivityForResult(filePickerIntent, Constant.REQUEST_CODE_PICK_FILE);
            closeFABMenu();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == Constant.REQUEST_CODE_PICK_FILE ||
                requestCode == Constant.REQUEST_CODE_PICK_IMAGE || requestCode == Constant.REQUEST_CODE_PICK_VIDEO
                || requestCode == Constant.REQUEST_CODE_PICK_AUDIO)) {
            String pickType = Constant.RESULT_PICK_FILE;

            if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE)
                pickType = Constant.RESULT_PICK_IMAGE;
            else if (requestCode == Constant.REQUEST_CODE_PICK_AUDIO)
                pickType = Constant.RESULT_PICK_AUDIO;
            else if (requestCode == Constant.REQUEST_CODE_PICK_VIDEO)
                pickType = Constant.RESULT_PICK_VIDEO;

            startUpload(data.getParcelableArrayListExtra(pickType));
        } else if (requestCode == 999) {
            synchronize(getLastFolderId());
        }
    }

    private <T extends BaseFile> void startUpload(ArrayList<T> parcelableArrayListExtra) {
        if (parcelableArrayListExtra != null && parcelableArrayListExtra.size() > 0) {
            showLoading();
            for (BaseFile parc : parcelableArrayListExtra) {
                FileRequest request = new FileRequest();
                request.setFileParentId(getLastFolderId());
                request.setName(parc.getName());
                request.setType(parc.getMimeType());
                request.setSize(parc.getSize());
                request.setContent(Util.encodeFileToBase64Binary(parc.getPath()));

                callAPI((cli) -> cli.file().create(sessionManager.getFullToken(), request), (response) -> {
                    synchronize(getLastFolderId());
                    toastMessage(R.string.successMessage);
                });
            }
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        newFolderLayout.setVisibility(View.VISIBLE);
        uploadFileLayout.setVisibility(View.VISIBLE);
        uploadImageLayout.setVisibility(View.VISIBLE);
        uploadAudioLayout.setVisibility(View.VISIBLE);
        uploadVideoLayout.setVisibility(View.VISIBLE);
        refreshFolderLayout.setVisibility(View.VISIBLE);
        mContentView.setVisibility(View.VISIBLE);
        dashboardManagerBGLayout.setVisibility(View.VISIBLE);

        dashboardManagerBtn.animate().rotationBy(180);
        newFolderLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        uploadFileLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        uploadImageLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
        uploadAudioLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_190));
        uploadVideoLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_235));
        refreshFolderLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_280));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        dashboardManagerBGLayout.setVisibility(View.GONE);
        dashboardManagerBtn.animate().rotationBy(-180);
        newFolderLayout.animate().translationY(0);
        uploadFileLayout.animate().translationY(0);
        uploadImageLayout.animate().translationY(0);
        uploadAudioLayout.animate().translationY(0);
        uploadVideoLayout.animate().translationY(0);
        refreshFolderLayout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    newFolderLayout.setVisibility(View.GONE);
                    uploadFileLayout.setVisibility(View.GONE);
                    uploadImageLayout.setVisibility(View.GONE);
                    uploadVideoLayout.setVisibility(View.GONE);
                    uploadAudioLayout.setVisibility(View.GONE);
                    refreshFolderLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void synchronize(Long fileParentId) {
        FileFilterRequest request = new FileFilterRequest();
        request.setFileParentId(fileParentId);

        callAPI((cli) -> cli.file().list(sessionManager.getFullToken(), request), (response) -> {
            refreshUI(response);
        });
    }

    private void refreshUI(FilesResponse response) {
        myListView.setAdapter(createListData(response));
        myListView.setOnItemClickListener((adapter, view, position, id) -> {
            Long lastElement = getLastFolderId();

            if (position == 0 && openedFolders.size() > 0) {
                openedFolders.remove(lastElement);
                lastElement = getLastFolderId();
            } else {
                int indexFile = (lastElement == null) ? position : position - 1;
                FileSummaryResponse file = response.getFiles()[indexFile];
                if (file.getType() == null && !openedFolders.contains(file.getId())) {
                    lastElement = file.getId();
                    openedFolders.add(lastElement);
                } else {
                    download(file);
                }
            }

            synchronize(lastElement);
        });
    }

    private SimpleAdapter createListData(FilesResponse response) {
        String TITLE_TAG = "TITLE_TAG";
        String SIZE_TAG = "SIZE_TAG";
        String DATE_TAG = "DATE_TAG";
        String ICON_TAG = "ICON_TAG";

        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> datum;
        if (response.getFilter().getFileParentId() != null) {
            datum = new HashMap<>(2);
            datum.put(TITLE_TAG, "");
            datum.put(SIZE_TAG, "");
            datum.put(DATE_TAG, "......");
            datum.put(ICON_TAG, Util.getFileTypeIcon(null));
            data.add(datum);
        }

        if (response.getFiles() != null && response.getFiles().length > 0) {
            for (FileSummaryResponse file : response.getFiles()) {
                datum = new HashMap<>(2);
                datum.put(TITLE_TAG, file.getName());
                datum.put(SIZE_TAG, Util.formatToSize(file.getSize()));
                datum.put(DATE_TAG, Util.formatToDate(file.getUpdated()));
                datum.put(ICON_TAG, Util.getFileTypeIcon(file.getType()));
                data.add(datum);
            }
        }

        return new SimpleAdapter(getContext(), data, R.layout.item_dashboard,
                new String[]{TITLE_TAG, SIZE_TAG, DATE_TAG, ICON_TAG},
                new int[]{R.id.fileTitleTV, R.id.fileSizeTV, R.id.fileLastModifiedDateTV, R.id.fileTypeIV});
    }

    private Long getLastFolderId() {
        int index = openedFolders.size() - 1;
        return (index >= 0) ? openedFolders.get(openedFolders.size() - 1) : null;
    }

    private void download(FileSummaryResponse file) {
//        DownloadManager.Request request = DownloadManager.Request.Builder.create()
//                .withUri("")
//                .withId(file.getId() + "_" + file.getUpdated().getTime())
//                .withFileName(file.getName())
//                .withAuthorization(sessionManager.getFullToken())
//                .withTitle("Zip package")
//                .withDescription("A zip package with some files")
//                .build();
//
//        // get download service and enqueue file
//        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
//        manager.enqueue(request);
    }
}
