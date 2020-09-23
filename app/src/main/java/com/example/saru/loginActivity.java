package com.example.saru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saru.Model.Users;
import com.example.saru.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {
private EditText InputPhoneNumber,InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingbar;
    private CheckBox chkBoxRememberMe;
    private TextView AdminLInk,forgetPassword;
    String parentDbName ="Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

forgetPassword=(TextView)findViewById(R.id.forget_password_link);
        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        AdminLInk=(TextView) findViewById(R.id.admin_panel_link);
        forgetPassword=(TextView)findViewById(R.id.forget_password_link);
        loadingbar=new ProgressDialog(this);
        chkBoxRememberMe=(CheckBox)findViewById(R.id.rememberz_me_chkb);
        Paper.init(this );
        LoginButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
            {

                LoginUser();
            }

});
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(loginActivity.this,ResetPasworsActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });
        AdminLInk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("login admin");
                AdminLInk.setVisibility(View.INVISIBLE);
             Intent intent=new Intent(loginActivity.this, AdminnssActivity.class);
             startActivity(intent);
            }
        });


    }

    private void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please write your number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please give your password", Toast.LENGTH_SHORT).show();
        }
        else
            {
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("please wait while your credentials are being checked");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            AllowAccessToAccount(phone, password);
             }

    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if(chkBoxRememberMe.isChecked() ){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }



        final DatabaseReference Rootref;
        Rootref= FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(phone).exists()){
                    Users usersData=dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            if(parentDbName.equals("Users")){
                                Toast.makeText(loginActivity.this,"Logged In successfully",Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent intent=new Intent(loginActivity.this,MenumainActivity.class);
                                Prevalent.currentOnlineUser=usersData;
                                startActivity(intent);
                            }
                        }
                        else {
                            loadingbar.dismiss();
                            Toast.makeText(loginActivity.this,"Password Is Incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(loginActivity.this,"Account with this number does not exists",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
