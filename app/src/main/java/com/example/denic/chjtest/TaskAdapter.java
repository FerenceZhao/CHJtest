package com.example.denic.chjtest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Denic on 2017/12/25.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task_class> taskLists;
   static class ViewHolder extends RecyclerView.ViewHolder{
       TextView TaskName;
       public  ViewHolder(View view){
           super(view);
           TaskName=(TextView)view.findViewById(R.id.Task_Name);

       }
   }
   public TaskAdapter( List<Task_class> taskList){
       taskLists=taskList;
   }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,parent,false);
       ViewHolder holder=new ViewHolder(view);
       return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task_class taskList=taskLists.get(position);
        holder.TaskName.setText(taskList.getName());
    }

    @Override
    public int getItemCount() {
        return taskLists.size();
    }
}
