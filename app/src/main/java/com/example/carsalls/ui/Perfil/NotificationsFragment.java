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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class NotificationsFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private FirebaseUser authUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;

    private static final int COD_SEL_IMAGE = 300;

    StorageReference storageReference;
    String storage_path = "Perfil/*";

    private Uri image_url;
    String photo = "photo";
    String rute_storage_photo;
    String rute_storage_photoP;

    ProgressDialog progressDialog;

    //PRECARGAR DATOS Y FOTO

    private final DatabaseReference base = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference users = base.child("users");
    private final DatabaseReference uid = users.child(mAuth.getUid());
    private final DatabaseReference usuarioss = uid.child("usuarios");


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PerfilViewModel notificationsViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        usuarioss.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUser().observe(getViewLifecycleOwner(), (user) -> {
                authUser = user;

            binding.txtNombreUser.setText(authUser.getDisplayName());
            binding.txtCorreoElectronico2.setText(authUser.getEmail());


        });


        //INSTANCIAR VARIABLES FIREBASE
        progressDialog = new ProgressDialog(requireContext());
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


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
        rute_storage_photoP = photo + " " + mAuth.getUid() + "LA";
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
                                String download_uri = uri.toString();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("photo", download_uri);
                                mfirestore.collection("coche").document("LA").update(map);
                                Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                binding.imgPerfil2.setImageURI(image_url);

                                //RELLENAR DATOS DE USUARIO
                                Usuario usuario = new Usuario();

                                usuario.setNombre(mAuth.getCurrentUser().getDisplayName());
                                usuario.setCorreo(mAuth.getCurrentUser().getEmail());
                                usuario.setImagenPerfil(rute_storage_photoP);

                                DatabaseReference base = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference users = base.child("users");
                                DatabaseReference uid = users.child(authUser.getUid());
                                DatabaseReference usuarioss = uid.child("usuarios");

                                DatabaseReference reference = usuarioss.push();
                                reference.setValue(usuario);
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