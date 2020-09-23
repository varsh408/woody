package com.example.saru;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saru.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
private CircleImageView profileImageView;
private EditText fullNameEditText, userPhoneEditText,adressEditText;
private TextView profileChangetxtbtn,closetxtButton,saveButton;
private Uri imageUri;
private static final int CAMERA_REQUEST_CODE=1;
private Button securityQuestionsbtn;
private String myUrl= "";
private StorageTask uploadTask;
private StorageReference storageProfilePictureRef;
private String checker= "";
private Integer count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for(int i=0;i<4;i++){

        }
        setContentView(R.layout.activity_settings);
        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        profileImageView=(CircleImageView)findViewById(R.id.settings_profile_image);
        securityQuestionsbtn=(Button)findViewById(R.id.security_questions_btn);
        fullNameEditText=(EditText)findViewById(R.id.settings_full_name);
        userPhoneEditText=(EditText)findViewById(R.id.settings_phone_number);
        adressEditText=(EditText)findViewById(R.id.settings_address);
        profileChangetxtbtn=(TextView)findViewById(R.id.profile_image_change_btn);
        closetxtButton=(TextView)findViewById(R.id.close_settings_btn);
        saveButton=(TextView)findViewById(R.id.update_account_settings_button);

        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,adressEditText);
closetxtButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        finish();
    }
});
securityQuestionsbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(SettingsActivity.this,ResetPasworsActivity.class);
        intent.putExtra("check","settings");
        startActivity(intent);
    }
});
saveButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(checker.equals("clicked")){
userInfoSaved();
        }
        else{
updateOnlyUserInfo();
        }
    }
});
profileChangetxtbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view)
    {
checker="clicked";
        CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .start(SettingsActivity.this);

    }
});
    }

    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("address", adressEditText.getText().toString());
        userMap.put("phone", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
        startActivity(new Intent(SettingsActivity.this, MenumainActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            profileImageView.setImageURI(imageUri);

        }
        else {
            Toast.makeText(this,"Error try again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
if(TextUtils.isEmpty((fullNameEditText).getText().toString())){
    Toast.makeText(this,"Name is mandatory",Toast.LENGTH_SHORT).show();
}
else if(TextUtils.isEmpty((adressEditText).getText().toString())){
    Toast.makeText(this,"Address is mandatory",Toast.LENGTH_SHORT).show();
}
else if (TextUtils.isEmpty((userPhoneEditText).getText().toString())){
    Toast.makeText(this,"Phone Number  is mandatory",Toast.LENGTH_SHORT).show();
}
else if (checker.equals("clicked")){
    uploadImage();
}
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("please wait!!!!!!!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if(imageUri!=null){
            final StorageReference fileRef=storageProfilePictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                  return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
if(task.isSuccessful()) {
    Uri downloadUri = task.getResult();
    myUrl = downloadUri.toString();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
    HashMap<String, Object> userMap = new HashMap<>();
    userMap.put("name", fullNameEditText.getText().toString());
    userMap.put("address", adressEditText.getText().toString());
    userMap.put("phone", userPhoneEditText.getText().toString());
    userMap.put("image", myUrl);
ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
progressDialog.dismiss();
    startActivity(new Intent(SettingsActivity.this, MenumainActivity.class));
    Toast.makeText(SettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
    finish();
}
else{
    progressDialog.dismiss();
    Toast.makeText(SettingsActivity.this, "ERROR", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText adressEditText) {
        DatabaseReference UsersRef= FirebaseDatabase.getInstance().getReference().child("User").child(Prevalent.currentOnlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          if(dataSnapshot.exists()){
              if(dataSnapshot.child("image").exists()){
          String image= dataSnapshot.child("image").getValue().toString();
          String name= dataSnapshot.child("name").getValue().toString();
          String phone= dataSnapshot.child("password").getValue().toString();
          String address= dataSnapshot.child("address").getValue().toString();

                  Picasso.get().load(image).into(profileImageView);
                  fullNameEditText.setText(name);
                  userPhoneEditText.setText(phone);
                  adressEditText.setText(address);

}
}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
