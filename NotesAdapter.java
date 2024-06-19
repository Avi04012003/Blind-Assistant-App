package com.example.ech.notes;

import android.content.Context;
import com.example.ech.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    @NonNull
    private Context context;
    private ArrayList<Notes> notesArrayList;
    private onNoteListener monNoteListener;

        public NotesAdapter(Context context, ArrayList<Notes> notes, onNoteListener monNoteListener)
    {
        this.context=context;
        this.notesArrayList=notes;
        this.monNoteListener=monNoteListener;
    }


    @NonNull
    public NotesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noterecycleview, parent, false);
        return new MyViewHolder(view,monNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.MyViewHolder holder, int position) {
        Notes notes = notesArrayList.get(position);
        holder.mid.setText(String.valueOf(notes.getMid()));
        holder.message.setText(notes.getMessage());
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size()<1?0:notesArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView message,mid;
        onNoteListener onNoteListener;


        public MyViewHolder(@NonNull View itemView ,onNoteListener onNoteListener) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            mid = itemView.findViewById(R.id.mid);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
        onNoteListener.OnNoteClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {

            onNoteListener.onNoteLongClick(getAdapterPosition(), v);
            return true;
        }
    }
        public interface onNoteListener
        {
            void OnNoteClick(int position);
            void onNoteLongClick(int position,View v);

        }

}