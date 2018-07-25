package algo.com.carbookingandroid.restfullapi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class APIManager {

    //https://challenge.smove.sg/availability?startTime=1532341618&endTime=1532377618
    private static String HITPAY_DOMAIN = "https://challenge.smove.sg";
    private final int API_REQUEST_TIMEOUT = 15;
    private Retrofit networkAdapter;

    public static APIManager getInstance(){
        return new APIManager();
    }

    public APIManager(){
        networkAdapter = new Retrofit.Builder()
                .baseUrl(HITPAY_DOMAIN)
                .client(defaultOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public APIService API_Service(){
        return networkAdapter.create(APIService.class);
    }

    private OkHttpClient defaultOkHttpClient() {
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(API_REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .build();
        return client;
    }

}
