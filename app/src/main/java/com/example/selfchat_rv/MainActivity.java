package com.example.selfchat_rv;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MyAdapter.recItemOnLongClick{

    public static final String DATA_SIZE = "Datasize";
    public static final String DATA_LIST = "sent";

    EditText input;
    RecyclerView recyclerView;
    MyAdapter ViewAdapter;
    Button button;

    SharedPreferences MySharedPrefrance;
    SharedPreferences.Editor MyEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.RV);
        input = (EditText) findViewById(R.id.plain_text_input);
        button = (Button) findViewById(R.id.button);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MySharedPrefrance = PreferenceManager.getDefaultSharedPreferences(this);


        int size = MySharedPrefrance.getInt(DATA_SIZE, 0);
        MyEditor = MySharedPrefrance.edit();
        ViewAdapter = new MyAdapter(size , MySharedPrefrance, MyEditor);
        ViewAdapter.setClickListener((MyAdapter.recItemOnLongClick) this);
        recyclerView.setAdapter(ViewAdapter);

        if(size != 0 ) {
            ViewAdapter.loading();
        }


        if (savedInstanceState != null)
        {
            String previusMessages = (savedInstanceState.getString("in"));
            ViewAdapter.setData(savedInstanceState.getStringArrayList("list"));
            input.setText(previusMessages);
            ViewAdapter.supportConfigurationChange();
        }

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                String msg;
                msg = input.getText().toString();
                input.setText("");
                //incase they tried to add empty text
                if (msg.length() == 0) {
                    Toast.makeText(getApplicationContext(), "NO OH, OH, no empty text allowed"
                            , Toast.LENGTH_LONG).show();
                    return;
                }
                ViewAdapter.addMsg(msg);

            }
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("in", input.getText().toString());
        outState.putStringArrayList("list", ViewAdapter.getData());
    }


    @Override
    public void itemLongClick(View view, final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure you wana delete it???")
                .setMessage("this will be the point of no return ")
                .setPositiveButton("Yes Please", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ViewAdapter.deleteMessage(position);
                        Toast.makeText(getApplicationContext(),
                                "message is Gone never to be back again", Toast.LENGTH_LONG).show();

                    }
                })
                .setNegativeButton("Hell No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),
                                "Nothing to hide I see :)", Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton("Cancel", null)
                .show();
    }
}
