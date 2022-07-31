package com.votelimes.memtask.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.votelimes.memtask.R;
import com.votelimes.memtask.activities.ContactActivity;
import com.votelimes.memtask.activities.ManageTaskActivity;
import com.votelimes.memtask.adapters.CardsListFragmentAdapter;
import com.votelimes.memtask.app.App;
import com.votelimes.memtask.model.ProjectData;
import com.votelimes.memtask.model.TaskAndTheme;
import com.votelimes.memtask.model.Theme;
import com.votelimes.memtask.storageutils.Tuple3;
import com.votelimes.memtask.viewmodels.CategoryActivitiesViewModel;
import com.votelimes.memtask.viewmodels.MemtaskViewModelBase;
import com.votelimes.memtask.viewmodels.ViewModelFactoryBase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.appbar.MaterialToolbar;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.core.ContactPickerActivity;

import java.util.List;

public class CardsListFragment extends Fragment implements SearchView.OnQueryTextListener {

    RecyclerView mRecyclerView;
    CardsListFragmentAdapter mRecyclerViewAdapter;
    CategoryActivitiesViewModel mViewModel;
    LinearLayoutManager mLayoutManager;
    ConstraintLayout mMainLayoutView;
    Context mContext;
    MaterialToolbar toolbar;
    SearchView searchView;


    final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 20) {
                    Toast.makeText(getActivity(), "Задача добавлена", Toast.LENGTH_SHORT).show();
                }
                else if(result.getResultCode() == 30){
                    Toast.makeText(getActivity(), "Проект создан", Toast.LENGTH_SHORT).show();
                }
                else if(result.getResultCode() == ContactActivity.RESULT_RETURN_ITEMS){
                    String contactsList = result.getData().getStringExtra(ContactActivity.RESULT_KEY);
                    mViewModel.putContacts(contactsList);
                }
                if(result.getData() != null && result.getData().hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)){

                    List<Contact> contacts = (List<Contact>) result
                            .getData()
                            .getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
                    String contactsList = "";

                    if(contacts != null) {
                        for (int i = 0; i < contacts.size(); i++) {
                            contactsList = contactsList + String.valueOf(contacts.get(i).getId());
                            if (i != contacts.size() - 1) {
                                contactsList = contactsList + "";
                            }
                        }
                        if(contactsList.length() != 0){
                            mViewModel.putContacts(contactsList);
                        }
                    }

                    /*// process groups
                    List<Group> groups = (List<Group>) result
                            .getData()
                            .getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA);
                    for (Group group : groups) {
                        // process the groups...
                    }*/
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
                .getApplication(), App.getDatabase(), App.getSilentDatabase());

        mContext = getContext();

        mViewModel = new ViewModelProvider(getActivity(), mFactory).get(CategoryActivitiesViewModel.class);

        mViewModel.loadData();

        mRecyclerView = getView().findViewById(R.id.cards_list);

        mMainLayoutView = getView().findViewById(R.id.fragment_cards_constraint);

        toolbar = getActivity().findViewById(R.id.toolbar);

        //toolbar.findViewById(R.id.action_search).setVisibility(View.VISIBLE);

        toolbar.setTitle(App.getSettings().getLastCategory().second);

        mLayoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        mViewModel.intermediate.observe(getViewLifecycleOwner(), onCreateObserver);

        FloatingActionButton fabTask = getView().findViewById(R.id.fragment_cards_fab_task);
        FloatingActionButton fabProject = getView().findViewById(R.id.fragment_cards_fab_proj);
        FloatingActionMenu fabMenu = getView().findViewById(R.id.fragment_cards_fabl);


        fabTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                mViewModel.addTaskChild();
                mRecyclerViewAdapter.notifyItemInserted(0);
                mRecyclerViewAdapter.scrollTo(0);
                mRecyclerViewAdapter.setAddedOutside(0);
            }
        });
        fabProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent projectIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                projectIntent.putExtra(MemtaskViewModelBase.MTP_MODE, MemtaskViewModelBase.PROJECT_CREATING);
                projectIntent.putExtra(MemtaskViewModelBase.MTP_CATEGORY_ID, App.getSettings().getLastCategory().first);
                fabMenu.close(true);
                activityLauncher.launch(projectIntent);
            }
        });

        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        ItemTouchHelper.SimpleCallback touchHelperCallbackRight = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CardsListFragmentAdapter.ProjectViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mRecyclerViewAdapter.removeItem(viewHolder.getAbsoluteAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallbackRight);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        ItemTouchHelper.SimpleCallback touchHelperCallbackLeft = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CardsListFragmentAdapter.ProjectViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Intent taskIntent = new Intent(view.getContext(), ManageTaskActivity.class);
                if(viewHolder instanceof CardsListFragmentAdapter.TaskViewHolder){
                    CardsListFragmentAdapter.TaskViewHolder vh = (CardsListFragmentAdapter.TaskViewHolder) viewHolder;
                    taskIntent.putExtra(MemtaskViewModelBase.MTP_ID, vh.getBinding().getData().getTask().getTaskId());
                    taskIntent.putExtra(MemtaskViewModelBase.MTP_MODE, MemtaskViewModelBase.TASK_EDITING);
                    taskIntent.putExtra(MemtaskViewModelBase.MTP_CATEGORY_ID, vh.getBinding().getData().getTask().getCategoryId());
                }
                else if(viewHolder instanceof CardsListFragmentAdapter.ProjectViewHolder){
                    CardsListFragmentAdapter.ProjectViewHolder vh = (CardsListFragmentAdapter.ProjectViewHolder) viewHolder;
                    taskIntent.putExtra(MemtaskViewModelBase.MTP_ID, vh.getBinding().getData().getProject().getProjectId());
                    taskIntent.putExtra(MemtaskViewModelBase.MTP_MODE, MemtaskViewModelBase.PROJECT_EDITING);
                    taskIntent.putExtra(MemtaskViewModelBase.MTP_CATEGORY_ID, vh.getBinding().getData().getProject().getCategoryId());
                }
                activityLauncher.launch(taskIntent);
                CardsListFragmentAdapter adapter = (CardsListFragmentAdapter) viewHolder.getBindingAdapter();
                adapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
            }
        };
        itemTouchHelper = new ItemTouchHelper(touchHelperCallbackLeft);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        }

    final Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>> onCreateObserver = new Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>>() {
        @Override
        public void onChanged(@Nullable final Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>> updatedHoard) {
            mViewModel.init();
            mRecyclerViewAdapter = new CardsListFragmentAdapter(
                    activityLauncher, mViewModel, mMainLayoutView, mLayoutManager, getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
            mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    mRecyclerView.requestFocus();
                    return false;
                }
            });
            App.getInstance().fixLoadTimer();
        }
    };

    final Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>> onUpdateObserver = new Observer<Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>>>() {
        @Override
        public void onChanged(@Nullable final Tuple3<List<TaskAndTheme>, List<ProjectData>, List<Theme>> updatedHoard) {
            mViewModel.init();
            App.getInstance().fixLoadTimer();
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mViewModel.updateData(newText).observe(this, onUpdateObserver);
        return false;
    }
}
