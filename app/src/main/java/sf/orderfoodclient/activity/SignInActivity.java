package sf.orderfoodclient.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.TextView;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import sf.orderfoodclient.R;
import sf.orderfoodclient.common.Common;
import sf.orderfoodclient.model.User;

public class SignInActivity extends AppCompatActivity {

    MaterialEditText edtPhone, edtPassword;
    FButton btnSignIn;
    FirebaseDatabase database;
    DatabaseReference tableUser;
    CheckBox ckbRemember;
    TextView txtForgotPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        database = FirebaseDatabase.getInstance();
        tableUser = database.getReference("User");

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        btnSignIn = (FButton) findViewById(R.id.btnSignIn);
        ckbRemember = (CheckBox) findViewById(R.id.ckbRemember);
        txtForgotPwd = (TextView) findViewById(R.id.txtForgotPwd);

        // init Paper
        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    if (ckbRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                    }

                    final ProgressDialog mDialog = new ProgressDialog(SignInActivity.this);
                    mDialog.setMessage("Loading...");
                    mDialog.show();

                    tableUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());
                                if (user.getPassword().toString().equals(edtPassword.getText().toString())) {
                                    Intent homeIntent = new Intent(SignInActivity.this, HomeActivity.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();
                                } else {
                                    mDialog.dismiss();
                                    Snackbar.make(findViewById(R.id.signInLayout), "Failed", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Snackbar.make(findViewById(R.id.signInLayout), "User not exist in database", Snackbar.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignInActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });

        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });

    }

    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inFlater = this.getLayoutInflater();
        View forgot_view = inFlater.inflate(R.layout.dialog_forgot_password, null);
        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = (MaterialEditText) forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCode = (MaterialEditText) forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // check if user available
                tableUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        if (user.getSecureCode().equals(edtSecureCode.getText().toString())) {
                            Toast.makeText(SignInActivity.this, "Your password : " + user.getPassword(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignInActivity.this, "Wrong secure code !!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}
