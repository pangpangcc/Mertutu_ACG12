package com.moetutu.acg12.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.moetutu.acg12.R;
import com.moetutu.acg12.activity.WenDangActivity;
import com.moetutu.acg12.adapter.ComicShufflingAdapter;
import com.moetutu.acg12.adapter.TuJiAdapter;
import com.moetutu.acg12.app.AppContext;
import com.moetutu.acg12.entity.ChMedicCircleRecEntity;
import com.moetutu.acg12.entity.TestMode;
import com.moetutu.acg12.http.RetrofitService;
import com.moetutu.acg12.http.callback.SimpleCallBack;
import com.moetutu.acg12.util.Const;
import com.moetutu.acg12.util.LogUtils;
import com.moetutu.acg12.util.SwipyAppBarScrollListener;
import com.moetutu.acg12.view.ZoomOutPageTransformer;
import com.moetutu.acg12.view.refresh.MaterialRefreshLayout;
import com.moetutu.acg12.view.refresh.MaterialRefreshListener;
import com.moetutu.acg12.view.widget.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Response;

public class FeagementComicOne extends LazyBaseFragment implements View.OnClickListener, BaseRecyclerAdapter.OnItemClickListener {
    View rootView;
    @InjectView(R.id.header_viewPager)
    ViewPager headerViewPager;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.myRefreshLayout)
    MaterialRefreshLayout myRefreshLayout;
    @InjectView(R.id.ctv_newest)
    CheckedTextView ctvNewest;
    @InjectView(R.id.ctv_type)
    CheckedTextView ctvType;
    @InjectView(R.id.ctv_statu)
    CheckedTextView ctvStatu;

    private ComicShufflingAdapter chMedCircImgAdapter;
    public AppContext appContext;
    private int PageIndex = 1;

    TuJiAdapter tuadapter;

    public static FeagementComicOne newInstance() {
        FeagementComicOne fragment = new FeagementComicOne();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_comic_main, container, false);
            ButterKnife.inject(this, rootView);
            appContext = AppContext.getApplication();
            initView();
            initData(false);
        }
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        return rootView;
    }

    private void initView() {
        chMedCircImgAdapter = new ComicShufflingAdapter(getActivity());
        headerViewPager.setOffscreenPageLimit(5);
        headerViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        headerViewPager.setAdapter(chMedCircImgAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(tuadapter = new TuJiAdapter());
        ctvNewest.setOnClickListener(this);
        ctvType.setOnClickListener(this);
        ctvStatu.setOnClickListener(this);
        myRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                initData(true);
            }
        });
        recyclerView.addOnScrollListener(new SwipyAppBarScrollListener(appbar, myRefreshLayout, recyclerView));
    }

    @Override
    public void onLazyLoad() {
        super.onLazyLoad();
        getData();
    }


    private synchronized void initData(final boolean isRefresh) {
        endLastRefresh(true);
        if (isRefresh) {
            PageIndex = 0;
        }
        RetrofitService
                .getInstance()
                .getApiCacheRetryService()
                .getList(appContext.getTuJiList(Const.DongManTuJi, PageIndex))
                .enqueue(new SimpleCallBack<TestMode>() {
                    @Override
                    public void onSuccess(Call<TestMode> call, Response<TestMode> response) {
                        endCurrentRefresh(isRefresh);
                        if (response.body().getPosts() == null) return;
                        PageIndex++;
                        tuadapter.bindData(isRefresh, response.body().getPosts());
                    }

                    @Override
                    public void onFailure(Call<TestMode> call, Throwable t) {
                        super.onFailure(call, t);
                        LogUtils.d("---------->call" + t.toString());
                        endCurrentRefresh(isRefresh);
                    }
                });

    }

    private void getData() {

        List<ChMedicCircleRecEntity> datas = new ArrayList<>();
        ChMedicCircleRecEntity en = new ChMedicCircleRecEntity();
        en.id = 0;
        en.title = "惊天魔盗团2";
        en.img = "http://img3.doubanio.com/view/movie_poster_cover/lpst/public/p2355440566.jpg";
        en.desc = "描述:惊天魔盗团2";
        datas.add(en);


        en = new ChMedicCircleRecEntity();
        en.id = 1;
        en.title = "赏金猎人";
        en.img = "http://img3.douban.com/view/movie_poster_cover/lpst/public/p2358403173.jpg";
        en.desc = "描述:赏金猎人";
        datas.add(en);


        en = new ChMedicCircleRecEntity();
        en.id = 1;
        en.title = "魔轮";
        en.img = "http://img3.douban.com/view/movie_poster_cover/lpst/public/p2358333141.jpg";
        en.desc = "描述:魔轮";
        datas.add(en);

        en = new ChMedicCircleRecEntity();
        en.id = 1;
        en.title = "终极胜利";
        en.img = "http://img3.douban.com/view/movie_poster_cover/lpst/public/p2363435535.jpg";
        en.desc = "描述:终极胜利";
        datas.add(en);


        en = new ChMedicCircleRecEntity();
        en.id = 1;
        en.title = "忍者神龟2：破影而出";
        en.img = "http://img3.doubanio.com/view/movie_poster_cover/lpst/public/p2357111716.jpg";
        en.desc = "描述:忍者神龟2：破影而出";
        datas.add(en);

        chMedCircImgAdapter.bindData(datas);
        if (chMedCircImgAdapter.getCount() >= 2) {
            headerViewPager.setCurrentItem(1, false);
        }

        myRefreshLayout.autoRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public void endLastRefresh(boolean isRefresh) {
        if (isRefresh)
            myRefreshLayout.finishRefreshLoadMore();
        else
            myRefreshLayout.finishRefresh();
    }

    public void endCurrentRefresh(boolean isRefresh) {
        if (isRefresh)
            myRefreshLayout.finishRefresh();
        else
            myRefreshLayout.finishRefreshLoadMore();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_newest:

                break;
            case R.id.ctv_type:

                break;
            case R.id.ctv_statu:

                break;
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (adapter == tuadapter) {
            TestMode.PostsBean obj = tuadapter.getItem(position);
            WenDangActivity.launch(getActivity(),obj.getID()+"");
        }

    }
}
