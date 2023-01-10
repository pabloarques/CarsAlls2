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
    String idd;

    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PerfilViewModel notificationsViewModel =
                new ViewModelProvider(this).get(PerfilViewModel.class);

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUser().observe(getViewLifecycleOwner(), (user) -> {
                authUser = user;
        });

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

            }
        });
        

        return root;
    }

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
        String rute_storage_photo = storage_path + " " + photo + " " + mAuth.getUid() + "" + idd;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

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
                                mfirestore.collection("coche").document(idd).update(map);
                                Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
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

    private void getCoche(String id){
        mfirestore.collection("coche").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}