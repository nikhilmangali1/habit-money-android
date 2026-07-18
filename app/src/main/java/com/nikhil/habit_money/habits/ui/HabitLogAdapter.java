package com.nikhil.habit_money.habits.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.habit_money.R;
import com.nikhil.habit_money.habits.model.HabitLog;

import java.util.ArrayList;
import java.util.List;

public class HabitLogAdapter extends RecyclerView.Adapter<HabitLogAdapter.ViewHolder> {

    private final List<HabitLog> logs = new ArrayList<>();

    public void setLogs(List<HabitLog> logs) {
        this.logs.clear();
        this.logs.addAll(logs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HabitLog log = logs.get(position);
        holder.dateText.setText(log.getLogDate());
        holder.statusText.setText("COMPLETED".equals(log.getStatus()) ? "✅" : "❌");
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final TextView statusText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.logDateText);
            statusText = itemView.findViewById(R.id.logStatusText);
        }
    }
}
