package com.rulers.walk;


import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.rulers.walk.SharedPreference.Check;

public class Details extends AppCompatActivity {

    EditText height,weight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        height=findViewById(R.id.height);
        weight=findViewById(R.id.weight);

        SharedPreferences sp=getSharedPreferences("pedometer",MODE_PRIVATE);
        if(!sp.getString("height","").equals(""))
        {
            height.setText(sp.getString("height",""));
        }
        if(sp.getFloat("weight",0f)!=0f)
        {
            weight.setText(String.valueOf(sp.getFloat("weight",0f)));
        }
    }

    public void submitData(View view) {
        Check.writeString("height",""+height.getText());
        Check.writeFloat("weight",Float.valueOf(weight.getText().toString()));
        finish();
    }
}