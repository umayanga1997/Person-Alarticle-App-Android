package com.openlab.personalarticle.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.openlab.personalarticle.Models.ArticleModel;
import com.openlab.personalarticle.databinding.FragmentHomeBinding;
import com.openlab.personalarticle.utils.ArticleViewAdapter;
import com.openlab.personalarticle.utils.ProgressDialogCustom;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    ArrayList<ArticleModel> articleList;
    ArticleViewAdapter adapter;
    RecyclerView recyclerView;
    ProgressDialogCustom pdc;
    DatabaseReference database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pdc = new ProgressDialogCustom(getContext());
        pdc.show();

        recyclerView = binding.articleList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        database = FirebaseDatabase.getInstance().getReference("Articles");

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                articleList = new ArrayList<ArticleModel>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for (DataSnapshot  snap : dataSnapshot.getChildren()){
                        ArticleModel article = snap.getValue(ArticleModel.class);
                        article.setId(snap.getKey());
                        article.setUserID(dataSnapshot.getKey());
                        if(!article.isPrivateArticle())
                            articleList.add(article);
                    }
                }
                adapter = new ArticleViewAdapter(getContext(), articleList);
                recyclerView.setAdapter(adapter);
                pdc.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pdc.dismiss();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}