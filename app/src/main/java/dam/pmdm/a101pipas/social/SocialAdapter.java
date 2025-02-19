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
import dam.pmdm.a101pipas.models.Amigos;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.SocialViewHolder> {

    private List<?> listaSocial; // Lista que maneja amigos y grupos

    public SocialAdapter(List<?> listaSocial) {
        this.listaSocial = listaSocial;
    }

    @NonNull
    @Override
    public SocialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social, parent, false);
        return new SocialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialViewHolder holder, int position) {
        Object item = listaSocial.get(position);

        if (item instanceof Amigos) {
            // 🟢 Es un amigo
            Amigos amigo = (Amigos) item;
            holder.tvNombre.setText(amigo.getUsername());

            Picasso.get()
                    .load(amigo.getFotoPerfil() != null ? amigo.getFotoPerfil() : "")
                    .placeholder(R.drawable.perfil_por_defecto)
                    .error(R.drawable.perfil_por_defecto)
                    .into(holder.imgPerfil);

        } else if (item instanceof Grupo) {
            // 🔵 Es un grupo
            Grupo grupo = (Grupo) item;
            holder.tvNombre.setText(grupo.getNombreGrupo());

            Picasso.get()
                    .load(grupo.getFotoGrupo() != null ? grupo.getFotoGrupo() : "")
                    .placeholder(R.drawable.perfil_por_defecto)
                    .error(R.drawable.perfil_por_defecto)
                    .into(holder.imgPerfil);

            // 🟡 Evento de clic para abrir `GroupDetail`
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), GroupDetail.class);
                intent.putExtra("groupId", grupo.getIdGrupo());
                intent.putExtra("nombreGrupo", grupo.getNombreGrupo());
                intent.putExtra("fotoGrupo", grupo.getFotoGrupo());
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaSocial.size();
    }

    public void setListaSocial(List<?> nuevaLista) {
        this.listaSocial = nuevaLista;
        notifyDataSetChanged();
    }

    static class SocialViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPerfil;
        TextView tvNombre;

        public SocialViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPerfil = itemView.findViewById(R.id.imgPerfilSocial);
            tvNombre = itemView.findViewById(R.id.tvUsernameSocial);
        }
    }
}
