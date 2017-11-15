
package com.github.lany192.sample;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {
    // 切换 Url 的优先级: DomainHeader 中的将覆盖全局的 BaseUrl
    // 这里不配置 DomainHeader，将只受到设置的全局 BaseUrl 的影响,没有全局 BaseUrl 将请求原始的 BaseUrl
    // 当你项目中只有一个 BaseUrl ,但需要动态改变,全局 BaseUrl 显得非常方便
    @GET("/json/demo2.json")
    Observable<ResponseBody> requestDefault();

    @Headers({DomainConfig.HEADER_1})
    @GET("/users")
    Observable<ResponseBody> getUsers(@Query("since") int since, @Query("per_page") int perPage);

    @Headers({DomainConfig.HEADER_2})
    @GET("/api/data/Android/{size}/{page}")
    Observable<ResponseBody> getData(@Path("size") int size, @Path("page") int page);

    @Headers({DomainConfig.HEADER_3})
    @GET("/v2/book/{id}")
    Observable<ResponseBody> getBook(@Path("id") int id);
}
