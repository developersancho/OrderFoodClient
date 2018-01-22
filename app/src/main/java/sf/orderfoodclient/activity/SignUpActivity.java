package sf.orderfoodclient.activity;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import sf.orderfoodclient.R;
import sf.orderfoodclient.common.Common;
import sf.orderfoodclient.model.User;

public class SignUpActivity extends AppCompatActivity {

    MaterialEditText edtPhone, edtPassword, edtName, edtSecureCode;
    FButton btnSignUp;
    FirebaseDatabase database;
    DatabaseReference tableUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtSecureCode = (MaterialEditText) findViewById(R.id.edtSecureCode);
        btnSignUp = (FButton) findViewById(R.id.btnSignUp);

        database = FirebaseDatabase.getInstance();
        tableUser = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("Loading...");
                    mDialog.show();

                    tableUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Snackbar.make(findViewById(R.id.signUpLayout), "Phone Number Already Registered", Snackbar.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(edtName.getText().toString(),
                                        edtPassword.getText().toString(),
                                        edtSecureCode.getText().toString());
                                tableUser.child(edtPhone.getText().toString()).setValue(user);
                                Snackbar.make(findViewById(R.id.signUpLayout), "Sign Up Success", Snackbar.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(SignUpActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
