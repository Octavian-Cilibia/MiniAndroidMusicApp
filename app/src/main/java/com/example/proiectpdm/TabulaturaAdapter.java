package com.example.proiectpdm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class TabulaturaAdapter extends RecyclerView.Adapter<TabulaturaAdapter.MyViewHolder> {

    private ArrayList<Nota> note;
    private MainActivity mainActivity;

    public TabulaturaAdapter(ArrayList<Nota> note, MainActivity mainActivity) {
        this.note = note;
        this.mainActivity = mainActivity;
    }

    public TabulaturaAdapter(ArrayList<Nota> note){
        this.note = note;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt;

        public MyViewHolder(final View view){
            super(view);
            nameTxt = view.findViewById(R.id.textView);
        }
    }

    @NonNull
    @Override
    public TabulaturaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tabulatura, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TabulaturaAdapter.MyViewHolder holder, int position) {
        String name = note.get(holder.getAdapterPosition()).getName();
        holder.nameTxt.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) v.getContext()).showDeleteConfirmationDialog(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return note.size();
    }
}
