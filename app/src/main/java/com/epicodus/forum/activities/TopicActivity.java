package com.epicodus.forum.activities;

import android.content.Intent;
import android.os.Parcel;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.epicodus.forum.ForumApplication;
import com.epicodus.forum.R;
import com.epicodus.forum.adapters.FirebaseCategoryListAdapter;
import com.epicodus.forum.adapters.FirebaseTopicListAdapter;
import com.epicodus.forum.fragments.AddContentFragment;
import com.epicodus.forum.fragments.LogOutFragment;
import com.epicodus.forum.fragments.LoginFragment;
import com.epicodus.forum.models.Category;
import com.epicodus.forum.models.Topic;
import com.epicodus.forum.util.FirebaseRecyclerAdapter;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TopicActivity extends FirebaseLoginBaseActivity {
    @Bind(R.id.topicRecycleView) RecyclerView mRecyclerView;
    private Category category;
    private Query mQuery;
    private Firebase mFirebaseRef;
    private FirebaseRecyclerAdapter mAdapter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        category = Parcels.unwrap(bundle.getParcelable("chosenItem"));

        setUpFirebaseQuery();
        setUpRecyclerView();
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topic_menu_sign_in, menu);
        this.menu = menu;
        authValidator();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addTopic:
                if (mFirebaseRef.getAuth() != null) {
                    FragmentManager fm = getSupportFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString("childName", "topics");
                    bundle.putString("categoryId", category.getCategoryId());
                    AddContentFragment topicFragment = AddContentFragment.newInstance();
                    topicFragment.setArguments(bundle);
                    topicFragment.show(fm, "Test String");
                } else {
                    FragmentManager fm = getSupportFragmentManager();
                    LoginFragment loginFragment = LoginFragment.newInstance();
                    loginFragment.show(fm, "fragment_login");
                }
                return true;
            case R.id.signIn:
                if (mFirebaseRef.getAuth() == null) {
                    showFirebaseLoginPrompt();
                } else {
                    FragmentManager logOutFRManager = getSupportFragmentManager();
                    Bundle logOutBundle = new Bundle();
                    logOutBundle.putString("email", mFirebaseRef.getAuth().getProviderData().get("email").toString());
                    LogOutFragment logOutFragment = LogOutFragment.newInstance();
                    logOutFragment.setArguments(logOutBundle);
                    logOutFragment.show(logOutFRManager, "fragment_log_out");
                    authValidator();
                    return true;
                }

        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpFirebaseQuery() {
        Firebase.setAndroidContext(this);
        mFirebaseRef = ForumApplication.getAppInstance().getFirebaseRef();
        String location = mFirebaseRef.child("topics/" + category.getCategoryId()).toString();
        mQuery = new Firebase(location);

    }

    private void setUpRecyclerView() {
        mAdapter = new FirebaseTopicListAdapter(mQuery, Topic.class);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void authValidator() {
        if (menu != null) {
            MenuItem signOption = menu.findItem(R.id.signIn);
            if (mFirebaseRef.getAuth() == null) {
                signOption.setTitle("sign in");
            } else {
                signOption.setTitle("sign out");
            }
        }
    }

    @Override
    protected Firebase getFirebaseRef() {
        return ForumApplication.getAppInstance().getFirebaseRef();
    }

    @Override
    protected void onFirebaseLoginProviderError(FirebaseLoginError firebaseLoginError) {
        Log.d("Facebook error: ", "facebook return an error");
        dismissFirebaseLoginPrompt();
    }

    @Override
    protected void onFirebaseLoginUserError(FirebaseLoginError firebaseLoginError) {
        Log.d("Facebook error: ", "cut users hands");
        dismissFirebaseLoginPrompt();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setEnabledAuthProvider(AuthProviderType.FACEBOOK);
        setEnabledAuthProvider(AuthProviderType.PASSWORD);
    }

    @Override
    public void onFirebaseLoggedIn(AuthData authData) {
        // TODO: Handle successful login
        Log.d("User's UID: ", authData.getUid());
        Log.d("User's UID: ", authData.getProviderData().get("profileImageURL").toString());
        authValidator();
    }
}
