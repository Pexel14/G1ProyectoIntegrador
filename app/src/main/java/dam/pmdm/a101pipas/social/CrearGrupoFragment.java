package dam.pmdm.a101pipas.social;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dam.pmdm.a101pipas.MainActivity;
import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentCrearGrupoBinding;
import dam.pmdm.a101pipas.desafios.descubrir.TarjetaDesafioDescubrirFragment;
import dam.pmdm.a101pipas.models.Grupo;
import dam.pmdm.a101pipas.models.User;

public class CrearGrupoFragment extends Fragment {

    private DatabaseReference refDesafios, refGrupos, refUsuarios;
    private FirebaseDatabase firebase;
    private ValueEventListener listener;
//    private Switch swHacerPrivadoCrearGrupo;
//    private TextView tvContraseniaCrearGrupo;
//    private EditText etNombreGrupoCrearGrupo, etContraseniaCrearGrupo;
//    private Button btnCrearGrupo, btnAniadirAmigosCrearGrupo, btnAtras, btnSeleccionarImagen;
    private FirebaseAuth mAuth;
    private LinearLayout llAmigosCrearGrupo;
//    private HorizontalScrollView hsvElegirDesafioCrearGrupo;
    private String nombres_miembros, amigo;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
//    private ImageView ivImagenIconoCrearGrupo;
    private FragmentCrearGrupoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCrearGrupoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        firebase = FirebaseDatabase.getInstance();
        refDesafios = firebase.getReference("desafios");
        refGrupos = firebase.getReference("grupos");
        refUsuarios = firebase.getReference("usuarios");

        limpiarFragments();
        cargarFragments();

        mAuth = FirebaseAuth.getInstance();

        nombres_miembros = aniadirUsuarioActual(nombres_miembros);
        amigo = "";

        binding.swHacerPrivadoCrearGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.swHacerPrivadoCrearGrupo.isChecked()) {
                    binding.tvContraseniaCrearGrupo.setVisibility(View.VISIBLE);
                    binding.etContraseniaCrearGrupo.setVisibility(View.VISIBLE);
                } else {
                    binding.tvContraseniaCrearGrupo.setVisibility(View.GONE);
                    binding.etContraseniaCrearGrupo.setVisibility(View.GONE);
                }
            }
        });

        binding.btnCrearGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Si hay algún editText, comprobar que el usuario exista
//                if (llAmigosCrearGrupo.getChildCount() != 0) {
//
//                    EditText etAmigo = (EditText) llAmigosCrearGrupo.getChildAt(llAmigosCrearGrupo.getChildCount()-1);
//                    amigo = etAmigo.getText().toString();
//
//                    // Si el campo está vacío
//                    if (amigo.isEmpty()) {
//                        Toast.makeText(getContext(), R.string.crear_grupo_fragmen_ultimo_campo_amigo_vacio, Toast.LENGTH_SHORT).show();
//                    }
//
//                    // Sino
//                    else {
//                        usuarioExiste(amigo, new OnUsuarioExisteListener() {
//                            @Override
//                            public void onResultado(boolean existe) {
//                                // Si el usuario existe
//                                if (existe) {
//                                    miembros += "," + amigo;
//                                    Log.d("CREAR_GRUPO", miembros);
//                                }
//                                // Sino
//                                else {
//                                    Toast.makeText(getContext(), "El usuario '" + amigo + "' no existe", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                }

                // Si los datos están incompletos
