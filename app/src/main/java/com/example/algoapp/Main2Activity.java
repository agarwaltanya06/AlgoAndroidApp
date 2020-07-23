package com.example.algoapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {

    // static String preorderText = "Preorder elements: ";

    private CustomView myCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        myCustomView = findViewById(R.id.custView);
        final TextView displayTextView = (TextView) findViewById(R.id.displayTextView);
        Button insertBtn = findViewById(R.id.insertButton);
        Button deleteBtn = findViewById(R.id.deleteButton);
        Button searchBtn = findViewById(R.id.searchBtn);
        Button preorderBtn = findViewById(R.id.preorderBtn);
        Button inorderBtn = findViewById(R.id.inorderBtn);
        Button postorderBtn = findViewById(R.id.postorderBtn);
        Button helpBtn = findViewById(R.id.helpBtn);
        final TextView inputText = findViewById(R.id.inputText);

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = "";
                s=inputText.getText().toString();
                if(s.length()!=0) {
                    int inputValue = Integer.parseInt(inputText.getText().toString());
                    myCustomView.addToBst(inputValue, displayTextView);
                    displayTextView.setText(myCustomView.insertionText);
                }
                inputText.setText("");
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s = "";
                s=inputText.getText().toString();
                if(s.length()!=0) {
                    int inputValue = Integer.parseInt(inputText.getText().toString());
                    myCustomView.delFromBst(inputValue, displayTextView);
                    displayTextView.setText(myCustomView.deletionText);
                }
                inputText.setText("");

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = "";
                s=inputText.getText().toString();
                if(s.length()!=0) {
                    int inputValue = Integer.parseInt(inputText.getText().toString());
                    myCustomView.searchInBst(inputValue, displayTextView);
                    displayTextView.setText(myCustomView.searchText);
                }
                inputText.setText("");
            }
        });

        preorderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCustomView.preorderText = "Preorder elements: ";
                if(myCustomView.mNodes.size()!=0) {
                    myCustomView.preorderTraverse(myCustomView.mNodes.get(0));
                }
                else{
                    myCustomView.preorderText = myCustomView.preorderText.concat("(Empty tree).");
                }
                displayTextView.setText(myCustomView.preorderText);

            }
        });

        inorderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCustomView.inorderText = "Inorder elements: ";
                if(myCustomView.mNodes.size()!=0) {
                    myCustomView.inorderTraverse(myCustomView.mNodes.get(0));
                }
                else{
                    myCustomView.inorderText = myCustomView.inorderText.concat("(Empty tree).");
                }
                displayTextView.setText(myCustomView.inorderText);

            }
        });

        postorderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCustomView.postorderText = "Postorder elements: ";
                if(myCustomView.mNodes.size()!=0) {
                    myCustomView.postorderTraverse(myCustomView.mNodes.get(0));
                }
                else{
                    myCustomView.postorderText = myCustomView.postorderText.concat("(Empty tree).");
                }
                displayTextView.setText(myCustomView.postorderText);

            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(startIntent);
            }
        });

    }


}





