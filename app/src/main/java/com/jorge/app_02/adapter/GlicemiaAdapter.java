package com.jorge.app_02.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jorge.app_02.R;
import com.jorge.app_02.model.Glicemia;
import java.util.List;

public class GlicemiaAdapter extends RecyclerView.Adapter<GlicemiaAdapter.GlicemiaViewHolder> {

    // Interface para comunicar a ação de exclusão para a Activity
    public interface OnItemLongClickListener {
        void onItemLongClick(Glicemia glicemia);
    }

    private Context context;
    private List<Glicemia> listaGlicemias;
    private OnItemLongClickListener listener;

    public GlicemiaAdapter(Context context, List<Glicemia> listaGlicemias, OnItemLongClickListener listener) {
        this.context = context;
        this.listaGlicemias = listaGlicemias;
        this.listener = listener;
    }

    public static class GlicemiaViewHolder extends RecyclerView.ViewHolder {
        public TextView itemDataHora;
        public TextView itemValor;
        public TextView itemComentario;
        public TextView itemStatus;

        public GlicemiaViewHolder(@NonNull View itemView) {
            super(itemView);
            itemDataHora = itemView.findViewById(R.id.itemDataHora);
            itemValor = itemView.findViewById(R.id.itemValor);
            itemComentario = itemView.findViewById(R.id.itemComentario);
            itemStatus = itemView.findViewById(R.id.itemStatus);
        }
    }

    @NonNull
    @Override
    public GlicemiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_glicemia, parent, false);
        return new GlicemiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlicemiaViewHolder holder, int position) {
        Glicemia glicemia = listaGlicemias.get(position);

        // Preenche os TextViews com os dados do objeto
        holder.itemDataHora.setText(glicemia.getData() + " - " + glicemia.getHora());
        holder.itemValor.setText("Valor: " + glicemia.getGlicemia() + " mg/dL");
        holder.itemComentario.setText("Comentário: " + glicemia.getComentario());
        holder.itemStatus.setText("Status: " + glicemia.getStatus());

        // Adiciona um listener de toque longo para a exclusão
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(glicemia);
            }
            return true; // Retorna true para consumir o evento
        });
    }

    @Override
    public int getItemCount() {
        return listaGlicemias.size();
    }

    // Método para atualizar os dados da lista, usado para a busca
    public void updateList(List<Glicemia> newList) {
        listaGlicemias.clear();
        listaGlicemias.addAll(newList);
        notifyDataSetChanged();
    }
}
