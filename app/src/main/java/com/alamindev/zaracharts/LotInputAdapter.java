package com.alamindev.zaracharts;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class LotInputAdapter extends RecyclerView.Adapter<LotInputAdapter.ViewHolder> {

    private final List<LotInputModel> list;

    public LotInputAdapter(List<LotInputModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lot_input, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LotInputModel item = list.get(position);
        holder.etLotNo.setText(item.getLotNo());
        holder.etShade.setText(item.getShade());
        holder.etLayQty.setText(item.getLayQty() > 0 ? String.valueOf(item.getLayQty()) : "");
        holder.etBundleRange.setText(item.getBundleRange());

        holder.btnRemoveRow.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                list.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText etLotNo, etShade, etLayQty, etBundleRange;
        ImageButton btnRemoveRow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etLotNo = itemView.findViewById(R.id.etRowLotNo);
            etShade = itemView.findViewById(R.id.etRowShade);
            etLayQty = itemView.findViewById(R.id.etRowLayQty);
            etBundleRange = itemView.findViewById(R.id.etRowBundleRange);
            btnRemoveRow = itemView.findViewById(R.id.btnRemoveRow);

            etLotNo.addTextChangedListener(new CustomTextWatcher() {
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION)
                        list.get(getAdapterPosition()).setLotNo(s.toString());
                }
            });

            etShade.addTextChangedListener(new CustomTextWatcher() {
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION)
                        list.get(getAdapterPosition()).setShade(s.toString());
                }
            });

            etLayQty.addTextChangedListener(new CustomTextWatcher() {
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        try {
                            double val = Double.parseDouble(s.toString());
                            list.get(getAdapterPosition()).setLayQty(val);
                        } catch (Exception e) {
                            list.get(getAdapterPosition()).setLayQty(0);
                        }
                    }
                }
            });

            etBundleRange.addTextChangedListener(new CustomTextWatcher() {
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION)
                        list.get(getAdapterPosition()).setBundleRange(s.toString());
                }
            });
        }
    }

    abstract static class CustomTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}