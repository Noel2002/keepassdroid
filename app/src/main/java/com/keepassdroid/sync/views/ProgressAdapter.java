package com.keepassdroid.sync.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.keepass.R;
import com.keepassdroid.sync.models.ProcessProgress;

import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {

    private List<ProcessProgress> progressList;

    public ProgressAdapter(List<ProcessProgress> progressList) {
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_process_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        ProcessProgress progress = progressList.get(position);
        holder.bind(progress);
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private TextView processName;
        private TextView processStatus;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            processName = itemView.findViewById(R.id.process_name);
            processStatus = itemView.findViewById(R.id.process_status);
        }

        public void bind(ProcessProgress progress) {
            processName.setText(progress.getProcess());
            processStatus.setText(progress.getStatus());
        }
    }
}
