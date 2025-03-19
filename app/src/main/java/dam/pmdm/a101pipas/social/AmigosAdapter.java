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
import dam.pmdm.a101pipas.models.Amigos;
import dam.pmdm.a101pipas.perfil.PerfilFragment;
import dam.pmdm.a101pipas.perfil.PerfilViewModel;

public class AmigosAdapter extends RecyclerView.Adapter<AmigosAdapter.AmigoViewHolder> {

    private List<Amigos> amigosList;
    private Fragment fragment;
    // Constructor
    public AmigosAdapter(List<Amigos> amigosList, Fragment fragment) {
        this.amigosList = amigosList;
        this.fragment = fragment;
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

        if(!amigo.getFotoPerfil().isEmpty()){
            Picasso.get()
                    .load(amigo.getFotoPerfil())
                    .placeholder(R.drawable.perfil_por_defecto) // Imagen de carga
                    .error(R.drawable.perfil_por_defecto) // Imagen de error
                    .into(holder.imgPerfil);
        }
        holder.tvUsername.setText(amigo.getUsername());

        PerfilViewModel perfilViewModel = new ViewModelProvider(fragment.requireActivity()).get(PerfilViewModel.class);

        holder.itemView.setOnClickListener(view -> {
            PerfilAmigoFragment.setTipo(1);
            perfilViewModel.setUsuarioId(amigo.getEmail().split("@")[0].replace(".",""));
            Navigation.findNavController(view).navigate(R.id.navigation_perfil_amigo);
        });
    }

    @Override
    public int getItemCount() {
        return amigosList.size();
    }

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
