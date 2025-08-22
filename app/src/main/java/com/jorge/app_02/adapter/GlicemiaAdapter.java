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

    private final Context context;
    private final List<Glicemia> glicemiaList;

    public GlicemiaAdapter(Context context, List<Glicemia> glicemiaList) {
        this.context = context;
        this.glicemiaList = glicemiaList;
    }

    @NonNull
    @Override
    public GlicemiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_glicemia, parent, false);
        return new GlicemiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlicemiaViewHolder holder, int position) {
        Glicemia glicemia = glicemiaList.get(position);
        holder.bind(glicemia);
    }

    @Override
    public int getItemCount() {
        return glicemiaList.size();
    }

    public static class GlicemiaViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewData;
        private final TextView textViewHora;
        private final TextView textViewGlicemia;
        private final TextView textViewStatus;
        private final TextView textViewComentario;

        public GlicemiaViewHolder(@NonNull View itemView) {
            super(itemView);
            // CORREÇÃO: Certifique-se de que os IDs correspondem exatamente aos do seu XML
            textViewData = itemView.findViewById(R.id.textViewData);
            textViewHora = itemView.findViewById(R.id.textViewHora);
            textViewGlicemia = itemView.findViewById(R.id.editTextGlicemia);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewComentario = itemView.findViewById(R.id.editTextComentario);
        }

        public void bind(Glicemia glicemia) {
            // CORREÇÃO: Adicionadas verificações de null para evitar crashes
            if (textViewData != null) {
                textViewData.setText(glicemia.getData());
            }
            if (textViewHora != null) {
                textViewHora.setText(glicemia.getHora());
            }
            if (textViewGlicemia != null) {
                textViewGlicemia.setText(String.valueOf(glicemia.getGlicemia()));
            }
            if (textViewStatus != null) {
                textViewStatus.setText(glicemia.getStatus());
            }
            if (textViewComentario != null) {
                textViewComentario.setText(glicemia.getComentario());
            }
        }
    }
}