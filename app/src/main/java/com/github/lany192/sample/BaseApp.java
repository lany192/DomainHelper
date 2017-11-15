
package com.github.lany192.sample;

import android.app.Application;


public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //将每个 BaseUrl 进行初始化,运行时可以随时改变 DOMAIN_NAME 对应的值,从而达到改变 BaseUrl 的效果
        DomainHelper.getInstance().putDomain(DomainConfig.GITHUB_DOMAIN_NAME, DomainConfig.APP_GETHUB_DOMAIN);
        DomainHelper.getInstance().putDomain(DomainConfig.GANK_DOMAIN_NAME, DomainConfig.APP_GANK_DOMAIN);
        DomainHelper.getInstance().putDomain(DomainConfig.DOUBAN_DOMAIN_NAME, DomainConfig.APP_DOUBAN_DOMAIN);
    }

}
