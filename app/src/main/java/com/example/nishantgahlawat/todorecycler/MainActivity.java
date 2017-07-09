package com.example.nishantgahlawat.todorecycler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ToDoAdapter.CheckBoxButtonListener, ToDoAdapter.TitleTextViewClistener, ToDoAdapter.TitleTextViewLongClickListener {

    private static final int NEW_TODO = 1;
    private static final int DETAILS_TODO =2;
    RecyclerView rvToDo;
    ArrayList<ToDoItem> toDoItemArrayList;
    ToDoAdapter toDoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvToDo = (RecyclerView)findViewById(R.id.ToDoRecyclerView);

        toDoItemArrayList = new ArrayList<>();

        toDoAdapter = new ToDoAdapter(toDoItemArrayList,this);
        toDoAdapter.setCheckBoxButtonListener(this);
        toDoAdapter.setTitleTextViewClistener(this);
        toDoAdapter.setTitleTextViewLongClickListener(this);

        rvToDo.setAdapter(toDoAdapter);
        rvToDo.setLayoutManager(new LinearLayoutManager(this));

        getNotificationItemUpdate();

        updateToDoList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NewToDo.class);
                startActivityForResult(intent,NEW_TODO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case NEW_TODO:
                if(resultCode==RESULT_OK){
                    ToDoItem toDoItem = (ToDoItem) data.getSerializableExtra(IntentConstraints.NewToDoExtra);

                    toDoItemArrayList.add(toDoItem);
                    toDoAdapter.notifyItemInserted(toDoItemArrayList.size());

                    if(toDoItem.hasReminder()){
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                        Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(IntentConstraints.NotificationToDoExtra,toDoItem);
                        intent.putExtra(IntentConstraints.NotificationBundleExtra,bundle);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int)toDoItem.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.setExact(AlarmManager.RTC,toDoItem.getReminder(),pendingIntent);
                    }
                }
                break;
            case DETAILS_TODO:
                if(resultCode==RESULT_OK){
                    ToDoItem toDoItem = (ToDoItem)data.getSerializableExtra(IntentConstraints.DetailsToDoExtra);
                    int position = data.getIntExtra(IntentConstraints.DetailsPositionExtra,-1);

                    ToDoItem toDoItemToChange = toDoItemArrayList.get(position);
                    toDoItemToChange.setTitle(toDoItem.getTitle());
                    toDoItemToChange.setDescription(toDoItem.getDescription());
                    toDoItemToChange.setDone(toDoItem.isDone());
                    toDoItemToChange.setReminder(toDoItem.getReminder());

                    toDoAdapter.notifyItemChanged(position);

                    ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getToDoOpenHelperInstance(this);
                    SQLiteDatabase sqLiteDatabase = toDoOpenHelper.getReadableDatabase();

                    String selection = ToDoOpenHelper.TODO_ID+"="+toDoItem.getId();

                    ContentValues cv = new ContentValues();
                    cv.put(ToDoOpenHelper.TODO_TITLE,toDoItem.getTitle());
                    cv.put(ToDoOpenHelper.TODO_DESCRIPTION,toDoItem.getDescription());
                    cv.put(ToDoOpenHelper.TODO_DONE,toDoItem.isDone()?1:0);
                    cv.put(ToDoOpenHelper.TODO_REMINDER,toDoItem.getReminder());

                    sqLiteDatabase.update(ToDoOpenHelper.TODO_TABLE_NAME,cv,selection,null);

                    boolean reminderChanged = data.getBooleanExtra(IntentConstraints.DetailsReminderChanged,false);
                    if(reminderChanged){
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                        Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int)toDoItem.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.cancel(pendingIntent);

                        intent = new Intent(MainActivity.this,AlarmReceiver.class);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(IntentConstraints.NotificationToDoExtra,toDoItem);
                        intent.putExtra(IntentConstraints.NotificationBundleExtra,bundle);

                        pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int)toDoItem.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmManager.setExact(AlarmManager.RTC,toDoItem.getReminder(),pendingIntent);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getNotificationItemUpdate() {
        Intent notifIntent = getIntent();
        ToDoItem notifToDoItem = (ToDoItem) notifIntent.getSerializableExtra(IntentConstraints.DetailsToDoExtra);
        if(notifToDoItem!=null) {
            ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getToDoOpenHelperInstance(this);
            SQLiteDatabase sqLiteDatabase = toDoOpenHelper.getReadableDatabase();

            String selection = ToDoOpenHelper.TODO_ID + "=" + notifToDoItem.getId();

            ContentValues cv = new ContentValues();
            cv.put(ToDoOpenHelper.TODO_TITLE, notifToDoItem.getTitle());
            cv.put(ToDoOpenHelper.TODO_DESCRIPTION, notifToDoItem.getDescription());
            cv.put(ToDoOpenHelper.TODO_DONE, notifToDoItem.isDone() ? 1 : 0);
            cv.put(ToDoOpenHelper.TODO_REMINDER, notifToDoItem.getReminder());

            sqLiteDatabase.update(ToDoOpenHelper.TODO_TABLE_NAME, cv, selection, null);

            boolean reminderChanged = notifIntent.getBooleanExtra(IntentConstraints.DetailsReminderChanged, false);
            if (reminderChanged) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, (int) notifToDoItem.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.cancel(pendingIntent);

                intent = new Intent(MainActivity.this, AlarmReceiver.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(IntentConstraints.NotificationToDoExtra, notifToDoItem);
                intent.putExtra(IntentConstraints.NotificationBundleExtra, bundle);

                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, (int) notifToDoItem.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.setExact(AlarmManager.RTC, notifToDoItem.getReminder(), pendingIntent);
            }
        }
    }

    private void updateToDoList() {
        toDoItemArrayList.clear();
        ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getToDoOpenHelperInstance(this);
        SQLiteDatabase sqLiteDatabase = toDoOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(ToDoOpenHelper.TODO_TABLE_NAME,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(ToDoOpenHelper.TODO_DESCRIPTION));
            long id = cursor.getLong(cursor.getColumnIndex(ToDoOpenHelper.TODO_ID));
            long created = cursor.getLong(cursor.getColumnIndex(ToDoOpenHelper.TODO_CREATED));
            boolean done = (cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_DONE))==0)?false:true;
            long reminder = cursor.getInt(cursor.getColumnIndex(ToDoOpenHelper.TODO_REMINDER));

            ToDoItem toDoItem = new ToDoItem(id,title,description,done,created);
            toDoItem.setReminder(reminder);

            toDoItemArrayList.add(toDoItem);
        }
        cursor.close();
        toDoAdapter.notifyItemRangeInserted(0,toDoItemArrayList.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckBoxClickListener(int position) {
        ToDoItem toDoItem = toDoItemArrayList.get(position);
        toDoItem.toggleDone();
        toDoAdapter.notifyItemChanged(position);
    }

    @Override
    public void onTitleClickListener(int position) {
        Intent intent = new Intent(MainActivity.this,ToDoDetails.class);
        intent.putExtra(IntentConstraints.DetailsPositionExtra,position);
        intent.putExtra(IntentConstraints.DetailsToDoExtra, toDoItemArrayList.get(position));
        startActivityForResult(intent,DETAILS_TODO);
    }

    @Override
    public void onTitleLongClickListener(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Delete");
        builder.setMessage("Are You Sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ToDoItem toDoItem = toDoItemArrayList.get(position);

                ToDoOpenHelper toDoOpenHelper = ToDoOpenHelper.getToDoOpenHelperInstance(MainActivity.this);
                SQLiteDatabase sqLiteDatabase = toDoOpenHelper.getReadableDatabase();

                String selection = ToDoOpenHelper.TODO_ID+"="+toDoItem.getId();

                sqLiteDatabase.delete(ToDoOpenHelper.TODO_TABLE_NAME,selection,null);

                if(toDoItem.hasReminder()){
                    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                    Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int)toDoItem.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManager.cancel(pendingIntent);
                }

                MainActivity.this.toDoItemArrayList.remove(toDoItem);
                MainActivity.this.toDoAdapter.notifyItemRemoved(position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
