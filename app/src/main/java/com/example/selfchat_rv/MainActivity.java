package com.example.selfchat_rv;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyUtil.MsgClickCallback {//implements View.OnClickListener{

    EditText input;
    RecyclerView recyclerView;
    MyAdapter ViewAdapter;
    Button button;

    MyDatabase room;
    MyUtil.MsgAdapter adapter = new MyUtil.MsgAdapter();
    ArrayList<Messege> msgs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.RV);
        input = (EditText) findViewById(R.id.plain_text_input);
        button = (Button) findViewById(R.id.button);

        room = MyDatabase.getDatabase(this);
        msgs.addAll(room.msgDao().getAllMsgs());

        Log.v("size of messages list",Integer.toString(msgs.size()));


        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.callback = this;
        adapter.submitList(msgs);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = input.getText().toString();
                Messege msg = new Messege(text);
                //incase they tried to add empty text
                if (text.length() == 0 ){
                    Toast.makeText(getApplicationContext(), "NO OH, OH, no empty text allowed"
                            , Toast.LENGTH_LONG).show();
                    return;
                } else {
                    input.setText("");
                    msgs.add(msg);
                    room.msgDao().insert(msg);
                    adapter.submitList(msgs);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("messages", (ArrayList<? extends Parcelable>) msgs);
    }

    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        msgs = state.getParcelableArrayList("messages");
        adapter.submitList(msgs);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onMsgClick(final Messege msg) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ArrayList<Messege> msgCopy = new ArrayList<>(msgs);
                        msgCopy.remove(msg);
                        room.msgDao().delete(msg);
                        msgs = msgCopy;
                        adapter.submitList(msgs);
                        adapter.notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
