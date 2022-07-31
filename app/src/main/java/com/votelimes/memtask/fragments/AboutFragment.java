package com.votelimes.memtask.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.votelimes.memtask.R;
import com.votelimes.memtask.activities.MainActivity;
import com.votelimes.memtask.app.App;
import com.google.android.material.appbar.MaterialToolbar;


public class AboutFragment extends Fragment {

    MaterialToolbar toolbar;
    String backStackTitle = "";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        toolbar = (MaterialToolbar) getActivity().findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).drawerLayout.close();

        toolbar.findViewById(R.id.action_search).setVisibility(View.GONE);
        backStackTitle = (String) toolbar.getTitle();
        toolbar.setTitle("О приложении");

        toolbar.getMenu().findItem(R.id.action_search).setVisible(false);
        toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    toolbar.setNavigationIcon(null);
                    ((MainActivity) getActivity()).setupNav();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        if(App.isTesting()){
            TextView text = (TextView) getView().findViewById(R.id.about_text);
            text.setText(text.getText() + "\n" + "App load time: " + String.valueOf(App.getInstance().getWorkTimeMillis()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toolbar.getMenu().findItem(R.id.action_search).setVisible(true);
        toolbar.setTitle(backStackTitle);
    }

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}