package sf.orderfoodclient.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import sf.orderfoodclient.model.User;
import sf.orderfoodclient.remote.APIService;
import sf.orderfoodclient.remote.RetrofitClient;

/**
 * Created by mesutgenc on 8.01.2018.
 */

public class Common {
    public static User currentUser;
    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }

        return false;
    }

    public static APIService getFCMService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

}
