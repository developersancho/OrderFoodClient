package sf.orderfoodclient.common;

import sf.orderfoodclient.model.User;

/**
 * Created by mesutgenc on 8.01.2018.
 */

public class Common {
    public static User currentUser;

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }
}
