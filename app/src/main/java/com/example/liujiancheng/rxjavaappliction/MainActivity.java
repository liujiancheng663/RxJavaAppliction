package com.example.liujiancheng.rxjavaappliction;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.RxLifecycle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * @author Administrator
 *
 */
public class MainActivity extends AppCompatActivity {


    @BindView(R.id.swipe_refresh_layout)SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.text_view)TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this::initData);
        mSwipeRefreshLayout.post(() -> {
            initData();
            mSwipeRefreshLayout.setRefreshing(true);
        });

    }

    private void initData() {

        RetrofitHelper.getBiLiInfoList()
                .getBiLiInfoMation()
                .compose(RxLifecycle.bind(BehaviorSubject.create()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::heldEmptyView, throwable -> {
                    Toast.makeText(MainActivity.this,"请求失败,"+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    initEmptyView();
                });

    }

    private void initEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mTextView.setText("请求数据失败");
    }


    private void heldEmptyView(RecommendInfo recommendInfo) {
        mSwipeRefreshLayout.setRefreshing(false);
        mTextView.setText(recommendInfo.toString());
    }
}
