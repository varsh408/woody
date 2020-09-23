package com.example.saru;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saru.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasworsActivity extends AppCompatActivity {
private String check="";
private TextView pageTitle,titleQuestions;
private EditText phoneNumber,question1, question2;
private Button verifyButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_paswors);
        check=getIntent().getStringExtra("check");
        pageTitle=(TextView)findViewById(R.id.page_title);
        phoneNumber=(EditText)findViewById(R.id.find_phone_number);
        question1=(EditText)findViewById(R.id.question1);
        question2=(EditText)findViewById(R.id.question2);
        titleQuestions=(TextView)findViewById(R.id.title_questions);
verifyButton=(Button)findViewById(R.id.verify_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(check.equals("settings")) {
            pageTitle.setText("Set Questions");
            titleQuestions.setText("Answer the questions");
            phoneNumber.setVisibility(View.GONE);
            displayPrevioysAnswers();
            verifyButton.setText("Set");
            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnswers();

                }
            });

        }
        else if(check.equals("login"))
        {
phoneNumber.setVisibility(View.VISIBLE);
verifyButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        verifyUser();
    }
});
        }
    }

    private void setAnswers(){
        String answer1=question1.getText().toString().toLowerCase();
        String answer2=question2.getText().toString().toLowerCase();
        if(question1.equals("")&&question2.equals(""))
        {
            Toast.makeText(ResetPasworsActivity.this,"Answer both the questions",Toast.LENGTH_SHORT).show();

        }
        else {
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());
            HashMap<String,Object> userdataMap=new HashMap<>();
            userdataMap.put("answer1",answer1);
            userdataMap.put("answer2",answer2);
            ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful()){
                        Toast.makeText(ResetPasworsActivity.this,"Correct Answers",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(ResetPasworsActivity.this,MenumainActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }
    }
    private void displayPrevioysAnswers(){
        DatabaseReference ref=FirebaseDatabase.getInstance()
                .getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()){
                    String ans1=dataSnapshot.child("answer1").getValue().toString();
                    String ans2=dataSnapshot.child("answer2").getValue().toString();
                    question1.setText(ans1);
                    question2.setText(ans2);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void verifyUser(){
        final String phone=phoneNumber.getText().toString();
        final String answer1=question1.getText().toString().toLowerCase();
        final String answer2=question2.getText().toString().toLowerCase();
        if(!phone.equals("")&& !answer1.equals("") && !answer2.equals("")){
            final DatabaseReference ref=FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(phone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists()){
                        String mPhone=dataSnapshot.child("phone").getValue().toString();
                        if(dataSnapshot.hasChild("Security Questions")){
                            String ans1=dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2=dataSnapshot.child("Security Questions").child("answer2").getValue().toString();
                            if(!ans1.equals(answer1)){
                                Toast.makeText(ResetPasworsActivity.this,"first answer is incorrect",Toast.LENGTH_SHORT).show();
                            }
                            else if(!ans2.equals(answer2)){
                                Toast.makeText(ResetPasworsActivity.this,"first answer is incorrect",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                AlertDialog.Builder builder=new AlertDialog.Builder(ResetPasworsActivity.this);
                                builder.setTitle("Enter New Password");
                                final EditText newPassword=new EditText(ResetPasworsActivity.this);
                                newPassword.setHint("write your new password");
                                builder.setView(newPassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        if(!newPassword.getText().toString().equals("")){
                                            ref.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                    {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(ResetPasworsActivity.this,"password changed successfully",Toast.LENGTH_SHORT).show();
                                                                Intent intent=new Intent(ResetPasworsActivity.this,loginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();

                            }

                        }
                        else{
                            Toast.makeText(ResetPasworsActivity.this,"security questions not set",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
else {
    Toast.makeText(ResetPasworsActivity.this,"Please complete the form",Toast.LENGTH_SHORT ).show();
        }
    }
}
