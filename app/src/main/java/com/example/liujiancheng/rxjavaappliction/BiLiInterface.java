package com.example.liujiancheng.rxjavaappliction;

import retrofit2.http.GET;
import rx.Observable;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: com.example.liujiancheng.rxjavaappliction.BiLiInterface.java
 * @author: liu jiancheng
 * @date: 2018-04-02 16:18
 */
public interface BiLiInterface {


    /**
     * 推荐数据
     * @return
     */
    @GET("x/show/old?platform=android&device=&build=412001")
    Observable<RecommendInfo> getBiLiInfoMation();

}
