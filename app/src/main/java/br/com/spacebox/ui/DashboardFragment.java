package br.com.spacebox.ui;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.download_manager.DownloadManager;
import br.com.download_manager.interfaces.IDownloadManagerAction;
import br.com.download_manager.model.DownloadManagerMessage;
import br.com.download_manager.model.DownloadManagerRequest;
import br.com.filepicker_manager.Constant;
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
import br.com.spacebox.ui.base.BaseFragment;
import br.com.spacebox.utils.Util;

import static android.app.Activity.RESULT_OK;
import static br.com.filepicker_manager.ui.ImagePickActivity.IS_NEED_IMAGE_PAGER;

public class DashboardFragment extends BaseFragment {
    private String TITLE_TAG = "TITLE_TAG";
    private String SIZE_TAG = "SIZE_TAG";
    private String DATE_TAG = "DATE_TAG";
    private String ICON_TAG = "ICON_TAG";
    private String IS_DOWNLOADED_TAG = "IS_DOWNLOADED_TAG";

    boolean isMenuOpen = false;
    private ListView myListView;
    private List<FileSummaryResponse> myListViewData;
    private List<Long> openedFolders;
    private View mContentView, dashboardManagerBGLayout;

    FloatingActionButton dashboardManagerBtn, newFolderBtn, uploadFileBtn,
            uploadVideoBtn, uploadAudioBtn, uploadImageBtn, refreshFolderBtn;
    ConstraintLayout newFolderLayout, uploadFileLayout, refreshFolderLayout,
            uploadImageLayout, uploadVideoLayout, uploadAudioLayout;

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

        addListenerMenu();

        mContentView.setOnClickListener(view -> closeMenu());

