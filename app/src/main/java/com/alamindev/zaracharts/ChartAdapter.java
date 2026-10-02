package com.alamindev.zaracharts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {

    private List<ChartModel> chartList;
    private final OnChartClickListener listener;

    public interface OnChartClickListener {
        void OnItemClick(ChartModel chart);
    }

    public ChartAdapter(List<ChartModel> chartList, OnChartClickListener listener) {
        this.chartList = chartList;
        this.listener = listener;
    }

    public void filterList(List<ChartModel> filteredList) {
        this.chartList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChartModel chart = chartList.get(position);
        holder.tvCutNo.setText("Cut No: " + chart.getCutNo());
        holder.tvColour.setText(chart.getColour());
        holder.tvRatio.setText("Ratio: " + chart.getRatio());
        holder.tvTotalPcs.setText("Total: " + (int)chart.getTotalPcs() + " Pcs");

        holder.itemView.setOnClickListener(v -> listener.OnItemClick(chart));
    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCutNo, tvColour, tvRatio, tvTotalPcs;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCutNo = itemView.findViewById(R.id.tvCutNo);
            tvColour = itemView.findViewById(R.id.tvColour);
            tvRatio = itemView.findViewById(R.id.tvRatio);
            tvTotalPcs = itemView.findViewById(R.id.tvTotalPcs);
        }
    }
}