package com.example.selfchat_rv;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MyAdapter.recItemOnLongClick{

    public static final String DATA_SIZE = "Datasize";
    public static final String DATA_LIST = "sent";
    public static final String FIRST_LUNCH = "first";

    public EditText input;
    public RecyclerView recyclerView;
    public MyAdapter ViewAdapter;
    public Button button;
    public FirebaseFirestore FireBase;
    public CollectionReference references;

    public SharedPreferences MySharedPrefrance;
    public SharedPreferences.Editor MyEditor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FireBase = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.RV);
        input = (EditText) findViewById(R.id.plain_text_input);
        button = (Button) findViewById(R.id.button);

        new FireBase().execute();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MySharedPrefrance = PreferenceManager.getDefaultSharedPreferences(this);

        int size = MySharedPrefrance.getInt(DATA_SIZE, 0);
        MyEditor = MySharedPrefrance.edit();
        ViewAdapter = new MyAdapter(size , MySharedPrefrance, MyEditor, FireBase);
        ViewAdapter.setClickListener((MyAdapter.recItemOnLongClick) this);
        recyclerView.setAdapter(ViewAdapter);

        if(size != 0 ) {
            ViewAdapter.loading();
        }


        if (savedInstanceState != null)
        {
            String previusMessages = (savedInstanceState.getString("in"));
            ViewAdapter.loading();
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
                ViewAdapter.AddMsgToFF(msg);

            }
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("in", input.getText().toString());
    }
    @Override
    public void onStart() {
        super.onStart();
        ViewAdapter.setData( new ArrayList<Message>());
        references.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    return;
                }
                for(DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges())
                {
                    DocumentSnapshot documentSnapshot = documentChange.getDocument();
                    String id = documentSnapshot.getId();
                    boolean isDocumentDeleted = documentChange.getOldIndex() != -1;
                    boolean isDocumentAdded = documentChange.getNewIndex() != -1;
                    if (isDocumentDeleted)
                    {
                        for(int index = 0 ; index < ViewAdapter.getData().size(); index++)
                            if (ViewAdapter.getData().get(index).getId().equals(id)) {
                                ViewAdapter.deleteMessage(index);
                                break;
                            }
                    }

                    else if(isDocumentAdded && !
                            documentSnapshot.getId().equals(MyAdapter.PROJEC_ID))
                    {
                        Map<String, Object> new_doc_data = documentSnapshot.getData();
                        String Id = new_doc_data.get(MyAdapter.ID_KEY)+"";
                        String content = new_doc_data.get(MyAdapter.TEXT_KEY)+"";
                        String timestamp = new_doc_data.get(MyAdapter.TIME_STAMP_KEY)+"";
                        ViewAdapter.addMsg(content, Id, timestamp);
                    }
                }
            }
        });
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

    private class FireBase extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            ViewAdapter.ReadMsgFromFF();
            return null;
        }
    }
}
