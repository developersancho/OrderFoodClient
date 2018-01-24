package sf.orderfoodclient.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import sf.orderfoodclient.common.Common;
import sf.orderfoodclient.model.firebase.Token;

/**
 * Created by mesutgenc on 24.01.2018.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if (Common.currentUser != null) {
            updateTokenToFirebase(tokenRefreshed);
        }
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("FoodTokens");
        Token token = new Token(tokenRefreshed, false);
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
