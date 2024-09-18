package com.example.gymfitness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.example.gymfitness.R;
import com.example.gymfitness.data.entities.Exercise;
import com.example.gymfitness.databinding.ExerciseInRoutineItemBinding;
import com.example.gymfitness.helpers.FavoriteHelper;

import java.util.List;

public class ExerciseInOwnRoutineAdapter extends BaseAdapter { // adapter cho exercise_in_routine_item.xml
    private final List<Exercise> exerciseList;
    private final LayoutInflater inflater;
    private final ExerciseRemoveListener exerciseRemoveListener;

    public ExerciseInOwnRoutineAdapter(Context context, List<Exercise> exerciseList, ExerciseRemoveListener exerciseRemoveListener) {
        this.exerciseList = exerciseList;
        this.inflater = LayoutInflater.from(context);
        this.exerciseRemoveListener = exerciseRemoveListener;
    }

    @Override
    public int getCount() {
        return exerciseList.size();
    }

    @Override
    public Object getItem(int i) {
        return exerciseList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ExerciseInRoutineItemBinding binding;

        if(view == null) {
            binding = ExerciseInRoutineItemBinding.inflate(inflater, viewGroup, false);
            view = binding.getRoot();
            view.setTag(binding);
        }
        else {
            binding = (ExerciseInRoutineItemBinding) view.getTag();
        }

        Exercise exercise = exerciseList.get(i);
        binding.setItem(exercise);
        Glide.with(binding.thumbnail.getContext())
                .load(exercise.getExerciseThumb())
                .placeholder(R.drawable.woman_help_home)
                .into(binding.thumbnail);

        binding.imgPlayvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseRemoveListener.onExerciseRemoved(exercise);
            }
        });

        binding.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        FavoriteHelper.checkFavorite(exercise, view.getContext(), binding.imgStar);
        binding.imgStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteHelper.setFavorite(exercise,v.getContext(), binding.imgStar);
            }
        });
        return view;
    }

    public interface ExerciseRemoveListener {
        void onExerciseRemoved(Exercise exercise);
    }
}
