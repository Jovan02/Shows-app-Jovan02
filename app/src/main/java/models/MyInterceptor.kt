package models

import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("token-type", "Bearer")
            .addHeader("access-token", "")
            .addHeader("client", "")
            .addHeader("uid", "")
            .build()
        return chain.proceed(request)
    }
}