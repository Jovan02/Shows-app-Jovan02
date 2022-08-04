package models

import android.content.Context
import android.content.SharedPreferences
import models.Constants.ACCEPT
import models.Constants.ACCESS_TOKEN
import models.Constants.APP
import models.Constants.APP_JSON
import models.Constants.CLIENT
import models.Constants.CONTENT_TYPE
import models.Constants.EXPIRY
import models.Constants.TOKEN_TYPE
import models.Constants.UID
import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor(context: Context) : Interceptor {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader(TOKEN_TYPE, sharedPreferences.getString(TOKEN_TYPE, "")!!)
            .addHeader(ACCESS_TOKEN, sharedPreferences.getString(ACCESS_TOKEN, "")!!)
            .addHeader(CLIENT, sharedPreferences.getString(CLIENT, "")!!)
            .addHeader(UID, sharedPreferences.getString(UID, "")!!)
            .addHeader(EXPIRY, sharedPreferences.getString(EXPIRY, "")!!)
            .addHeader(ACCEPT, APP_JSON)
            .addHeader(CONTENT_TYPE, APP_JSON)
            .build()
        return chain.proceed(request)
    }
}