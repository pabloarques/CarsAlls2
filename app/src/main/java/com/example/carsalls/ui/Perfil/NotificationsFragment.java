package com.example.carsalls.ui.Perfil;

import static android.app.Activity.RESULT_OK;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.carsalls.MainActivity;
import com.example.carsalls.ViewModels.SharedViewModel;
import com.example.carsalls.databinding.FragmentPerfilBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class NotificationsFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private FirebaseUser authUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;
    private static final int COD_SEL_IMAGE = 300;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    String storage_path = "Perfil/*";


    private Uri image_url;
    String photo = "photo";
    String rute_storage_photo;
    ProgressDialog progressDialog;

    String download_uri;
    String downloadUrl;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PerfilViewModel notificationsViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);


        //INSTANCIAR VARIABLES FIREBASE
        progressDialog = new ProgressDialog(requireContext());
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        //SHAREDVIEWMODEL ES PARA PASAR EL USUARIO A ESTE FRAGMENTO, CUALQUIER CODIGO QUE
        //NECESITE EL AUTHUSER SE HACE DENTRO
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUser().observe(getViewLifecycleOwner(), (user) -> {
                authUser = user;

                binding.txtNombreUser.setText(authUser.getDisplayName());
                binding.txtCorreoElectronico2.setText(authUser.getEmail());

                //PEDIR LA FOTO A LA BASE
                databaseReference = FirebaseDatabase.getInstance().getReference();

        });

        //BOTONES
        binding.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               actualizarFoto();
            }
        });


        binding.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("photo", "");
                mfirestore.collection("coche").document("LA").update(map);
                Toast.makeText(requireContext(), "Foto eliminada", Toast.LENGTH_SHORT).show();

            }
        });

        binding.btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(requireContext(), "Ha cerrado sesion", Toast.LENGTH_SHORT);
                startActivity(new Intent(requireContext(), MainActivity.class));
            }
        });

        View root = binding.getRoot();
        return root;
    }//Oncreate



    private void actualizarFoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, COD_SEL_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        Log.d("image_url", "requestCode - RESULT_OK: " + requestCode + " " + RESULT_OK);
        if(resultCode == RESULT_OK){
            if(requestCode == COD_SEL_IMAGE){
                image_url = data.getData();
                subirFoto(image_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void subirFoto(Uri image_url) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        rute_storage_photo = storage_path + " " + photo + " " + mAuth.getUid() + "LA";

        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                    if (uriTask.isSuccessful()){
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                download_uri = uri.toString();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("photo", download_uri);
                                mfirestore.collection("coche").document("LA").update(map);
                                Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                //CONSEGUIR URL DE LA FOTO Y PASARLA AL USUARIO
                                reference.getDownloadUrl().addOnCompleteListener(task -> {
                                    Uri downloadUri = task.getResult();
                                    Glide.with(requireContext()).load(downloadUri).into(binding.imgPerfil2);
                                     downloadUrl = downloadUri.toString();

                                    //RELLENAR DATOS DE USUARIO
                                    Usuario usuario = new Usuario();
                                    usuario.setNombre(mAuth.getCurrentUser().getDisplayName());
                                    usuario.setCorreo(mAuth.getCurrentUser().getEmail());
                                    usuario.setImagenPerfil(downloadUrl);

                                    databaseReference = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference usuarioss = databaseReference.child("usuarios");

                                    DatabaseReference reference = usuarioss.child(authUser.getUid());
                                    reference.setValue(usuario);
                                });
                            }
                        });
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Error al cargar la foto", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}