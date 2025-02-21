package dam.pmdm.a101pipas.social;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.ranking.GrupoViewModel;

public class GruposAdapter extends RecyclerView.Adapter<GruposAdapter.GrupoViewHolder> {

    private List<Grupo> gruposList;
    private Fragment fragment;

    // Constructor
    public GruposAdapter(List<Grupo> gruposList, Fragment fragment) {
        this.gruposList = gruposList;
        this.fragment = fragment;
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

        GrupoViewModel grupoViewModel = new ViewModelProvider(fragment.requireActivity()).get(GrupoViewModel.class);

        // Evento de clic para abrir la pantalla de detalles
        holder.itemView.setOnClickListener(v -> {
            grupoViewModel.setIdGrupo(Integer.parseInt(grupo.getIdGrupo()));
            Navigation.findNavController(v).navigate(R.id.navigation_grupo);
//            Intent intent = new Intent(v.getContext(), GroupDetail.class);
//            intent.putExtra("groupId", grupo.getIdGrupo()); // Pasar solo el ID del grupo
//            v.getContext().startActivity(intent);
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
