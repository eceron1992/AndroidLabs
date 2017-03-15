package com.example.ezekielceron.androidlabs;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.widget.TextView;

public class ChatWindow extends AppCompatActivity {
    final String ACTIVITY_NAME = "ChatWindow";
    private ArrayList<String> list;
    private ListView listView;
    private Button sendButton;
    private EditText messageText;
    ChatDatabaseHelper cdHelper;
    SQLiteDatabase sDB;
    ContentValues cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        list = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.chat_listView);
        cdHelper = new ChatDatabaseHelper(this);
        messageText = (EditText) findViewById(R.id.msg_text);
        final ChatAdapter messageAdapter =new ChatAdapter( this );
        listView.setAdapter (messageAdapter);
        messageAdapter.notifyDataSetChanged();
        sendButton = (Button) findViewById(R.id.button2);


        sDB = cdHelper.getWritableDatabase();
        Cursor cursor = sDB.query(false, "CHATS", new String[] {"ID", "MESSAGE"}, "ID not null", null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast() ) {
            String ms = cursor.getString(1);
            list.add(ms);
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            cursor.moveToNext();
        }
        Log.i(ACTIVITY_NAME, "Cursor\'s  column count =" + cursor.getColumnCount() );
        for(int i = 0; i < cursor.getColumnCount(); i++){
            Log.i(ACTIVITY_NAME, "Cursor\'s column count = " + cursor.getColumnName(i));
        }
        cv = new ContentValues();
        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.i(ACTIVITY_NAME, "Clicked the send button");
                String message = messageText.getText().toString();
                list.add(message);
                messageAdapter.notifyDataSetChanged();
                cv.put("MESSAGE",message );
                sDB.insert("CHATS", "Null replacement value", cv);
                messageText.setText("");
            }
        });
    }

    private class ChatAdapter extends ArrayAdapter<String>{
        public ChatAdapter(Context ctx){
            super(ctx, 0);
        }

        public int getCount(){
            return list.size();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;
            if(position%2 ==0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView)result.findViewById(R.id.message_text);
            message.setText(   getItem(position)  ); // get the string at position
            return result;
        }

        public String getItem(int position){
            return list.get(position);
        }


    }
    protected void onDestroy() {
        super.onDestroy();
        sDB.close();
    }

}
