package dam.pmdm.a101pipas.experiencias;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.models.Desafio;
import dam.pmdm.a101pipas.models.DesafioUsuario;
import dam.pmdm.a101pipas.models.Experiencia;
import dam.pmdm.a101pipas.models.User;

public class ExperienciasListAdapter extends RecyclerView.Adapter<ExperienciasListAdapter.ExperienciaViewHolder> {

    // Lista de experiencias y variables necesarias
    private List<Experiencia> experienciaList;
    private OnExperienciaClickListener listener;
    private List<String> experienciasCompletadas; // Para almacenar los IDs de las experiencias completadas
    private String tituloDesafio; // Almacena el título del desafío
    private Fragment fragment;

    // Interfaz para manejar clics en las experiencias

    public interface OnExperienciaClickListener {
        void onExperienciaClick(Experiencia experiencia);
    }

    public ExperienciasListAdapter(List<Experiencia> experienciaList, OnExperienciaClickListener listener, String tituloDesafio, Fragment fragment) {
        this.experienciaList = experienciaList;
        this.listener = listener;
        this.experienciasCompletadas = new ArrayList<>();
        this.tituloDesafio = tituloDesafio;
        this.fragment = fragment;
        cargarExperienciasCompletadas();
    }

    // Carga las experiencias completadas desde la base de datos
    private void cargarExperienciasCompletadas() {
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0].replace(".", "");
        DatabaseReference completadasRef = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(user).child("desafios").child(tituloDesafio)
                .child("experiencias_completadas");

        completadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingIds = dataSnapshot.getValue().toString();
                    if (existingIds != null) {
                        String[] ids = existingIds.split(", ");
                        for (String id : ids) {
                            experienciasCompletadas.add(id.trim()); // Agregar cada ID al conjunto
                        }
                    }
                }

                int totalExperiencias = experienciaList.size();
                int porcentajeCompletado = calcularPorcentajeCompletado(totalExperiencias);

                // Llamar a actualizarInsignia para cambiar el src de ivInsignias
                actualizarInsignia(porcentajeCompletado);

                notifyDataSetChanged();
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

        Log.d("LISTADO", "ID: " + experiencia.getId());
        Log.d("LISTADO", "EXPERIENCIAS: " + experienciasCompletadas);

        if(!experienciasCompletadas.isEmpty()){
            if(experienciasCompletadas.contains(String.valueOf(experiencia.getId()))){
                holder.tvCheck.setImageResource(R.drawable.checkcompleto);
            } else {
                holder.tvCheck.setImageResource(R.drawable.circulo);
            }
        }

        //TODO: Ver porque no se inicia con los botones pulsados al estar completada la experiencia
        //TODO: Ver porque las experiencias no se eliminan
        //TODO: Arreglar Ranking

        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0].replace(".", "");
        actualizarProgreso(user);

        holder.tvCheck.setOnClickListener(v -> {
            // Cambiar el estado de completada
            boolean nuevoEstado = !experienciasCompletadas.contains(String.valueOf(experiencia.getId()));
            if (nuevoEstado) {
                experienciasCompletadas.add(String.valueOf(experiencia.getId()));
            } else {
                experienciasCompletadas.remove(String.valueOf(experiencia.getId()));
            }

            // Cambiar la imagen del ícono
            holder.tvCheck.setImageResource(nuevoEstado ? R.drawable.checkcompleto : R.drawable.circulo);

            // Actualizar el estado en Firebase
            actualizarEstadoEnBaseDeDatos(String.valueOf(experiencia.getId()), nuevoEstado);
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

        if (completada) {
            agregarExperienciaACompletadas(experienciaId, user).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if(task.isSuccessful()){
                        actualizarProgreso(user);
                    }
                }
            });
        } else {
            eliminarExperienciaDeCompletadas(experienciaId, user).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if(task.isSuccessful()){
                        actualizarProgreso(user);
                    }
                }
            }); // Llamar al método para eliminar
        }
    }

    private int calcularPorcentajeCompletado(int totalExperiencias) {
        if (totalExperiencias <= 0) {
            return 0;
        }
        int completadas = experienciasCompletadas.size();
        return (int) ((completadas / (float) totalExperiencias) * 100);
    }

    private void actualizarInsignia(int porcentaje) {
        ImageView ivInsignias = fragment.getView().findViewById(R.id.ivInsignias);

        if (porcentaje >= 0 && porcentaje <= 10) {
            ivInsignias.setImageResource(R.drawable.feliz);
        } else if (porcentaje > 10 && porcentaje <= 20) {
            ivInsignias.setImageResource(R.drawable.piruleta);
        } else if (porcentaje > 20) {
            ivInsignias.setImageResource(R.drawable.estrella);
        }
    }

    private void actualizarProgreso(String user) {
        DatabaseReference refDef = FirebaseDatabase.getInstance().getReference("desafios");
        FirebaseDatabase.getInstance().getReference("usuarios").child(user).child("desafios").child(tituloDesafio).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DesafioUsuario desafioUsuario = new DesafioUsuario(
                            snapshot.child("estado").getValue(String.class),
                            snapshot.child("experiencias_completadas").getValue().toString()
                    );

                    refDef.child(tituloDesafio).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot desafioSnapshot) {
                            Desafio desafioGeneral = new Desafio(
                                    desafioSnapshot.child("titulo").getValue().toString(),
                                    desafioSnapshot.child("experiencias").getValue().toString()
                            );

                            if (desafioGeneral != null) {
                                int totalExperiencias = desafioGeneral.getExperienciasRequeridas().size();
                                int completadas = desafioUsuario.getExperienciasCompletadasList().size();
                                int porcentaje = (int) ((completadas / (float) totalExperiencias) * 100);

                                TextView tvProgreso = fragment.getView().findViewById(R.id.tvProgress);
                                ProgressBar progressBar = fragment.getView().findViewById(R.id.progressBar);

                                Log.d("LISTADO", "PROGRESO: " + totalExperiencias + " - " + completadas + " - " + porcentaje + " - " + desafioUsuario.getExperienciasCompletadasList());

                                tvProgreso.setText(String.format(fragment.getContext().getString(R.string.listado_experiencias_progreso), completadas, totalExperiencias));
                                progressBar.setProgress(porcentaje);

                                if(porcentaje == 100){
                                    desafioUsuario.setEstado(1);
                                } else {
                                    desafioUsuario.setEstado(0);
                                }

                                DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("usuarios").child(user);
                                refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int experiencias = 0;
                                        Map<String, DesafioUsuario> desafios = null;

                                        if(snapshot.hasChild("experiencias_completadas")){
                                            experiencias = Integer.parseInt(snapshot.child("experiencias_completadas").getValue().toString());
                                        }
                                        Object valor = snapshot.child("desafios").getValue();
                                        if(snapshot.hasChild("desafios")){
                                            desafios = (Map<String, DesafioUsuario>) valor;
                                        }

                                        User usuario = new User(
                                                snapshot.child("id").getValue().toString(),
                                                snapshot.child("username").getValue(String.class),
                                                snapshot.child("email").getValue(String.class),
                                                "",
                                                snapshot.child("foto_perfil").getValue(String.class),
                                                snapshot.child("grupos").getValue().toString(),
                                                snapshot.child("amigos").getValue().toString(),
                                                experiencias,
                                                desafios
                                        );

                                        desafios.put(tituloDesafio, desafioUsuario);
                                        usuario.setDesafios(desafios);

                                        refUsers.setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("LISTADO", "EXPERIENCIA COMPLETADA");
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });

                }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private Task<Boolean> agregarExperienciaACompletadas(String experienciaId, String user) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        DatabaseReference completadasRef = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(user)
                .child("desafios")
                .child(tituloDesafio)
                .child("experiencias_completadas");

        completadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String existingIds = "";
                if(dataSnapshot.exists()){
                    existingIds = dataSnapshot.getValue().toString();
                }

                // Comprobar y agregar el ID de experiencia
                if (!existingIds.contains(experienciaId)) {
                    existingIds += existingIds.isEmpty() ? experienciaId : ", " + experienciaId;
                }

                // Guardar la nueva cadena de IDs
                completadasRef.setValue(existingIds).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        taskCompletionSource.setResult(true);
                        Log.d("Firebase", "Experiencia añadida a experiencias_completadas correctamente");
                    } else {
                        taskCompletionSource.setResult(false);
                        Log.e("Firebase", "Error al añadir experiencia a experiencias_completadas");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setResult(false);
                Log.e("Firebase", "Error al leer la base de datos", databaseError.toException());
            }
        });
        return taskCompletionSource.getTask();
    }

    private Task<Boolean> eliminarExperienciaDeCompletadas(String experienciaId, String user) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        DatabaseReference completadasRef = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(user)
                .child("desafios")
                .child(tituloDesafio)
                .child("experiencias_completadas");

        completadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingIds = dataSnapshot.getValue().toString();
                    if (existingIds != null) {
                        String newIds = removerIdDeCadena(existingIds, experienciaId);
                        completadasRef.setValue(newIds).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                taskCompletionSource.setResult(true);
                                Log.d("Firebase", "Experiencia eliminada de experiencias_completadas correctamente");
                            } else {
                                taskCompletionSource.setResult(false);
                                Log.e("Firebase", "Error al eliminar experiencia de experiencias_completadas");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskCompletionSource.setResult(false);
                Log.e("Firebase", "Error al leer la base de datos", databaseError.toException());
            }
        });
        return taskCompletionSource.getTask();
    }

    // Método para remover un ID de la cadena
    private String removerIdDeCadena(String existingIds, String experienciaId) {
        String[] idsArray = existingIds.split(",");
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
        TextView tvTitulo, tvDescripcion, tvProgreso;
        ProgressBar progressBar;

        public ExperienciaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProgreso = itemView.findViewById(R.id.tvProgress);
            progressBar = itemView.findViewById(R.id.progressBar);
            imgExperiencia = itemView.findViewById(R.id.imgExperiencia);
            tvTitulo = itemView.findViewById(R.id.tvTitle);
            tvDescripcion = itemView.findViewById(R.id.tvDescription);
            tvMapLink = itemView.findViewById(R.id.tvMapLink);
            tvCheck = itemView.findViewById(R.id.tvCheck); // Asegúrate de que el ID en el XML sea correcto
        }
    }
}