package com.example.saru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saru.Model.Admins;
import com.example.saru.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminnssActivity extends AppCompatActivity {
EditText a,b;
    String dbs="Admins";
Button AdminLoginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminnss);
        a=(EditText)findViewById(R.id.Phone1);
        b=(EditText)findViewById(R.id.Password1);

        AdminLoginbtn=(Button)findViewById(R.id.Adminlogin_btn);
       AdminLoginbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               loginAdmin();
           }
       });

    }

    private void loginAdmin() {
         String Phone=a.getText().toString();
         String Password=b.getText().toString();
        if (TextUtils.isEmpty(Phone)) {
            Toast.makeText(this, "Please write your number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Password))
        {
            Toast.makeText(this, "Please give your password", Toast.LENGTH_SHORT).show();
        }
        else {
AllowacessToAdmin(Phone,Password);
        }

    }

    private void AllowacessToAdmin(final String Phone,final String Password)
    {
        final DatabaseReference Adminref;
        Adminref= FirebaseDatabase.getInstance().getReference();
        Adminref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(dbs).child(Phone).exists()){
                    Admins adminsData=dataSnapshot.child(dbs).child(Phone).getValue(Admins.class);
                    if(adminsData.getPhone().equals(Phone))
                    {
                     if(adminsData.getPassword().equals(Password)){
                         Toast.makeText(AdminnssActivity.this,"Logged In successfully",Toast.LENGTH_SHORT).show();

                         Intent intent=new Intent(AdminnssActivity.this,AdminCategoryActivity.class);

                         startActivity(intent);
                     }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
