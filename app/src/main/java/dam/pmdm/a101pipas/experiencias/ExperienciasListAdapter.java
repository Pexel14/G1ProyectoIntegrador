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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Experiencia;

public class ExperienciasListAdapter extends RecyclerView.Adapter<ExperienciasListAdapter.ExperienciaViewHolder> {

    // Lista de experiencias y variables necesarias
    private List<Experiencia> experienciaList;
    private OnExperienciaClickListener listener;
    private Set<String> experienciasCompletadas; // Para almacenar los IDs de las experiencias completadas
    private String tituloDesafio; // Almacena el título del desafío

    // Interfaz para manejar clics en las experiencias
    public interface OnExperienciaClickListener {
        void onExperienciaClick(Experiencia experiencia);
    }

    public ExperienciasListAdapter(List<Experiencia> experienciaList, OnExperienciaClickListener listener, String tituloDesafio) {
        this.experienciaList = experienciaList;
        this.listener = listener;
        this.tituloDesafio = tituloDesafio; // Asignar el título del desafío
        this.experienciasCompletadas = new HashSet<>(); // Inicializar el conjunto
        cargarExperienciasCompletadas(); // Cargar los IDs de experiencias completadas
    }

    // Carga las experiencias completadas desde la base de datos
    private void cargarExperienciasCompletadas() {
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0].replace(".", "");
        DatabaseReference completadasRef = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(user)
                .child("experiencias_completadas");

        completadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingIds = dataSnapshot.getValue(String.class);
                    if (existingIds != null) {
                        String[] ids = existingIds.split(", ");
                        for (String id : ids) {
                            experienciasCompletadas.add(id.trim()); // Agregar cada ID al conjunto
                        }
                    }
                }
                notifyDataSetChanged(); // Notificar cambios en los datos
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error al leer la base de datos", databaseError.toException());
            }
        });
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

        // Cambiar el ícono basado en el estado de completada
        holder.tvCheck.setImageResource(experienciasCompletadas.contains(experiencia.getId()) ? R.drawable.checkcompleto : R.drawable.circulo);

        holder.tvCheck.setOnClickListener(v -> {
            // Cambiar el estado de completada
            boolean nuevoEstado = !experienciasCompletadas.contains(experiencia.getId());
            if (nuevoEstado) {
                experienciasCompletadas.add(experiencia.getId());
            } else {
                experienciasCompletadas.remove(experiencia.getId());
            }

            // Cambiar la imagen del ícono
            holder.tvCheck.setImageResource(nuevoEstado ? R.drawable.checkcompleto : R.drawable.circulo);

            // Actualizar el estado en Firebase
            actualizarEstadoEnBaseDeDatos(experiencia.getId(), nuevoEstado);
        });

        // Configurar el enlace del mapa
        holder.tvMapLink.setOnClickListener(view -> {
            if (listener != null) {
                listener.onExperienciaClick(experiencia);
            }
        });
    }

    private void actualizarEstadoEnBaseDeDatos(String experienciaId, boolean completada) {
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0].replace(".", "");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(user)
                .child("experiencias")
                .child(experienciaId)
                .child("completada");

        userRef.setValue(completada).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Estado de experiencia actualizado correctamente");
                if (completada) {
                    agregarExperienciaACompletadas(experienciaId, user);
                } else {
                    eliminarExperienciaDeCompletadas(experienciaId, user); // Llamar al método para eliminar
                }
            } else {
                Log.e("Firebase", "Error al actualizar el estado de la experiencia");
            }
        });
    }

    private void agregarExperienciaACompletadas(String experienciaId, String userId) {
        DatabaseReference completadasRef = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(userId)
                .child("experiencias_completadas");

        completadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String existingIds = dataSnapshot.exists() ? dataSnapshot.getValue(String.class) : "";

                // Comprobar y agregar el ID de experiencia
                if (!existingIds.contains(experienciaId)) {
                    existingIds = existingIds.isEmpty() ? experienciaId : existingIds + ", " + experienciaId;
                }

                // Guardar la nueva cadena de IDs
                completadasRef.setValue(existingIds).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Experiencia añadida a experiencias_completadas correctamente");
                    } else {
                        Log.e("Firebase", "Error al añadir experiencia a experiencias_completadas");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error al leer la base de datos", databaseError.toException());
            }
        });
    }

    private void eliminarExperienciaDeCompletadas(String experienciaId, String userId) {
        DatabaseReference completadasRef = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(userId)
                .child("experiencias_completadas");

        completadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingIds = dataSnapshot.getValue(String.class);
                    if (existingIds != null) {
                        String newIds = removerIdDeCadena(existingIds, experienciaId);
                        completadasRef.setValue(newIds).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Firebase", "Experiencia eliminada de experiencias_completadas correctamente");
                            } else {
                                Log.e("Firebase", "Error al eliminar experiencia de experiencias_completadas");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error al leer la base de datos", databaseError.toException());
            }
        });
    }

    // Método para remover un ID de la cadena
    private String removerIdDeCadena(String existingIds, String experienciaId) {
        String[] idsArray = existingIds.split(", ");
        StringBuilder newIds = new StringBuilder();

        for (String id : idsArray) {
            if (!id.trim().equals(experienciaId)) {
                if (newIds.length() > 0) {
                    newIds.append(", "); // Añadir coma entre IDs
                }
                newIds.append(id.trim());
            }
        }
        return newIds.toString();
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
        ImageView imgExperiencia, tvMapLink, tvCheck; // Modificado para incluir tvCheck
        TextView tvTitulo, tvDescripcion;

        public ExperienciaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgExperiencia = itemView.findViewById(R.id.imgExperiencia);
            tvTitulo = itemView.findViewById(R.id.tvTitle);
            tvDescripcion = itemView.findViewById(R.id.tvDescription);
            tvMapLink = itemView.findViewById(R.id.tvMapLink);
            tvCheck = itemView.findViewById(R.id.tvCheck); // Asegúrate de que el ID en el XML sea correcto
        }
    }
}