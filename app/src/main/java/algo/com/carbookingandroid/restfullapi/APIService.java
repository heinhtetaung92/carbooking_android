package algo.com.carbookingandroid.restfullapi;

import algo.com.carbookingandroid.model.APIResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public interface  APIService {

    @GET("/availability")
    Call<APIResponse> searchBookingValidity(@Query("startTime") long startTime,
                                            @Query("endTime") long endTime);

}
