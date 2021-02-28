package com.iwanghang.weartest;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iwanghang.weartest.whAdapter.AppListAdapter;
import com.iwanghang.weartest.whAdapter.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppsActivity extends AppCompatActivity {

    private static TextView text_info_01;
    private static TextView text_info_02;
    private static TextView text_info_03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();

        getApplication();
    }

    private ContentLoadingProgressBar mLoadingProgressBar;
    private Set<AppListAdapter.ApplicationInfoWrap> mSelectedApplicationInfos;
    private void initView() {
        mSelectedApplicationInfos = new HashSet<>();
        mLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.loading);

        mAppListView = (RecyclerView) findViewById(R.id.app_list);

        mLoadingProgressBar.show();
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryFilterAppInfo();
//                querySelectedApp();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingProgressBar.hide();
                        initAppList();
                        mAppListView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    private RecyclerView mAppListView;
    private AppListAdapter mAppAdapter;
    private List<AppListAdapter.ApplicationInfoWrap> applicationInfos = new ArrayList<>();
    private List<AppListAdapter.ApplicationInfoWrap> mCanOpenApplicationInfos = new ArrayList<>();
    private List<AppListAdapter.ApplicationInfoWrap> mShowApplicationInfos = new ArrayList<>();
    private List<AppListAdapter.ApplicationInfoWrap> mShowApplicationInfosTemp = new ArrayList<>();
    private void queryFilterAppInfo() {
        PackageManager pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        // GET_UNINSTALLED_PACKAGES 代表已删除，但还有安装目录的
        List<ApplicationInfo> appInfos= pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        // List<ApplicationInfo> applicationInfos=new ArrayList<>();

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
        List<ResolveInfo>  resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        Set<String> allowPackages=new HashSet();
        for (ResolveInfo resolveInfo:resolveinfoList){
            allowPackages.add(resolveInfo.activityInfo.packageName);
        }

        for (ApplicationInfo app:appInfos) {
////            if((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)//通过flag排除系统应用，会将电话、短信也排除掉
////            {
////                applicationInfos.add(app);
////            }
////            if(app.uid > 10000){//通过uid排除系统应用，在一些手机上效果不好
////                applicationInfos.add(app);
////            }
//            if (allowPackages.contains(app.packageName)){
//                applicationInfos.add(app);
//            }
            AppListAdapter.ApplicationInfoWrap wrap = new AppListAdapter.ApplicationInfoWrap();
            wrap.applicationInfo = app;
            if (allowPackages.contains(app.packageName)) {
                applicationInfos.add(wrap);
            }
            applicationInfos.add(wrap);
        }
        mCanOpenApplicationInfos = applicationInfos;

        // 只需要显示第三方应用
        for (AppListAdapter.ApplicationInfoWrap a : mCanOpenApplicationInfos) {
            if ((a.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0){
                Log.d("byWh", "第三方应用: " + a.applicationInfo.toString());
                mShowApplicationInfos.add(a);
            } else {
                Log.d("byWh", "系统应用: " + a.applicationInfo.toString());
            }
        }

        // 需要显示所有应用
//        mShowApplicationInfos = mCanOpenApplicationInfos;

        // mShowApplicationInfos 过滤重复
        mShowApplicationInfosTemp.addAll(mShowApplicationInfos);
        mShowApplicationInfos.clear();
        for (AppListAdapter.ApplicationInfoWrap a : mShowApplicationInfosTemp) {
            if (!mShowApplicationInfos.contains(a)) {
                Log.d("byWh", "唯一: " + a.applicationInfo.toString());
                mShowApplicationInfos.add(a);
            } else {
                Log.d("byWh", "重复: " + a.applicationInfo.toString());
            }
        }
    }

    private void initAppList() {
        mAppListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        divider.setDivider(this.getResources().getDrawable(R.drawable.situation_divider));
        mAppListView.addItemDecoration(divider);

        mAppAdapter = new AppListAdapter(this);
        mAppAdapter.setAppList(mShowApplicationInfos);
        mAppAdapter.setmListener(new AppListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isChecked) {
                if (position < 0 || position >= mShowApplicationInfos.size()) {
                    return;
                }
                int size = mSelectedApplicationInfos.size();
                final AppListAdapter.ApplicationInfoWrap select = mShowApplicationInfos.get(position);
                select.isSelected = isChecked;
                if (isChecked) {
                    mSelectedApplicationInfos.add(select);
                } else {
                    mSelectedApplicationInfos.remove(select);
                }
                if (size==mSelectedApplicationInfos.size()){
                    return;
                }
                if (mSelectedApplicationInfos.size()==0){
                    mAppAdapter.setEditMode(false);
                }
//                refreshMenu(false);
            }

            @Override
            public void onItemSpinnerChanged(int position, int selectPosition) {
                if (position < 0 || position >= mShowApplicationInfos.size()) {
                    return;
                }
                final AppListAdapter.ApplicationInfoWrap select = mShowApplicationInfos.get(position);
                select.selection=selectPosition;
//                UrlCountUtil.onEvent(UrlCountUtil.STATUS_WL_SELECTION,selectPosition+"");
            }

            @Override
            public void onLongClick(int position) {
                if (position < 0 || position >= mShowApplicationInfos.size()) {
                    return;
                }
                final AppListAdapter.ApplicationInfoWrap select = mShowApplicationInfos.get(position);
                select.isSelected=true;
                mSelectedApplicationInfos.add(select);
//                refreshMenu(true);
                mAppAdapter.setEditMode(true);
            }
        });
        mAppListView.setAdapter(mAppAdapter);
    }


}
