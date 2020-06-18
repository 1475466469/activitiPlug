package com.activiti6.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class testlistener implements TaskListener  {


    @Override
    public void notify(DelegateTask delegateTask) {
        List<String> list=new ArrayList<>();
        list.add("1111");
        list.add("2222");
        delegateTask.addCandidateUsers(list);

    }



}
