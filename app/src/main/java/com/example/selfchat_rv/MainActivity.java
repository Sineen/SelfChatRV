package com.example.selfchat_rv;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText input;
    RecyclerView recyclerView;
    MyAdapter ViewAdapter;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.RV);
        input = (EditText) findViewById(R.id.plain_text_input);
        button = (Button) findViewById(R.id.button);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewAdapter = new MyAdapter();
        recyclerView.setAdapter(ViewAdapter);

        if (savedInstanceState != null)
        {
            input.setText(savedInstanceState.getString("in"));
            ViewAdapter.setData(savedInstanceState.getStringArrayList("list"));
            ViewAdapter.supportConfigurationChange();
        }

        button.setOnClickListener(this);

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("in", input.getText().toString());
        outState.putStringArrayList("list", ViewAdapter.getData());
    }


    @Override
    public void onClick(View view) {
        String msg;
        msg = input.getText().toString();
        input.setText("");
        //incase they tried to add empty text
        if (msg.length() == 0 ){
            Toast.makeText(getApplicationContext(), "NO OH, OH, no empty text allowed"
                    , Toast.LENGTH_LONG).show();
            return;
        }
        ViewAdapter.addMsg(msg);

    }
}
