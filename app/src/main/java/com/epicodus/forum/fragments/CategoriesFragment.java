package com.epicodus.forum.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.epicodus.forum.ForumApplication;
import com.epicodus.forum.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends DialogFragment implements View.OnClickListener {
    @Bind(R.id.categoryNameEditText) EditText mCategoryNameText;
    @Bind(R.id.addCategoryButton) Button mAddCategoryButton;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, view);
        mAddCategoryButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        String categoryName = mCategoryNameText.getText().toString();
        saveCategoryToFirebase(categoryName);
        dismiss();
    }

    public void saveCategoryToFirebase(String categoryName) {
        ForumApplication.getAppInstance()
                .getFirebaseRef()
                .child("categories")
                .push()
                .setValue(categoryName);
    }



}
