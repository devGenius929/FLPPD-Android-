package com.antigravitystudios.flppd.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkModule {

    private static final String BASE_URL = "https://flppdappdev.com/api/v1/";

    private static Interactor interactor;
    private static ApiService apiService;

    public static void init() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        interactor = new Interactor(apiService);
    }

    public static Interactor getInteractor() {
        return interactor;
    }

    public static ApiService getApiService() {
        return apiService;
    }
}
