package com.example.saru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.saru.Model.Users;
import com.example.saru.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
private Button joinNowBUtton , LoginButton;
    private ProgressDialog loadingbar;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinNowBUtton=(Button)findViewById(R.id.main_join_now_btn);
        LoginButton=(Button) findViewById(R.id.main_login_btn) ;
        loadingbar=new ProgressDialog(this);
    Paper.init(this);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,loginActivity.class);
                startActivity(intent);
            }
        });
       joinNowBUtton.setOnClickListener((new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent=new Intent(MainActivity.this, RegisterActivity.class);
               startActivity(intent);
           }
       }));
       String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
       String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        if(UserPhoneKey!=" " && UserPasswordKey!=" ")
        {
                if(!TextUtils.isEmpty(UserPhoneKey )&& !TextUtils.isEmpty(UserPasswordKey))
                {
                       AllowAcess(UserPhoneKey,UserPasswordKey);

                        loadingbar.setTitle("Already Logged In");
                        loadingbar.setMessage("please wait while your credentials are being checked");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();
        }
        }
}

    private void AllowAcess(final String phone, final String password)
    {

        final DatabaseReference Rootref;
        Rootref= FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(phone).exists()){
                    Users usersData=dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this,"Logged In successfully",Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Intent intent=new Intent(MainActivity.this,MenumainActivity.class);
                            Prevalent.currentOnlineUser=usersData;
                            startActivity(intent);
                        }
                        else {
                            loadingbar.dismiss();
                            Toast.makeText(MainActivity.this,"Password Is Incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,"Account with this number does not exists",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
