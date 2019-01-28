package com.example.android.poddle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PodacastSelectedActivity extends AppCompatActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podacast_selected);

        mToolbar = (Toolbar) findViewById(R.id.toolbarSelected);

        String data = getIntent().getStringExtra("PODCAST_INTENT");

        replaceSelectedPodcastActivityContent(PodcastSelectedFragment.NewInstance(data),false);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
    }

    private void replaceSelectedPodcastActivityContent(Fragment fragment, boolean shouldAddBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content,fragment);
        if (shouldAddBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

}
