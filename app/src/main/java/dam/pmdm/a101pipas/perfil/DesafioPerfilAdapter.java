package dam.pmdm.a101pipas.perfil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.DesafioPerfil;

public class DesafioPerfilAdapter extends RecyclerView.Adapter<DesafioPerfilAdapter.DesafioViewHolder> {

    private List<DesafioPerfil> desafios;

    public DesafioPerfilAdapter(List<DesafioPerfil> desafios) {
        this.desafios = desafios;
    }

    @NonNull
    @Override
    public DesafioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_profile_item, parent, false);
        return new DesafioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DesafioViewHolder holder, int position) {
        DesafioPerfil desafio = desafios.get(position);

        holder.tvTitle.setText(desafio.getTitulo());

        holder.pbDesafio.setProgress(desafio.getProgreso());
    }

    @Override
    public int getItemCount() {
        return desafios != null ? desafios.size() : 0;
    }

    public void actualizarLista(List<DesafioPerfil> nuevosDesafios) {
        this.desafios = nuevosDesafios;
        notifyDataSetChanged();
    }

    static class DesafioViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ProgressBar pbDesafio;

        public DesafioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNombreDesafio);
            pbDesafio = itemView.findViewById(R.id.pbDesafio);
        }
    }
}