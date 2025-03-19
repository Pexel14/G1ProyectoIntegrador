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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.a101pipas.MainActivity;
import dam.pmdm.a101pipas.R;
import dam.pmdm.a101pipas.databinding.FragmentCrearGrupoBinding;
import dam.pmdm.a101pipas.desafios.descubrir.TarjetaDesafioDescubrirFragment;
import dam.pmdm.a101pipas.models.Desafio;
import dam.pmdm.a101pipas.models.Grupo;

public class CrearGrupoFragment extends Fragment {

    private FirebaseAuth mAuth;
    private String miembros, miembrosId;
    private String amigo;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
    private FragmentCrearGrupoBinding binding;
    private CrearGrupoViewModel viewModel;
    private DatabaseReference refDesafios = FirebaseDatabase.getInstance().getReference("desafios");
    private DatabaseReference refGrupos = FirebaseDatabase.getInstance().getReference("grupos");
    private String idUltimo = "0";
    private String idDesafio = "0";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCrearGrupoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CrearGrupoViewModel.class);

        viewModel.getDesafiosLiveData().observe(getViewLifecycleOwner(), desafios -> {
            Log.d("CrearGrupoFragment", "Se han obtenido los desafíos del usuario");
            for (TarjetaDesafioDescubrirFragment desafio : desafios) {
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.contenedorFragmentsCrearGrupo, desafio)
                        .commit();
            }
            Log.d("CrearGrupoFragment", "Desafíos añadios al contenedor");
        });

        viewModel.cargarFragments();

        mAuth = FirebaseAuth.getInstance();

        miembrosId = "";
        miembros = "";
        aniadirIdUsuario(""); // Si no le pasamos nada, obtiene el usuario actual
