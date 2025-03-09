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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

        // 🔹 Obtener usuario actual
        String userId = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0].replace(".", "");

        // 🔹 Referencia a desafíos en curso del usuario
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("usuarios").child(userId).child("desafios_en_curso");

        // 🔹 Convertir ID de experiencia a String para evitar problemas de conversión
        String experienciaIdString = String.valueOf(experiencia.getId());

        // 🔹 Verificar si la experiencia está en "exp_completadas"
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean experienciaCompletada = false;
                String desafioKey = null;

                for (DataSnapshot desafioSnapshot : snapshot.getChildren()) {
                    String desafioId = desafioSnapshot.getKey();

                    // 🔹 Verificamos si en este desafío la experiencia está completada
                    if (desafioSnapshot.child("exp_completadas").hasChild(experienciaIdString)) {
                        experienciaCompletada = true;
                        desafioKey = desafioId;
                        break;
                    }
                }

                // 🔹 Mostrar el check si la experiencia está completada
                holder.tvCheck.setVisibility(experienciaCompletada ? View.VISIBLE : View.GONE);

                // 🔹 Manejar el evento de clic en el icono de completado
                String finalDesafioKey = desafioKey;
                boolean finalExperienciaCompletada = experienciaCompletada;
                holder.ivCompletado.setOnClickListener(v -> {
                    if (finalDesafioKey != null) {
                        DatabaseReference experienciaRef = userRef.child(finalDesafioKey)
                                .child("exp_completadas").child(experienciaIdString);

                        if (finalExperienciaCompletada) {
                            // 🔹 Si estaba completada, la eliminamos
                            experienciaRef.removeValue()
                                    .addOnSuccessListener(aVoid -> holder.tvCheck.setVisibility(View.GONE))
                                    .addOnFailureListener(e -> Log.e("Firebase", "Error al eliminar experiencia", e));
                        } else {
                            // 🔹 Si no estaba completada, la agregamos
                            experienciaRef.setValue(true)
                                    .addOnSuccessListener(aVoid -> holder.tvCheck.setVisibility(View.VISIBLE))
                                    .addOnFailureListener(e -> Log.e("Firebase", "Error al marcar experiencia", e));
                        }
                    } else {
                        Log.e("Firebase", "No se encontró un desafío para la experiencia con ID: " + experiencia.getId());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al obtener las experiencias completadas", error.toException());
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

        ImageView imgExperiencia, tvMapLink, ivCompletado;
        TextView tvTitulo, tvDescripcion, tvCheck;

        public ExperienciaViewHolder(@NonNull View itemView) {
            super(itemView);

            imgExperiencia = itemView.findViewById(R.id.imgExperiencia);
            tvTitulo = itemView.findViewById(R.id.tvTitle);
            tvDescripcion = itemView.findViewById(R.id.tvDescription);
            tvMapLink = itemView.findViewById(R.id.tvMapLink);
            tvCheck = itemView.findViewById(R.id.tvCheck);
            ivCompletado = itemView.findViewById(R.id.ivCompletado);
        }
    }
}
