package models

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor(context: Context) : Interceptor {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("token-type", sharedPreferences.getString("token-type", "")!!)
            .addHeader("access-token", sharedPreferences.getString("access-token", "")!!)
            .addHeader("client", sharedPreferences.getString("client", "")!!)
            .addHeader("uid", sharedPreferences.getString("uid", "")!!)
            .addHeader("expiry", sharedPreferences.getString("expiry", "")!!)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}