package com.example.denic.chjtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denic on 2017/12/15.
 */

public class TaskFragment extends Fragment {
    private List<Task_class> taskLists=new ArrayList<>();
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.task_fragment,container,false);
        initTask();
        RecyclerView recyclerView=(RecyclerView) view.findViewById(R.id.task_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        TaskAdapter adapter=new TaskAdapter(taskLists);
        recyclerView.setAdapter(adapter);

        return  view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    private void initTask(){

        for ( int i=0;i<30;i++){
            String name= String.valueOf(i);
            Task_class taskList=new Task_class(name);
            taskLists.add(taskList);
        }
    }
}
