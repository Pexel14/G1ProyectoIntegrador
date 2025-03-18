package dam.pmdm.a101pipas.experiencias;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Experiencia;

public class ExperienciasListAdapter extends RecyclerView.Adapter<ExperienciasListAdapter.ExperienciaViewHolder> {

    private List<Experiencia> experienciaList;
    private OnExperienciaClickListener listener;

    public interface OnExperienciaClickListener {
        void onExperienciaClick(Experiencia experiencia);
    }

    public ExperienciasListAdapter(List<Experiencia> experienciaList, OnExperienciaClickListener listener) {
        this.experienciaList = experienciaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExperienciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_experiencias, parent, false);
        return new ExperienciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienciaViewHolder holder, int position) {
        Experiencia experiencia = experienciaList.get(position);

        // Cargar imagen usando Picasso
        Picasso.get()
                .load(experiencia.getImagen())
                .placeholder(R.drawable.experiencia)
                .error(R.drawable.experiencia)
                .into(holder.imgExperiencia);

        holder.tvTitulo.setText(experiencia.getTitulo());
        holder.tvDescripcion.setText(experiencia.getDescripcion());

        // Configurar enlace del mapa
        holder.tvMapLink.setOnClickListener(view -> {
            if (listener != null) {
                listener.onExperienciaClick(experiencia);
            }
        });
    }

    public void setExperiencias(List<Experiencia> experiencias) {
        this.experienciaList = experiencias;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return experienciaList.size();
    }

    static class ExperienciaViewHolder extends RecyclerView.ViewHolder {

        ImageView imgExperiencia, tvMapLink;
        TextView tvTitulo, tvDescripcion;

        public ExperienciaViewHolder(@NonNull View itemView) {
            super(itemView);

            imgExperiencia = itemView.findViewById(R.id.imgExperiencia);
            tvTitulo = itemView.findViewById(R.id.tvTitle);
            tvDescripcion = itemView.findViewById(R.id.tvDescription);
            tvMapLink = itemView.findViewById(R.id.tvMapLink);
        }
    }
}
