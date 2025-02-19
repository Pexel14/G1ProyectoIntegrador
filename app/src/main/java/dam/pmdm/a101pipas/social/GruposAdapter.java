package dam.pmdm.a101pipas.social;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dam.pmdm.a101pipas.R;

public class GruposAdapter extends RecyclerView.Adapter<GruposAdapter.GrupoViewHolder> {

    private List<Grupo> gruposList;

    // Constructor
    public GruposAdapter(List<Grupo> gruposList) {
        this.gruposList = gruposList;
    }

    @NonNull
    @Override
    public GrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grupo, parent, false);
        return new GrupoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoViewHolder holder, int position) {
        Grupo grupo = gruposList.get(position);

        // Cargar imagen con Picasso (si no hay imagen, usar una por defecto)
        if (grupo.getFotoGrupo() == null || grupo.getFotoGrupo().isEmpty()) {
            holder.imgGrupo.setImageResource(R.drawable.perfil_por_defecto);
        } else {
            Picasso.get().load(grupo.getFotoGrupo()).placeholder(R.drawable.perfil_por_defecto).into(holder.imgGrupo);
        }

        // Asignar datos
        holder.tvNombreGrupo.setText(grupo.getNombreGrupo());

        // Evento de clic para abrir la pantalla de detalles
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), GroupDetail.class);
            intent.putExtra("groupId", grupo.getIdGrupo()); // Pasar solo el ID del grupo
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return gruposList.size();
    }

    // Método para actualizar la lista de grupos
    public void setGruposList(List<Grupo> nuevaLista) {
        this.gruposList = nuevaLista;
        notifyDataSetChanged();
    }

    static class GrupoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGrupo;
        TextView tvNombreGrupo;

        public GrupoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGrupo = itemView.findViewById(R.id.imgGrupo);
            tvNombreGrupo = itemView.findViewById(R.id.tvNombreGrupo);
        }
    }
}
