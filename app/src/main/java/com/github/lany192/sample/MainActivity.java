package com.github.lany192.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private EditText mUrl1;
    private EditText mUrl2;
    private EditText mUrl3;
    private EditText mGlobalUrl;

    private TextView mShowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShowText = findViewById(R.id.my_text_view);
        mUrl1 = findViewById(R.id.et_url1);
        mUrl2 = findViewById(R.id.et_url2);
        mUrl3 = findViewById(R.id.et_url3);
        mGlobalUrl = findViewById(R.id.et_global_url);
        mUrl1.setSelection(mUrl1.getText().toString().length());
        findViewById(R.id.bt_request1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUrl httpUrl = DomainHelper.getInstance().fetchDomain(DomainConfig.GITHUB_DOMAIN_NAME);
                if (httpUrl == null || !httpUrl.toString().equals(mUrl1.getText().toString())) { //可以在 App 运行时随意切换某个接口的 BaseUrl
                    DomainHelper.getInstance().putDomain(DomainConfig.GITHUB_DOMAIN_NAME, mUrl1.getText().toString());
                }
                RequestHelper
                        .getInstance()
                        .getApiService()
                        .getUsers(1, 10)
                        .compose(MainActivity.this.<ResponseBody>getDefaultTransformer())
                        .subscribe(getDefaultObserver());
            }
        });

        findViewById(R.id.bt_request2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUrl httpUrl = DomainHelper.getInstance().fetchDomain(DomainConfig.GANK_DOMAIN_NAME);
                if (httpUrl == null || !httpUrl.toString().equals(mUrl2.getText().toString())) { //可以在 App 运行时随意切换某个接口的 BaseUrl
                    DomainHelper.getInstance().putDomain(DomainConfig.GANK_DOMAIN_NAME, mUrl2.getText().toString());
                }
                RequestHelper
                        .getInstance()
                        .getApiService()
                        .getData(10, 1)
                        .compose(MainActivity.this.<ResponseBody>getDefaultTransformer())
                        .subscribe(getDefaultObserver());
            }
        });

        findViewById(R.id.bt_request3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUrl httpUrl = DomainHelper.getInstance().fetchDomain(DomainConfig.DOUBAN_DOMAIN_NAME);
                if (httpUrl == null || !httpUrl.toString().equals(mUrl3.getText().toString())) { //可以在 App 运行时随意切换某个接口的 BaseUrl
                    DomainHelper.getInstance().putDomain(DomainConfig.DOUBAN_DOMAIN_NAME, mUrl3.getText().toString());
                }
                RequestHelper
                        .getInstance()
                        .getApiService()
                        .getBook(1220562)
                        .compose(MainActivity.this.<ResponseBody>getDefaultTransformer())
                        .subscribe(getDefaultObserver());
            }
        });

    }

    // 请求默认 BaseUrl，请求的接口没有配置 DomainHeader，所以只受全局 BaseUrl的影响
    public void btnRequestDefault(View view) {
        RequestHelper
                .getInstance()
                .getApiService()
                .requestDefault()
                .compose(this.<ResponseBody>getDefaultTransformer())
                .subscribe(getDefaultObserver());
    }

    // 设置全局替换的 BaseUrl
    public void btnSetGlobalUrl(View view) {
        //当你项目中只有一个 BaseUrl ,但需要动态改变,全局 BaseUrl 显得非常方便
        HttpUrl httpUrl = DomainHelper.getInstance().getGlobalDomain();
        if (null == httpUrl || !httpUrl.toString().equals(mGlobalUrl.getText().toString().trim()))
            DomainHelper.getInstance().setGlobalDomain(mGlobalUrl.getText().toString().trim());

        Toast.makeText(getApplicationContext(), "全局替换baseUrl成功", Toast.LENGTH_SHORT).show();
    }

    // 移除全局的 BaseUrl
    public void btnRmoveGlobalUrl(View view) {
        //不想再使用全局 BaseUrl ,想用之前传入 Retrofit 的默认 BaseUrl ,就Remove
        DomainHelper.getInstance().removeGlobalDomain();
        Toast.makeText(getApplicationContext(), "移除了全局baseUrl", Toast.LENGTH_SHORT).show();
    }

    private <T> ObservableTransformer<T, T> getDefaultTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {

                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate(new Action() {
                            @Override
                            public void run() throws Exception {

                            }
                        });
            }
        };
    }

    private Observer<ResponseBody> getDefaultObserver() {
        return new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody response) {
                try {
                    String string = response.string();
                    Log.d("test", string);
                    mShowText.setText("" + string);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                mShowText.setText("" + throwable.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }
}
