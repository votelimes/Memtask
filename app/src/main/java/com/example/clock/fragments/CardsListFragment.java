package com.example.clock.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clock.R;
import com.example.clock.activities.ManageTaskActivity;
import com.example.clock.adapters.CardsListFragmentAdapter;
import com.example.clock.adapters.CategoriesListFragmentAdapter;
import com.example.clock.app.App;
import com.example.clock.model.Category;
import com.example.clock.model.Project;
import com.example.clock.model.Task;
import com.example.clock.model.Theme;
import com.example.clock.storageutils.LiveDataTransformations;
import com.example.clock.storageutils.Tuple3;
import com.example.clock.viewmodels.MainViewModel;
import com.example.clock.viewmodels.ViewModelFactoryBase;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CardsListFragment extends Fragment {

    RecyclerView mRecyclerView;
    CardsListFragmentAdapter mRecyclerViewAdapter;
    MainViewModel mViewModel;
    LinearLayoutManager mLayoutManager;
    ConstraintLayout mMainLayoutView;
    Context mContext;
    MaterialToolbar toolbar;

    final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 20) {
                    Toast.makeText(getActivity(), "Задача добавлена", Toast.LENGTH_SHORT).show();
                }
                else if(result.getResultCode() == 30){
                    Toast.makeText(getActivity(), "Проект создан", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "Изменения отменены", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_cards_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactoryBase mFactory = new ViewModelFactoryBase(this
                .getActivity()
                .getApplication());

        mContext = getContext();

        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(MainViewModel.class);

        mRecyclerView = getView().findViewById(R.id.cards_list);

        mMainLayoutView = getView().findViewById(R.id.fragment_cards_constraint);

        toolbar = getActivity().findViewById(R.id.topAppBar);

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        mViewModel.intermediate.observe(getViewLifecycleOwner(), hoardObserver);

        ExtendedFloatingActionButton addButton = getView()
                .findViewById(R.id.fragment_cards_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManageTaskActivity.class);
                MaterialAlertDialogBuilder selectObjectTypeDialog = new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Выберите действие")
                        .setItems(R.array.task_dialog_long, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0: // Изменить
                                        Intent taskIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                                        taskIntent.putExtra("mode", "Task");

                                        activityLauncher.launch(taskIntent);
                                        break;
                                    case 1: // Удалить
                                        Intent projectIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                                        projectIntent.putExtra("mode", "Project");

                                        activityLauncher.launch(projectIntent);
                                        break;
                                }
                            }
                        });
                selectObjectTypeDialog.show();

                activityLauncher.launch(intent);
            }
        });


    }

    final Observer<Tuple3<List<Task>, List<Project>, List<Theme>>> hoardObserver = new Observer<Tuple3<List<Task>, List<Project>, List<Theme>>>() {
        @Override
        public void onChanged(@Nullable final Tuple3<List<Task>, List<Project>, List<Theme>> updatedHoard) {
            mRecyclerViewAdapter = new CardsListFragmentAdapter(
                    activityLauncher,
                    new Tuple3<List<Task>, List<Project>, List<Theme>>
                            (mViewModel.getTasksByCategory(App.getSettings().getLastCategory().first),
                                mViewModel.getProjectsByCategory(App.getSettings().getLastCategory().first),
                                mViewModel.getThemes()));

            toolbar.setTitle(App.getSettings().getLastCategory().second);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    };
}
