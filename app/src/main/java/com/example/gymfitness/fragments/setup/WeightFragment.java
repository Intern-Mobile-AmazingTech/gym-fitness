package com.example.gymfitness.fragments.setup;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymfitness.R;
import com.example.gymfitness.databinding.FragmentHowOldBinding;
import com.example.gymfitness.databinding.FragmentWeightBinding;
import com.shawnlin.numberpicker.NumberPicker;

public class WeightFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FragmentWeightBinding binding;

    public WeightFragment() {

    }

    public static HowOldFragment newInstance(String param1, String param2) {
        HowOldFragment fragment = new HowOldFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weight, container, false);

        binding.txtKg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                binding.txtMeasurementUnit.setText("Kg");
            }
        });
        binding.txtLb.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                binding.txtMeasurementUnit.setText("Lb");
            }
        });

        binding.np.setSelectedTypeface(getString(R.string.number_picker_formatter), Typeface.BOLD);
        binding.np.setTypeface(getString(R.string.number_picker_formatter), Typeface.BOLD);
        binding.np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                binding.txtWeight.setText(String.valueOf(newVal));
            }
        });

        return binding.getRoot();
    }
}