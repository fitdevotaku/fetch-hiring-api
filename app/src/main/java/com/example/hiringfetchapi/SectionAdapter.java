package com.example.hiringfetchapi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SectionAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM   = 1;
    private final List<String> lines;

    public SectionAdapter(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public int getItemViewType(int position) {
        //  marker for a header is just a trailing ":" on that line
        return lines.get(position).endsWith(":")
                ? TYPE_HEADER
                : TYPE_ITEM;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(
                    R.layout.row_header, parent, false);
            TextView tv = v.findViewById(R.id.tvHeader);
            return new HeaderVH(tv);
        } else {
            View v = inflater.inflate(
                    R.layout.row_item, parent, false);
            TextView tv = v.findViewById(R.id.tvName);
            return new ItemVH(tv);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder, int pos) {

        String text = lines.get(pos);
        if (holder instanceof HeaderVH) {
            ((HeaderVH) holder).tvHeader.setText(text);
        } else {
            ((ItemVH) holder).tvName.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    public static class HeaderVH extends RecyclerView.ViewHolder {
        public final TextView tvHeader;
        public HeaderVH(TextView itemView) {
            super(itemView);
            this.tvHeader = itemView;
        }
    }

    public static class ItemVH extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public ItemVH(TextView itemView) {
            super(itemView);
            this.tvName = itemView;
        }
    }
}
