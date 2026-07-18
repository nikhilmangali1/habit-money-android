package com.nikhil.habit_money.core.network;

import com.nikhil.habit_money.core.util.TokenManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://localhost:8080/";         // http://10.0.2.2:8080/ - for emulator
    private static ApiService apiService;

    public static ApiService getApiService(TokenManager tokenManager) {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = chain -> {
                java.net.URL url = chain.request().url().url();
                String path = url.getPath();

                boolean isAuthEndpoint = path.contains("/auth/register")
                        || path.contains("/auth/login")
                        || path.contains("/auth/refresh")
                        || path.contains("/auth/logout");

                if (!isAuthEndpoint && tokenManager.getAccessToken() != null) {
                    var request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + tokenManager.getAccessToken())
                            .build();
                    return chain.proceed(request);
                }
                return chain.proceed(chain.request());
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
