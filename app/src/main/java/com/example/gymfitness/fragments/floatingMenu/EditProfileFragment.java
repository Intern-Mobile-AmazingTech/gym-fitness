package com.example.gymfitness.fragments.floatingMenu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gymfitness.R;
import com.example.gymfitness.data.DAO.UserInformationDAO;
import com.example.gymfitness.data.database.FitnessDB;
import com.example.gymfitness.data.entities.UserInformation;
import com.example.gymfitness.databinding.FragmentEditprofileBinding;
import com.example.gymfitness.viewmodels.EditProfileViewModel;
import com.example.gymfitness.viewmodels.ProfileViewModel;
import com.example.gymfitness.viewmodels.SharedViewModel;
import com.example.gymfitness.viewmodelsfactory.EditProfileViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditProfileFragment extends Fragment {

    FragmentEditprofileBinding binding;
    EditProfileViewModel editProfileViewModel;
    SharedViewModel sharedViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_editprofile, container, false);

        UserInformationDAO userInformationDAO = FitnessDB.getInstance(getContext()).userInformationDAO();
        EditProfileViewModelFactory factory = new EditProfileViewModelFactory(userInformationDAO);
        editProfileViewModel = new ViewModelProvider(this, factory).get(EditProfileViewModel.class);
        editProfileViewModel.loadUserInformation();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        SimpleDateFormat dob = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        editProfileViewModel.getUserInformation().observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation userInfo) {
                if (userInfo != null) {
                    if(userInfo.getDob() != null){
                        String dateOfBirth = dob.format(userInfo.getDob());
                        binding.tvDateOfBirth.setText(dateOfBirth);
                        binding.edtDateOfBirth.setText(dateOfBirth);
                    }

                    int imageResId;
                    if ("male".equalsIgnoreCase(userInfo.getGender())) {
                        imageResId = R.drawable.man_profile;
                    } else if ("female".equalsIgnoreCase(userInfo.getGender())) {
                        imageResId = R.drawable.wonman_profile;
                    } else {
                        imageResId = R.drawable.account_image;
                    }

                    Glide.with(requireContext())
                            .load(userInfo.getImg() != null ? userInfo.getImg() : imageResId)
                            .placeholder(R.drawable.bgr_onboarding_2d)
                            .error(R.drawable.arrow_next)
                            .into(binding.imgProfile);

                    binding.tvNameProfile.setText(userInfo.getFullname());
                    binding.tvEmail.setText(userInfo.getEmail());
                    binding.tvWeight.setText(userInfo.getWeight() + " KG");
                    binding.tvOld.setText(String.valueOf(userInfo.getAge()));
                    binding.tvHeigh.setText(userInfo.getHeight() + " CM");
                    binding.editFullname.setText(userInfo.getFullname());
                    binding.edtEmail.setText(userInfo.getEmail());
                    binding.edtPhoneNumber.setText(userInfo.getPhonenumber());
                    binding.edtWeight.setText(String.valueOf(userInfo.getWeight()));
                    binding.edtHeigh.setText(String.valueOf(userInfo.getHeight()));
                }
            }
        });

        binding.btnUpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInformation updatedInfo = editProfileViewModel.getUserInformation().getValue();
                if (updatedInfo != null) {
                    String fullName = binding.editFullname.getText().toString();
                    if (!TextUtils.isEmpty(fullName)) {
                        updatedInfo.setFullname(fullName);
                    } else {
                        updatedInfo.setFullname("");
                    }

                    String email = binding.edtEmail.getText().toString();
                    if (!TextUtils.isEmpty(email)) {
                        updatedInfo.setEmail(email);
                    } else {
                        updatedInfo.setEmail("");
                    }

                    String phoneNumber = binding.edtPhoneNumber.getText().toString();
                    if (!TextUtils.isEmpty(phoneNumber)) {
                        updatedInfo.setPhonenumber(phoneNumber);
                    } else {
                        updatedInfo.setPhonenumber("");
                    }

                    Date dateOfBirth = null;
                    try {
                        dateOfBirth = dob.parse(binding.edtDateOfBirth.getText().toString());
                        updatedInfo.setDob(dateOfBirth);
                        Log.e("Ngan ne", dateOfBirth.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Lỗi nè", e.toString());
                    }

                    int enteredAge = updatedInfo.getAge();
                    int calculatedAge = calculateAgeFromDob(dateOfBirth);
                    if (enteredAge != calculatedAge && dateOfBirth != null) {
                        updatedInfo.setAge(calculatedAge);
                    } else {
                        updatedInfo.setAge(enteredAge);
                    }

                    String height = binding.edtHeigh.getText().toString();
                    if (!TextUtils.isEmpty(height)) {
                        updatedInfo.setHeight(Integer.parseInt(height));
                    } else {
                        updatedInfo.setHeight(Integer.parseInt("0"));
                    }

                    String weight = binding.edtWeight.getText().toString();
                    if (!TextUtils.isEmpty(weight)) {
                        updatedInfo.setWeight(Float.parseFloat(weight));
                    } else {
                        updatedInfo.setWeight(Float.parseFloat("0"));
                    }

                    editProfileViewModel.updateUserInformation(updatedInfo);
                    sharedViewModel.setUsername(updatedInfo);
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        return binding.getRoot();
    }

    private int calculateAgeFromDob(Date dob) {
        if (dob == null) return 0;

        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dob);

        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) &&
                        today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        return age;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        binding = null;
    }
}