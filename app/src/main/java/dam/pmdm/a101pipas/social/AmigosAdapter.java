package dam.pmdm.a101pipas.social;

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

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder> {

    private List<Amigos> amigosList;

    // Constructor
    public AmigosAdapter(List<Amigos> amigosList) {
        this.amigosList = amigosList;
    }

    @NonNull
    @Override
    public AmigoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_amigo, parent, false);
        return new AmigoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmigoViewHolder holder, int position) {
        Amigos amigo = amigosList.get(position);

        // Cargar imagen de perfil con Picasso
        Picasso.get()
                .load(amigo.getFotoPerfil())
                .placeholder(R.drawable.perfil_por_defecto) // Imagen de carga
                .error(R.drawable.perfil_por_defecto) // Imagen de error
                .into(holder.imgPerfil);

        // Configurar datos del usuario
        holder.tvUsername.setText(amigo.getUsername());
    }

    @Override
    public int getItemCount() {
        return amigosList.size();
    }

    // Método para actualizar la lista de amigos
    public void setAmigosList(List<Amigos> nuevaLista) {
        this.amigosList = nuevaLista;
        notifyDataSetChanged();
    }

    static class AmigoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPerfil;
        TextView tvUsername;

        public AmigoViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPerfil = itemView.findViewById(R.id.imgPerfil);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }
}
