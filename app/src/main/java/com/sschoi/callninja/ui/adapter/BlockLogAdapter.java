package com.sschoi.callninja.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sschoi.callninja.R;
import com.sschoi.callninja.data.model.BlockLog;
import java.util.List;

/**
 * RecyclerView Adapter for displaying blocked call logs.
 */
public class BlockLogAdapter extends RecyclerView.Adapter<BlockLogAdapter.ViewHolder> {

    private List<BlockLog> blockLogs;

    public BlockLogAdapter(List<BlockLog> blockLogs) {
        this.blockLogs = blockLogs;
    }

    /**
     * Update the adapter data and refresh the list.
     */
    public void update(List<BlockLog> newLogs) {
        this.blockLogs = newLogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_block_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BlockLog log = blockLogs.get(position);
        holder.tvNumber.setText(log.getPhoneNumber());
        holder.tvTime.setText(log.getBlockTime());
    }

    @Override
    public int getItemCount() {
        return blockLogs != null ? blockLogs.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber;
        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
