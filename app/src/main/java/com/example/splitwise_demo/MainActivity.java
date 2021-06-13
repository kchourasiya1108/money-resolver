package com.example.splitwise_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Button logout, addNewGrp, addNewUserToGrp;
    EditText newGrpName, validUserName;
    Spinner grpDropdown;
    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logoutBtn);
        addNewGrp = findViewById(R.id.addNewGrpBtn);
        addNewUserToGrp = findViewById(R.id.addUserToGrpBtn);
        newGrpName = findViewById(R.id.newGroup);
        validUserName = findViewById(R.id.addFriend);
        grpDropdown = findViewById(R.id.grpSelection);
        fStore = FirebaseFirestore.getInstance();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        addNewGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if present do not create
                //else make one

            }
        });
    }
}


/*

                user--->
                \group:user
                add more user
                \
                \
                user--->
                \
                \
                \

 */