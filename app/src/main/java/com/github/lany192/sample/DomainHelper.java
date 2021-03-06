
package com.github.lany192.sample;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DomainHelper {
    private final String TAG = getClass().getSimpleName();

    private boolean isRun = true; //默认开始运行,可以随时停止运行,比如你在 App 启动后已经不需要在动态切换 baseurl 了
    private final Map<String, HttpUrl> mDomainNameHub = new HashMap<>();
    private final Interceptor mInterceptor;
    private volatile static DomainHelper instance = null;

    private DomainHelper() {
        this.mInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (!isRun()) // 可以在 App 运行时,随时通过 setRun(false) 来结束本管理器的运行,
                    return chain.proceed(chain.request());
                return chain.proceed(processRequest(chain.request()));
            }
        };
    }

    public static DomainHelper getInstance() {
        if (instance == null) {
            synchronized (DomainHelper.class) {
                if (instance == null) {
                    instance = new DomainHelper();
                }
            }
        }
        return instance;
    }

    public OkHttpClient.Builder with(OkHttpClient.Builder builder) {
        return builder.addInterceptor(mInterceptor);
    }

    public Request processRequest(Request request) {
        Request.Builder newBuilder = request.newBuilder();
        String domainName = obtainDomainNameFromHeaders(request);
        HttpUrl httpUrl;
        // 如果有 header,获取 header 中 domainName 所映射的 url,若没有,则检查全局的 BaseUrl,未找到则为null
        if (!TextUtils.isEmpty(domainName)) {
            httpUrl = fetchDomain(domainName);
            newBuilder.removeHeader(DomainConfig.DOMAIN_NAME);
        } else {
            httpUrl = fetchDomain(DomainConfig.GLOBAL_DOMAIN_NAME);
        }

        if (null != httpUrl) {
            HttpUrl newUrl = parseUrl(httpUrl, request.url());
            Log.d(TAG, "New Url is { " + newUrl.toString() + " } , Old Url is { " + request.url().toString() + " }");
            return newBuilder
                    .url(newUrl)
                    .build();
        }

        return newBuilder.build();

    }

    /**
     * 管理器是否在运行
     *
     * @return
     */
    public boolean isRun() {
        return this.isRun;
    }

    /**
     * 控制管理器是否运行,在每个域名地址都已经确定,不需要再动态更改时可设置为 false
     *
     * @param run
     */
    public void setRun(boolean run) {
        this.isRun = run;
    }

    /**
     * 全局动态替换 BaseUrl，优先级： Header中配置的url > 全局配置的url
     * 除了作为备用的 BaseUrl ,当你项目中只有一个 BaseUrl ,但需要动态改变
     * 这种方式不用在每个接口方法上加 Header,也是个很好的选择
     *
     * @param url
     */
    public void setGlobalDomain(String url) {
        synchronized (mDomainNameHub) {
            mDomainNameHub.put(DomainConfig.GLOBAL_DOMAIN_NAME, checkUrl(url));
        }
    }

    /**
     * 获取全局 BaseUrl
     */
    public HttpUrl getGlobalDomain() {
        return mDomainNameHub.get(DomainConfig.GLOBAL_DOMAIN_NAME);
    }

    /**
     * 移除全局 BaseUrl
     */
    public void removeGlobalDomain() {
        synchronized (mDomainNameHub) {
            mDomainNameHub.remove(DomainConfig.GLOBAL_DOMAIN_NAME);
        }
    }

    /**
     * 存放 Domain 的映射关系
     *
     * @param domainName
     * @param domainUrl
     */
    public void putDomain(String domainName, String domainUrl) {
        synchronized (mDomainNameHub) {
            mDomainNameHub.put(domainName, checkUrl(domainUrl));
        }
    }

    /**
     * 取出对应 DomainName 的 Url
     *
     * @param domainName
     * @return
     */
    public HttpUrl fetchDomain(String domainName) {
        return mDomainNameHub.get(domainName);
    }

    public void removeDomain(String domainName) {
        synchronized (mDomainNameHub) {
            mDomainNameHub.remove(domainName);
        }
    }

    public void clearAllDomain() {
        mDomainNameHub.clear();
    }

    public boolean haveDomain(String domainName) {
        return mDomainNameHub.containsKey(domainName);
    }

    public int domainSize() {
        return mDomainNameHub.size();
    }


    /**
     * 从header中取出 DomainName
     */
    private String obtainDomainNameFromHeaders(Request request) {
        List<String> headers = request.headers(DomainConfig.DOMAIN_NAME);
        if (headers == null || headers.size() == 0)
            return null;
        if (headers.size() > 1)
            throw new IllegalArgumentException("Only one Domain-Name in the headers");
        return request.header(DomainConfig.DOMAIN_NAME);
    }

    private HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl url) {

        // 如果 HttpUrl.parse(url); 解析为 null 说明,url 格式不正确,正确的格式为 "https://github.com:443"
        // http 默认端口 80,https 默认端口 443 ,如果端口号是默认端口号就可以将 ":443" 去掉
        // 只支持 http 和 https

        if (null == domainUrl) return url;

        return url.newBuilder()
                .scheme(domainUrl.scheme())
                .host(domainUrl.host())
                .port(domainUrl.port())
                .build();
    }

    private HttpUrl checkUrl(String url) {
        HttpUrl parseUrl = HttpUrl.parse(url);
        if (null == parseUrl) {
            throw new RuntimeException("url格式不正确" + url);
        } else {
            return parseUrl;
        }
    }
}