        return mContentView;
    }

    private void addListenerMenu() {
        dashboardManagerBtn.setOnClickListener((view) -> {
            if (!isMenuOpen) {
                showMenu();
            } else {
                closeMenu();
            }
        });

        dashboardManagerBGLayout.setOnClickListener(view -> closeMenu());

        refreshFolderBtn.setOnClickListener((view) -> {
            synchronize(getLastFolderId());
            closeMenu();
        });

        newFolderBtn.setOnClickListener((view) -> {
            Intent intent = new Intent(getContext(), UploadFolderActivity.class);
            intent.putExtra(UploadFolderActivity.KEY_FILE_PARENT_ID, getLastFolderId());
            startActivityForResult(intent, 999);
            closeMenu();
        });

        uploadImageBtn.setOnClickListener((view) -> {
            Intent imagePickerIntent = new Intent(getContext(), ImagePickActivity.class);
            imagePickerIntent.putExtra(IS_NEED_IMAGE_PAGER, false);
            imagePickerIntent.putExtra(Constant.MAX_NUMBER, 5);
            startActivityForResult(imagePickerIntent, Constant.REQUEST_CODE_PICK_IMAGE);
            closeMenu();
        });

        uploadVideoBtn.setOnClickListener((view) -> {
            Intent intent2 = new Intent(getContext(), VideoPickActivity.class);
            intent2.putExtra(Constant.MAX_NUMBER, 5);
            startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
            closeMenu();
        });

        uploadAudioBtn.setOnClickListener((view) -> {
            Intent intent3 = new Intent(getContext(), AudioPickActivity.class);
            intent3.putExtra(Constant.MAX_NUMBER, 5);
            startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
            closeMenu();
        });

        uploadFileBtn.setOnClickListener((view) -> {
            Intent filePickerIntent = new Intent(getContext(), NormalFilePickActivity.class);
            filePickerIntent.putExtra(Constant.MAX_NUMBER, 5);
            filePickerIntent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{
                    ".xlsx", ".xls", ".doc", ".docX", ".ppt", ".pptx", ".pdf", ".html"
            });
            startActivityForResult(filePickerIntent, Constant.REQUEST_CODE_PICK_FILE);
            closeMenu();
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
            String path = null;
            for (BaseFile parc : parcelableArrayListExtra) {
                FileRequest request = new FileRequest();
                path = parc.getPath();
                request.setFileParentId(getLastFolderId());
                request.setName(path.substring(path.lastIndexOf("/") + 1));
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

    private void showMenu() {
        isMenuOpen = true;
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

    private void closeMenu() {
        isMenuOpen = false;
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
                if (!isMenuOpen) {
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
        this.myListViewData = Arrays.asList(response.getFiles());
        SimpleAdapter adapter = buildAdapter(response);
        myListView.setAdapter(adapter);
        addListenerListViewFiles(response);
    }

    private void addListenerListViewFiles(FilesResponse files) {
        myListView.setOnItemLongClickListener((parent, view, position, arg3) -> {
            Long lastElement = getLastFolderId();
            if (lastElement == null || position > 0) {
                FileSummaryResponse file = getSelectedFile(files, lastElement, position);
                showDialogYesOrNo(R.string.questionContinueExclusion, (dialog, which) -> {
                    dialog.dismiss();
                    callAPI((cli) -> cli.file().delete(sessionManager.getFullToken(), file.getId()), (response) -> {
                        synchronize(lastElement);
                    }, true);
                });
            }

            return true;
        });

        myListView.setOnItemClickListener((adapter, view, position, id) -> {
            Long lastElement = getLastFolderId();

            if (position == 0 && openedFolders.size() > 0) {
                openedFolders.remove(lastElement);
                lastElement = getLastFolderId();
                synchronize(lastElement);
            } else {
                FileSummaryResponse file = getSelectedFile(files, lastElement, position);
                if (file.getType() == null && !openedFolders.contains(file.getId())) {
                    lastElement = file.getId();
                    openedFolders.add(lastElement);
                    synchronize(lastElement);
                } else {
                    download(file, view);
                }
            }
        });
    }

    private FileSummaryResponse getSelectedFile(FilesResponse response, Long lastElement, int position) {
        int indexFile = (lastElement == null) ? position : position - 1;
        return response.getFiles()[indexFile];
    }

    private SimpleAdapter buildAdapter(FilesResponse response) {
        List<Map<String, String>> data = new ArrayList<>();

        if (response.getFilter().getFileParentId() != null)
            data.add(createAdapterDataItemBackFolder());

        if (response.getFiles() != null && response.getFiles().length > 0) {
            for (FileSummaryResponse file : response.getFiles()) {
                data.add(createAdapterDataItem(file.getName(), file.getSize(), file.getUpdated(), file.getType()));
            }
        }

        return new SimpleAdapter(getContext(), data, R.layout.item_dashboard,
                new String[]{TITLE_TAG, SIZE_TAG, DATE_TAG, ICON_TAG, IS_DOWNLOADED_TAG},
                new int[]{R.id.fileTitleTV, R.id.fileSizeTV, R.id.fileLastModifiedDateTV, R.id.fileTypeIV, R.id.downloadedIV});
    }

    private Map<String, String> createAdapterDataItemBackFolder() {
        return createAdapterDataItem(null, null, null, null);
    }

    private Map<String, String> createAdapterDataItem(String title, Long size, Date updatedDate, String type) {
        Map<String, String> datum = new HashMap<>(5);
        datum.put(TITLE_TAG, (title != null) ? title : "");
        datum.put(SIZE_TAG, (size != null) ? Util.formatToSize(size) : "");
        datum.put(DATE_TAG, (updatedDate != null) ? Util.formatToDate(updatedDate) : "......");
        datum.put(ICON_TAG, Util.getFileTypeIcon(type));

        boolean isDownloaded = false;
        if (title != null) {
            isDownloaded = DownloadManager.getInstance().isCached(getContext(), title, updatedDate);
        }

        datum.put(IS_DOWNLOADED_TAG, isDownloaded ? Integer.toString(R.drawable.downloaded) : "");
        return datum;
    }

    private Long getLastFolderId() {
        int index = openedFolders.size() - 1;
        return (index >= 0) ? openedFolders.get(openedFolders.size() - 1) : null;
    }

    private void openFile(DownloadManagerMessage response) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_VIEW);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setDataAndType(response.getFile(), response.getMimeType());
        startActivity(shareIntent);
    }

    private void download(FileSummaryResponse file, View view) {
        IDownloadManagerAction onBeforeDownload = (message) -> {
            ProgressBar progress = view.findViewById(R.id.progressBarDownload);
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(message.getProgress());
        };

        IDownloadManagerAction onProgressDownload = (message) -> {
            ProgressBar progress = view.findViewById(R.id.progressBarDownload);
            progress.setProgress(message.getProgress());
        };

        IDownloadManagerAction onCompleteDownload = (message) -> {
            ProgressBar progress = view.findViewById(R.id.progressBarDownload);
            progress.setVisibility(View.GONE);
            progress.setProgress(0);

            ImageView imageView = view.findViewById(R.id.downloadedIV);
            imageView.setImageResource(R.drawable.downloaded);

            if (message.isFromCache())
                openFile(message);
        };

//        DownloadManager.Request request = DownloadManager.Request.Builder.create()
//                .withUri(SpaceBoxConst.FILE_API_CLIENT_BASE_URL_DOWNLOAD + file.getId())
//                .withAuthorization(sessionManager.getFullToken())
//                .withEnableNotification(true)
//                .withUpdateDate(file.getUpdated())
//                .withFileName(file.getName())
//                .withContext(getContext())
//
//                .withOnCachedDownload(onCachedDownload)
//                .withOnBeforeDownload(onBeforeDownload)
//                .withOnProgressDownload(onProgressDownload)
//                .withOnCompleteDownload(onCompleteDownload)
//                .build();
//
//        DownloadManager.getInstance().enqueue(request);


        DownloadManagerRequest request = DownloadManagerRequest.Builder.create()
                .withUri("https://blog.kitei.com.br/wp-content/uploads/2018/07/215241-limpar-o-nome-online-como-escolher-o-melhor-site-para-isso.jpg")
                .withFileName(file.getName())
                .withContext(getContext())
                .withEnableNotification(true)

                .withOnBeforeDownload(onBeforeDownload)
                .withOnProgressDownload(onProgressDownload)
                .withOnCompleteDownload(onCompleteDownload)
                .build();

        DownloadManager.getInstance().enqueue(request);
    }
}
