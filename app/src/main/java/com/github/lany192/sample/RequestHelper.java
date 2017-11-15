package com.github.lany192.sample;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestHelper {
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;
    private ApiService mOneApiService;

    private volatile static RequestHelper instance = null;

    public static RequestHelper getInstance() {
        if (instance == null) {
            synchronized (RequestHelper.class) {
                if (instance == null) {
                    instance = new RequestHelper();
                }
            }
        }
        return instance;
    }

    private RequestHelper() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        this.mOkHttpClient = DomainHelper.getInstance()
                .with(new OkHttpClient.Builder())
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(logInterceptor)
                .build();

        this.mRetrofit = new Retrofit.Builder()
                .baseUrl(DomainConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用rxjava
                .addConverterFactory(GsonConverterFactory.create())//使用Gson
                .client(mOkHttpClient)
                .build();

        this.mOneApiService = mRetrofit.create(ApiService.class);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public ApiService getApiService() {
        return mOneApiService;
    }
}
