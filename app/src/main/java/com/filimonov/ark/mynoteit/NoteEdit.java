package com.filimonov.ark.mynoteit;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ArkFil on 23.09.2015.
 */
public class NoteEdit extends Activity {
    private static final String TAG = "myLogs";
    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "вторая активность NoteEdit только что создана");

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//убрать тайтл

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }

        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {
                         public void onClick(View view) {
                             EditText mTitleText = (EditText) findViewById(R.id.title);//фильтр добавления пустой строки кнопкой
                             setResult(RESULT_OK);
                             //проверка на пустоту
                             if(mTitleText.getText().length() > 0) finish();
             }}
        );

    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        Log.d(TAG, "saveState<передача базе данных из полей title и body в метода createNote, или передача в метод updateNote>");
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (title.length()>0) {

            if (mRowId == null) {
                long id = mDbHelper.createNote(title, body);
                Log.d(TAG, "запустили createNote БД");
                if (id > 0) {
                    mRowId = id;
                }
            } else {
                mDbHelper.updateNote(mRowId, title, body);
                Log.d(TAG, "запустили updateNote БД");
            }
        }
    }

}