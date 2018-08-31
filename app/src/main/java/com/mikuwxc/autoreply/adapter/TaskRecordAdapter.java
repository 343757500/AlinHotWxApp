package com.mikuwxc.autoreply.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikuwxc.autoreply.R;
import com.mikuwxc.autoreply.bean.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */
public class TaskRecordAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private List<Record> taskRecordList = new ArrayList<>();

    public TaskRecordAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Record> taskRecordList) {
        if (taskRecordList != null) {
            this.taskRecordList.addAll(taskRecordList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return taskRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return taskRecordList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Record record = taskRecordList.get(position);
        convertView = inflater.inflate(R.layout.record_item, null);
        TextView taskTv = (TextView) convertView.findViewById(R.id.task_tv);
        String format = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date(record.getLastUpdated()));
        taskTv.setText(format + record.getTaskName());
        return convertView;
    }
}
