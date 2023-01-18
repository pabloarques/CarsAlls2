package com.example.carsalls.ui.home;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.carsalls.R;
import com.example.carsalls.ViewModels.SharedViewModel;
import com.example.carsalls.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseUser authUser;
    private FirebaseAuth mAuth;
    private Uri uri;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.imgPerfil.setImageResource(R.drawable.kkabarth);

        binding.imgbCoches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               
            }
        });


        //SHAREDVIEWMODEL ES PARA PASAR EL USUARIO A ESTE FRAGMENTO, CUALQUIER CODIGO QUE
        //NECESITE EL AUTHUSER SE HACE DENTRO
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUser().observe(getViewLifecycleOwner(), (user) -> {
            authUser = user;

            //binding.txtUser.setText(authUser.getDisplayName());

        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}