//                if (datosIncompletos()) {
//                    Toast.makeText(getContext(), R.string.crear_grupo_fragment_rellene_todos_los_campos, Toast.LENGTH_SHORT).show();
//                }

                // Si hay amigos introducidos, validar el último amigo (los anteriores ya han sido validados)
                /*else*/ if (llAmigosCrearGrupo.getChildCount() != 0) {

                    EditText etAmigo = (EditText) llAmigosCrearGrupo.getChildAt(llAmigosCrearGrupo.getChildCount()-1);
                    amigo = etAmigo.getText().toString();

                    // Si el campo está vacío
                    if (amigo.isEmpty()) {
                        Toast.makeText(getContext(), R.string.crear_grupo_fragmen_ultimo_campo_amigo_vacio, Toast.LENGTH_SHORT).show();
                    }

                    // Sino
                    else {
                        usuarioExiste(amigo, new OnUsuarioExisteListener() {
                            @Override
                            public void onResultado(boolean existe) {
                                // Si el usuario existe
                                if (existe) {
                                    nombres_miembros += "," + amigo;
                                    Log.d("CREAR_GRUPO", nombres_miembros);
                                }
                                // Sino
                                else {
                                    Toast.makeText(getContext(), "El usuario '" + amigo + "' no existe", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                // Si los datos están completos y los amigos están validados
                else {
                    crearGrupo();
                }
            }
        });

        binding.btnAniadirAmigosCrearGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Si hay algún editText, comprobar que el usuario exista
                if (llAmigosCrearGrupo.getChildCount() != 0) {

                    EditText etAmigo = (EditText) llAmigosCrearGrupo.getChildAt(llAmigosCrearGrupo.getChildCount()-1);
                    amigo = etAmigo.getText().toString();

                    // Si el campo está vacío
                    if (amigo.isEmpty()) {
                        Toast.makeText(getContext(), "El último campo de amigo está vacío", Toast.LENGTH_SHORT).show();
                    }

                    // Sino
                    else {
                        usuarioExiste(amigo, new OnUsuarioExisteListener() {
                            @Override
                            public void onResultado(boolean existe) {
                                // Si el usuario existe
                                if (existe) {
                                    agregarEditText();
                                }
                                // Sino
                                else {
                                    Toast.makeText(getContext(), "El usuario '" + amigo + "' no existe", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                // Si no hay nada, crea un editText directamente
                else {
                    agregarEditText();
                }

            }
        });

        binding.btnAtrasRecuperarContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_social);
            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        Picasso.get().load(imageUri).into(binding.ivImagenIconoCrearGrupo);
                    }
                }
        );

        binding.btnSeleccionarImagen.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setPackage("com.google.android.apps.photos");
            imagePickerLauncher.launch(i);
        });


    }

    public interface OnUsuarioExisteListener {
        void onResultado(boolean existe);
    }

    public void usuarioExiste(String amigo, final OnUsuarioExisteListener listener) {
        refUsuarios.child(amigo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onResultado(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al comunicarse con la BBDD", Toast.LENGTH_SHORT).show();
                listener.onResultado(false); // En caso de error, asumimos que no existe
            }
        });
    }

    private void agregarEditText() {

        EditText nuevoEditText = new EditText(getContext());

        // Convertir dp a píxeles
        int marginTopInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()
        );
        int marginHorPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()
        );

        // Crear LayoutParams con MATCH_PARENT y WRAP_CONTENT
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Establecer el margen superior
        params.setMargins(marginHorPx, marginTopInPx, marginHorPx, 0);

        // Aplicar los LayoutParams al EditText
        nuevoEditText.setLayoutParams(params);

        // Establecer fondo personalizado
        nuevoEditText.setBackground(getContext().getResources().getDrawable(R.drawable.edit_text_border));

        // Agregar el EditText al LinearLayout
        llAmigosCrearGrupo.addView(nuevoEditText);

    }


    private void crearGrupo() {

        int creador = obtenerIDCreador();

        String desafio = "";
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioDescubrirFragment) {
                if(((TarjetaDesafioDescubrirFragment) fragment).isSeleccionado()) {
                    desafio = ((TarjetaDesafioDescubrirFragment) fragment).getKey();
                }
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha_creacion = sdf.format(Calendar.getInstance().getTime());

        String foto_grupo = null; // De momento no puedes seleccionar una imagen

        final String[] miembros = {obtenerIDMiembros(nombres_miembros)};

        String titulo = binding.etNombreGrupoCrearGrupo.getText().toString();

        Grupo grupo = new Grupo(creador, desafio, fecha_creacion, foto_grupo, miembros[0], titulo);

        if (binding.swHacerPrivadoCrearGrupo.isChecked()) {
            String contrasenia = binding.etContraseniaCrearGrupo.getText().toString();
            grupo.setContraseña(contrasenia);
        }

        String nombre = binding.etNombreGrupoCrearGrupo.getText().toString();
        String key = refGrupos.push().getKey(); // Se supone que genera la nueva key

        usuarioExiste(amigo, new OnUsuarioExisteListener() {
            @Override
            public void onResultado(boolean existe) {
                // Si el usuario existe
                if (existe) {
                    miembros[0] += obtenerIDMiembros(amigo);
                    grupoYaExiste(nombre, new GrupoExistenteCallback() {
                        @Override
                        public void onResult(boolean existe) {
                            if (existe) {
                                Toast.makeText(getContext(), "El nombre '" + nombre + "' ya está cogido", Toast.LENGTH_SHORT).show();
                            } else {
                                if (soloUnFragmentSeleccionado()) {
                                    // Si no se han añadido amigos
                                    if (!miembros[0].contains(",")) {
                                        Toast.makeText(getContext(), "Debes añadir al menos un amigo", Toast.LENGTH_SHORT).show();
                                    } else {
                                        grupo.setMiembros(miembros[0]);
                                        refGrupos.child(key).setValue(grupo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent i = new Intent(getContext(), MainActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Toast.makeText(getContext(), "Error al crear el grupo", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Seleccione exactamente un desafío", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                // Sino
                else {
                    Toast.makeText(getContext(), "El usuario '" + amigo + "' no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private String obtenerIDMiembros(String nombresMiembros) {

        String[] aNombresMiembros = nombresMiembros.split(",");

        final String[] miembros = {""};

        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot usuario : snapshot.getChildren()) {
                        User u = usuario.getValue(User.class);
                        for (int i=0; i<aNombresMiembros.length; i++) {
                            if (aNombresMiembros[i].equals(u.getUsername())) {
                                miembros[0] += u.getid() + ",";
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (miembros[0].equals(",")) {
            miembros[0] = miembros[0].substring(0, miembros[0].length() - 1);
        }

        return miembros[0];
    }

    private int obtenerIDCreador() {
        String sId = mAuth.getCurrentUser().getEmail().split("@")[0].replace(".", "");
        final int[] id = {-1};

        refUsuarios.child(sId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    id[0] = Integer.parseInt(snapshot.getValue(User.class).getid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return id[0];

    }

    private String aniadirUsuarioActual(String miembros) {

        // Si está vacío se inicializa como una cadena vacía
        if (miembros == null || miembros.isEmpty()) {
            miembros = "";
        }

        // Añadir el usuario actual a 'miembros'
        if (mAuth.getCurrentUser() != null) {
            String usuario = mAuth.getCurrentUser().getEmail();
            if (usuario != null) {
                String id = usuario.split("@")[0].replace(".", "");
                if (!miembros.isEmpty()) {
                    miembros += ",";
                }
                miembros += id;
            }
        }
        return miembros;
    }

    private boolean soloUnFragmentSeleccionado() {
        int cant = 0;

        List<TarjetaDesafioDescubrirFragment> fragments = new ArrayList<>();

        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioDescubrirFragment) {
                fragments.add((TarjetaDesafioDescubrirFragment) fragment);
            }
        }
        
        for (TarjetaDesafioDescubrirFragment fragment : fragments) {
            if (fragment.isSeleccionado()) {
                cant += 1;
            }
        }


        if (cant == 1) {
            return true;
        } else {
            return false;
        }
    }

    private void grupoYaExiste(String nombre, final GrupoExistenteCallback callback) {
        refGrupos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dsGrupo : snapshot.getChildren()) {
                    Grupo grupo = dsGrupo.getValue(Grupo.class);
                    if (grupo.getTitulo().equals(nombre)) {
                        callback.onResult(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al leer los datos", Toast.LENGTH_SHORT).show();
                callback.onResult(false); // En caso de error, consideramos que no existe
            }
        });
    }

    public interface GrupoExistenteCallback {
        void onResult(boolean existe);
    }

//    private boolean datosIncompletos() {
//
//        if (soloUnFragmentSeleccionado() &&
//                binding.etNombreGrupoCrearGrupo.getText().toString().isEmpty() &&
//                binding.ivImagenIconoCrearGrupo.getDrawable() != null)  {
//
//        }
//
////        // Si es público
////        if (!binding.swHacerPrivadoCrearGrupo.isChecked()) {
////            if (binding.etNombreGrupoCrearGrupo.getText().toString().isEmpty()) {
////                return true;
////            } else {
////                return false;
////            }
////        }
////
////        // Si es privado
////        else {
////            if (!etNombreGrupoCrearGrupo.getText().toString().isEmpty() &&
////                !etContraseniaCrearGrupo.getText().toString().isEmpty()) {
////                return false;
////            } else {
////                return true;
////            }
////        }
//
//    }

    private void cargarFragments() {
        limpiarFragments();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                limpiarFragments();

                if (snapshot.exists()) {
                    for (DataSnapshot grupo : snapshot.getChildren()) {

                        String key = grupo.getKey().toString();
                        String titulo = grupo.child("titulo").getValue(String.class);
                        String ciudad = grupo.child("ciudad").getValue(String.class);

                        Log.d("FirebaseData", "Key: " + key + ", Titulo: " + titulo + ", Ciudad: " + ciudad);

                        TarjetaDesafioDescubrirFragment fragment = TarjetaDesafioDescubrirFragment.newInstance(titulo, ciudad, key);

                        getChildFragmentManager()
                                .beginTransaction()
                                .add(R.id.contenedorFragmentsCrearGrupo, fragment)
                                .commit();

                    }
                } else {
                    Toast.makeText(getContext(), "No existen desafíos", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", getString(R.string.descubrir_fragment_error_consulta) + error.getMessage());
            }
        };

        refDesafios.addValueEventListener(listener);
    }

    private void limpiarFragments() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioDescubrirFragment) {
                getChildFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
    }

}