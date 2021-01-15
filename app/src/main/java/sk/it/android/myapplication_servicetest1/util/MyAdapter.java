package sk.it.android.myapplication_servicetest1.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sk.it.android.myapplication_servicetest1.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final OnItemListener onItemListener;
    private final List<Song> list;

    public MyAdapter(ArrayList<Song> arrayList, OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
        this.list = arrayList;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnItemListener onItemListener;
        private final TextView title, artist, duration;

        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);

            title = itemView.findViewById(R.id.textViewTitle);
            artist = itemView.findViewById(R.id.textViewArtist);
            duration = itemView.findViewById(R.id.textViewDuration);

            title.setSelected(true);
        }

        public void bind(Song song) {
            this.title.setText(song.getTitle());
            this.artist.setText(song.getArtist());
            this.duration.setText(song.getDurationReadable());
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
