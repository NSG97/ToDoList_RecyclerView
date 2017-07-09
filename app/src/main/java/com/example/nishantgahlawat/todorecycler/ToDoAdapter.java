package com.example.nishantgahlawat.todorecycler;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nishant Gahlawat on 09-07-2017.
 */

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private ArrayList<ToDoItem> toDoItemArrayList;
    private Context context;

    CheckBoxButtonListener checkBoxButtonListener;
    TitleTextViewClistener titleTextViewClistener;
    TitleTextViewLongClickListener titleTextViewLongClickListener;

    public ToDoAdapter(ArrayList<ToDoItem> toDoItemArrayList, Context context) {
        this.toDoItemArrayList = toDoItemArrayList;
        this.context = context;
    }

    public void setCheckBoxButtonListener(CheckBoxButtonListener checkBoxButtonListener){
        this.checkBoxButtonListener = checkBoxButtonListener;
    }

    public void setTitleTextViewClistener(TitleTextViewClistener titleTextViewClistener){
        this.titleTextViewClistener = titleTextViewClistener;
    }

    public void setTitleTextViewLongClickListener(TitleTextViewLongClickListener titleTextViewLongClickListener){
        this.titleTextViewLongClickListener = titleTextViewLongClickListener;
    }

    private Context getContext(){
        return context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ListItemTitleTV;
        public ImageButton checkBoxIB;


        public ViewHolder(View itemView) {
            super(itemView);

            ListItemTitleTV = (TextView) itemView.findViewById(R.id.ListItemTitle);
            checkBoxIB = (ImageButton) itemView.findViewById(R.id.ListItemCheckBox);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item,parent,false);

        ViewHolder viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        ToDoItem toDoItem = toDoItemArrayList.get(position);

        holder.ListItemTitleTV.setText(toDoItem.getTitle());
        holder.checkBoxIB.setBackgroundResource(toDoItem.isDone()?android.R.drawable.checkbox_on_background:android.R.drawable.checkbox_off_background);

        holder.checkBoxIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxButtonListener!=null)
                    checkBoxButtonListener.onCheckBoxClickListener(position);
            }
        });

        holder.ListItemTitleTV.setLongClickable(true);

        holder.ListItemTitleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleTextViewClistener!=null)
                    titleTextViewClistener.onTitleClickListener(position);
            }
        });

        holder.ListItemTitleTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(titleTextViewLongClickListener!=null)
                    titleTextViewLongClickListener.onTitleLongClickListener(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return toDoItemArrayList.size();
    }

    interface CheckBoxButtonListener{
        public void onCheckBoxClickListener(int position);
    }

    interface TitleTextViewClistener{
        public void onTitleClickListener(int position);
    }

    interface TitleTextViewLongClickListener{
        public void onTitleLongClickListener(int position);
    }
}
