package com.caobolun.infraai.http;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpMediaTypes {

    /**
     * JSON 媒体类型，使用 UTF-8 字符集
     * 用于 OkHttp 请求中的 MediaType 对象
     */
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
}
