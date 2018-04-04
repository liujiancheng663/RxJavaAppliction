package com.example.liujiancheng.rxjavaappliction;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * @version V1.0 <RetrofitHelper>
 * @FileName: com.example.liujiancheng.rxjavaappliction.RetrofitHelper.java
 * @author: liu jiancheng
 * @date: 2018-04-02 15:57
 */
public class RetrofitHelper {

    private static OkHttpClient okHttpClient;

    static {
        initOkHttpServer();
    }



    public static BiLiInterface getBiLiInfoList(){

        return createApi(BiLiInterface.class,TnSapConst.Https);
    }


    /**
     *  retrofit初始化,加入okHttpClient，gson做自动解析
     *
     * @param clazz
     * @param https
     * @param <T>
     * @return
     */
    private static <T>  T createApi(Class<T> clazz, String https) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(https)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        return retrofit.create(clazz);

    }





    /**
     * 初始化OKHttpClient,设置缓存,设置超时时间,设置打印日志,设置拦截器
     */
    private static void initOkHttpServer() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (okHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (okHttpClient == null) {
                    //设置Http缓存
                    Cache cache = new Cache(new File(RxJavaApplication.getInstance()
                            .getCacheDir(), "HttpCache"), 1024 * 1024 * 10);
                    okHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            .addNetworkInterceptor(new CacheInterceptor())
                            .addNetworkInterceptor(new StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .addInterceptor(new UserAgentInterceptor())
                            .build();
                }
            }

        }
    }


    /**
     * 添加UA拦截器，B站请求API需要加上UA才能正常使用
     */
    private static class UserAgentInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", TnSapConst.COMMON_UA_STR)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }


    /**
     * 为okHttp添加缓存，这里是考虑到服务器不支持缓存时，从而让okHttp支持缓存
     */
    private static class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            // 有网络时 设置缓存超时时间1个小时
            int maxAge = 60 * 60;
            // 无网络时
            // ，设置超时为1天
            int maxStale = 60 * 60 * 24;
            Request request = chain.request();
            if (CommonUtil.isNetworkAvailable(RxJavaApplication.getInstance())) {
                //有网络时只从网络获取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
            } else {
                //无网络时只从缓存中读取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (CommonUtil.isNetworkAvailable(RxJavaApplication.getInstance())) {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }

}
