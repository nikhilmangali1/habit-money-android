package com.nikhil.habit_money.core.network;

import com.nikhil.habit_money.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nikhil.habit_money.core.util.TokenManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
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
                    .authenticator(new TokenRefreshAuthenticator(tokenManager))
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

    private static class TokenRefreshAuthenticator implements Authenticator {

        private final TokenManager tokenManager;

        TokenRefreshAuthenticator(TokenManager tokenManager) {
            this.tokenManager = tokenManager;
        }

        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            if (response.code() != 401) return null;

            String path = response.request().url().encodedPath();
            if (path.contains("/auth/refresh")) return null;

            String refreshToken = tokenManager.getRefreshToken();
            if (refreshToken == null) {
                tokenManager.clearAll();
                return null;
            }

            String body = "{\"refreshToken\":\"" + refreshToken + "\"}";
            Request refreshRequest = new Request.Builder()
                    .url(BASE_URL + "api/v1/auth/refresh")
                    .post(RequestBody.create(body, JSON))
                    .build();

            OkHttpClient refreshClient = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build();

            try (Response refreshResponse = refreshClient.newCall(refreshRequest).execute()) {
                if (!refreshResponse.isSuccessful() || refreshResponse.body() == null) {
                    tokenManager.clearAll();
                    return null;
                }

                String json = refreshResponse.body().string();
                JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                String newAccessToken = jsonObject.get("accessToken").getAsString();
                String newRefreshToken = jsonObject.get("refreshToken").getAsString();
                String email = jsonObject.has("email") ? jsonObject.get("email").getAsString() : null;
                String firstName = jsonObject.has("firstName") ? jsonObject.get("firstName").getAsString() : null;

                tokenManager.saveTokens(newAccessToken, newRefreshToken, email, firstName);

                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newAccessToken)
                        .build();
            }
        }
    }
}
