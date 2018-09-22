package br.com.filepicker_manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import br.com.filepicker_manager.R;
import br.com.filepicker_manager.Constant;
import br.com.filepicker_manager.ToastUtil;
import br.com.filepicker_manager.filter.FileFilter;
import br.com.filepicker_manager.filter.callback.FilterResultCallback;
import br.com.filepicker_manager.filter.entity.Directory;
import br.com.filepicker_manager.filter.entity.ImageFile;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ImageBrowserActivity extends FilePickerBaseActivity {
    public static final String IMAGE_BROWSER_INIT_INDEX = "ImageBrowserInitIndex";
    public static final String IMAGE_BROWSER_SELECTED_LIST = "ImageBrowserSelectedList";
    private int mMaxNumber;
    private int mCurrentNumber = 0;
    private int initIndex = 0;
    private int mCurrentIndex = 0;

    private ViewPager mViewPager;
    private Toolbar mTbImagePick;
    private ArrayList<ImageFile> mList = new ArrayList<>();
    private ImageView mSelectView;
    private ArrayList<ImageFile> mSelectedFiles;

    @Override
    void permissionGranted() {
        loadData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.vw_activity_image_browser);

        mMaxNumber = getIntent().getIntExtra(Constant.MAX_NUMBER, ImagePickActivity.DEFAULT_MAX_NUMBER);
        initIndex = getIntent().getIntExtra(IMAGE_BROWSER_INIT_INDEX, 0);
        mCurrentIndex = initIndex;
        mSelectedFiles = getIntent().getParcelableArrayListExtra(IMAGE_BROWSER_SELECTED_LIST);
        mCurrentNumber = mSelectedFiles.size();


        super.onCreate(savedInstanceState);
    }

    private void initView() {
        mTbImagePick = (Toolbar) findViewById(R.id.tb_image_pick);
        mTbImagePick.setTitle(mCurrentNumber + "/" + mMaxNumber);
        setSupportActionBar(mTbImagePick);
        mTbImagePick.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishThis();
            }
        });

        mSelectView = (ImageView) findViewById(R.id.cbx);
        mSelectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isSelected() && isUpToMax()) {
                    ToastUtil.getInstance(ImageBrowserActivity.this).showToast(R.string.vw_up_to_max);
                    return;
                }

                if (v.isSelected()) {
                    mList.get(mCurrentIndex).setSelected(false);
                    mCurrentNumber--;
                    v.setSelected(false);
                    mSelectedFiles.remove(mList.get(mCurrentIndex));
                } else {
                    mList.get(mCurrentIndex).setSelected(true);
                    mCurrentNumber++;
                    v.setSelected(true);
                    mSelectedFiles.add(mList.get(mCurrentIndex));
                }

                mTbImagePick.setTitle(mCurrentNumber + "/" + mMaxNumber);
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.vp_image_pick);
        mViewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mViewPager.setAdapter(new ImageBrowserAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
                mSelectView.setSelected(mList.get(mCurrentIndex).isSelected());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(initIndex, false);
        mSelectView.setSelected(mList.get(mCurrentIndex).isSelected());
    }

    private void loadData() {
        FileFilter.getImages(this, new FilterResultCallback<ImageFile>() {
            @Override
            public void onResult(List<Directory<ImageFile>> directories) {
                mList.clear();
                for (Directory<ImageFile> directory : directories) {
                    mList.addAll(directory.getFiles());
                }

                for (ImageFile file : mList) {
                    if (mSelectedFiles.contains(file)) {
                        file.setSelected(true);
                    }
                }

                initView();
                mViewPager.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private class ImageBrowserAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView view = new PhotoView(ImageBrowserActivity.this);
            view.enable();
            view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            Glide.with(ImageBrowserActivity.this)
                    .load(mList.get(position).getPath())
                    .transition(withCrossFade())
                    .into(view);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vw_menu_image_pick, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            finishThis();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isUpToMax() {
        return mCurrentNumber >= mMaxNumber;
    }

    private void finishThis() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Constant.RESULT_BROWSER_IMAGE, mSelectedFiles);
//        intent.putExtra(IMAGE_BROWSER_SELECTED_NUMBER, mCurrentNumber);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishThis();
    }
}
