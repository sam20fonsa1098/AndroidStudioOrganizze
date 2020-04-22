package com.example.organizze.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organizze.R;
import com.example.organizze.model.Moviment;

import java.util.List;

public class AdapterMoviments extends RecyclerView.Adapter<AdapterMoviments.MyViewHolder>{

    List<Moviment> moviments;
    Context context;

    public AdapterMoviments(List<Moviment> moviments, Context context) {
        this.moviments = moviments;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterMoviments.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_moviments, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMoviments.MyViewHolder holder, int position) {
        Moviment moviment = moviments.get(position);

        holder.titulo.setText(moviment.getDescription());
        holder.valor.setText(String.valueOf(moviment.getMoney()));
        holder.categoria.setText(moviment.getCategory());
        holder.valor.setTextColor(context.getResources().getColor(R.color.colorAccentReceita));

        if(moviment.getType().equals("Despesa")) {
            holder.valor.setTextColor(context.getResources().getColor(R.color.colorAccentDespesa));
            holder.valor.setText("-" + Double.toString(moviment.getMoney()));
        }
    }

    @Override
    public int getItemCount() {
        return moviments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titulo, valor, categoria;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textAdapterTitulo);
            valor = itemView.findViewById(R.id.textAdapterValor);
            categoria = itemView.findViewById(R.id.textAdapterCategoria);
        }
    }
}
