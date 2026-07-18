package com.nikhil.habit_money.habits.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.habit_money.R;
import com.nikhil.habit_money.habits.model.Habit;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder> {

    private final List<Habit> habits;
    private final OnHabitClickListener listener;

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
        void onCheckChanged(Habit habit, boolean isChecked);
    }

    public HabitAdapter(List<Habit> habits, OnHabitClickListener listener) {
        this.habits = habits;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habits.get(position);
        holder.bind(habit, listener);
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView titleText;
        private final TextView categoryText;
        private final TextView streakText;
        private final CheckBox completedCheck;
        private final ImageView chevron;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            titleText = itemView.findViewById(R.id.habitTitle);
            categoryText = itemView.findViewById(R.id.habitCategory);
            streakText = itemView.findViewById(R.id.streakText);
            completedCheck = itemView.findViewById(R.id.habitCompleted);
            chevron = itemView.findViewById(R.id.habitChevron);
        }

        void bind(Habit habit, OnHabitClickListener listener) {
            titleText.setText(habit.getTitle());
            categoryText.setText(habit.getCategory() != null ? habit.getCategory().getName() : "");
            streakText.setText(itemView.getContext().getString(R.string.streak_format, habit.getStreakCount()));

            completedCheck.setOnCheckedChangeListener(null);
            completedCheck.setChecked(habit.isCompletedToday());

            cardView.setOnClickListener(v -> listener.onCheckChanged(habit, !habit.isCompletedToday()));
            chevron.setOnClickListener(v -> listener.onHabitClick(habit));
        }
    }
}