//        miembros = aniadirUsuarioActual(miembros);
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

                Log.d("CrearGrupoFragment", "Se pulsa el botón 'Crear Grupo'");

                Grupo grupo = new Grupo();

                String nombre = binding.etNombreGrupoCrearGrupo.getText().toString();
                grupo.setTitulo(nombre);

                final String[] key = {""};
                obtenerUltimaKey().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {

                            Log.d("CrearGrupoFragment", "Se obtiene la última key de 'Grupos'");

                            key[0] = String.valueOf(Integer.parseInt(idUltimo) + 1);
                            Log.d("CrearGrupoFragment", "id obtenida (debería ser 1) : " + String.valueOf(key[0]));
                            grupo.setId(Integer.parseInt(key[0]));

                            if (binding.swHacerPrivadoCrearGrupo.isChecked()) {
                                String contrasenia = binding.etContraseniaCrearGrupo.getText().toString();
                                grupo.setContrasena(contrasenia);
                            }

                            if (soloUnFragmentSeleccionado()) {

                                Log.d("CrearGrupoFragment", "Se sabe que sólo hay un fragmento seleccionado");

                                obtenerIdDesafio().addOnCompleteListener(new OnCompleteListener<String>() {

                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {

                                        Log.d("CrearGrupoFragment", "Se obtiene el id del desafío");

                                        grupo.setDesafio(Integer.parseInt(idDesafio));

                                        // Si no se han añadido amigos
                                        if (!miembrosId.contains(",")) {
                                            Toast.makeText(getContext(), "Debes añadir al menos un amigo", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            grupo.setMiembros(miembrosId);
                                            grupo.setFoto_grupo("");

                                            viewModel.getCrearGrupoLiveData().observe(getViewLifecycleOwner(), grupoCreado -> {
                                                if (grupoCreado) {

                                                    Log.d("CrearGrupoFragment", "Se crea el grupo");

                                                    Toast.makeText(getContext(), "Grupo creado con éxito", Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(getContext(), MainActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Toast.makeText(getContext(), "Error al crear el grupo", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            Log.d("CrearGrupoFragment", "Se va a crear el grupo");
                                            Log.d("CrearGrupoFragment", "ID: " + grupo.getId());
                                            Log.d("CrearGrupoFragment", "Titulo: " + grupo.getTitulo());
                                            Log.d("CrearGrupoFragment", "Miembros: " + grupo.getMiembros());

                                            viewModel.crearGrupo(key[0], grupo, imageUri);
                                        }

                                    }
                                });

                            } else {
                                Toast.makeText(getContext(), "Seleccione exactamente un desafío", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });



            }

        });

        binding.btnAniadirAmigosCrearGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ami = binding.etAmigoCrearGrupo.getText().toString();
                boolean yaEstaIncluido = false;
                String[] listaMiembros = miembros.split(",");

                for (int i=0; i<listaMiembros.length; i++) {
                    if (listaMiembros[i].equals(ami)) {
                        yaEstaIncluido = true;
                    }
                }

                if (ami.isEmpty()) {
                    Toast.makeText(getContext(), "Introduce un amigo", Toast.LENGTH_SHORT).show();
                } else if (yaEstaIncluido) {
                    Toast.makeText(getContext(), ami + " ya está incluido", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.getUsuarioExisteLiveData().removeObservers(getViewLifecycleOwner());
                    viewModel.getUsuarioExisteLiveData().observe(getViewLifecycleOwner(), existe -> {
                        if (existe != null) {
                            if (existe) {
                                miembros += "," + ami;
                                String miembrosFormateado = miembros.replace(",", "\n");
                                binding.tvListaAmigosCrearGrupo.setText(miembrosFormateado);
                                binding.etAmigoCrearGrupo.setText("");
                                aniadirIdUsuario(ami);
                            } else {
                                Toast.makeText(getContext(), "El usuario '" + ami + "' no existe", Toast.LENGTH_SHORT).show();
                            }

                            viewModel.limpiarUsuarioExisteLiveData();

                        }
                    });

                    viewModel.getUsuarioExiste(ami);
                    Log.d("CrearGrupoFragment", "Al viewModel se le pasa: " + ami);
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

    private Task<String> obtenerIdDesafio() {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        String nomDesafio = "";

        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof TarjetaDesafioDescubrirFragment) {
                if (((TarjetaDesafioDescubrirFragment) fragment).isSeleccionado()) {
                    TarjetaDesafioDescubrirFragment fra = (TarjetaDesafioDescubrirFragment) fragment;
                    nomDesafio = fra.getArguments().getString("titulo");
                }
            }
        }

        String finalNomDesafio = nomDesafio;
        refDesafios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idDesafioAux = "";
                for (DataSnapshot dsDesafios : snapshot.getChildren()) {
                    Desafio desafio = new Desafio(
                            dsDesafios.child("titulo").getValue(String.class),
                            dsDesafios.child("ciudad").getValue(String.class),
                            dsDesafios.child("descripcion").getValue(String.class),
                            dsDesafios.child("etiquetas").getValue(String.class),
                            dsDesafios.child("id").getValue().toString(),
                            ""

                    );
                    if (desafio.getTitulo().equals(finalNomDesafio)) {
                        idDesafioAux = String.valueOf(desafio.getId());
                        idDesafio = idDesafioAux;
                    }
                }

                taskCompletionSource.setResult(idDesafioAux);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setResult("Error");
            }
        });

        return taskCompletionSource.getTask();

    }

    private Task<String> obtenerUltimaKey() {
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        refGrupos.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idAux = "";
                for (DataSnapshot usuarios : snapshot.getChildren()) {

                    if (usuarios.child("id").exists()) {

                        idAux = usuarios.child("id").getValue().toString();

                        if (Integer.parseInt(idAux) > Integer.parseInt(idUltimo)) {
                            idUltimo = idAux;
                        }

                    }
                }
                taskCompletionSource.setResult(idAux);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                taskCompletionSource.setResult("Error");
            }
        });

        return taskCompletionSource.getTask();

    }

    private void aniadirIdUsuario(String usuario) {

        if (usuario.isEmpty()) {
            usuario = mAuth.getCurrentUser().getEmail();
            Log.d("CrearGrupoFragment", "Valor 1 de 'usuario': " + usuario);
            if (usuario != null) {
                usuario = usuario.split("@")[0].replace(".", "");
                Log.d("CrearGrupoFragment", "Valor 2 de 'usuario': " + usuario);
            }
        }

        if (!miembrosId.isEmpty()) {
            miembrosId += ",";
        }

        miembrosId += usuario;

        Log.d("CrearGrupoFragment", "Valor de 'miembrosId': " + miembrosId);

//        viewModel.getIdUsuarioLiveData().observe(getViewLifecycleOwner(), id -> {
//            if (!miembrosId.isEmpty()) {
//                miembrosId += ",";
//            }
//
//            miembrosId += id;
//            Log.d("CrearGrupoFragment", "Valor de miembros: " + miembrosId);
//
//        });
//
//        viewModel.limpiarIdUsuarioLiveData();
//
//        viewModel.getIdUsuario(usuario);

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

}