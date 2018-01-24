package sf.orderfoodclient.remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import sf.orderfoodclient.model.firebase.MyResponse;
import sf.orderfoodclient.model.firebase.Sender;

/**
 * Created by mesutgenc on 24.01.2018.
 */

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA-NnvtOg:APA91bGbEgqeKk_IZPEZXYH1frm1UZzvEu3S3yS7bafApScxZ8o044B5m9HlqEHu6WMMX7JVbazCLmXtgSkSxQiy1rJYaItOs7-akz2LK3OFQRB7vcPVev_2B4fuHy-D-0n0L2hVwDJL"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
