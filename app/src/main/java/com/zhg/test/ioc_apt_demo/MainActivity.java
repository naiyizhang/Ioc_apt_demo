package com.zhg.test.ioc_apt_demo;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhg.ioc.BindView;
import com.zhg.ioc.api.ViewInjector;
import com.zhg.test.ioc_apt_demo.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements MyListFragment.OnListFragmentInteractionListener {

    @BindView(R.id.id_btn)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.injectView(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getSupportFragmentManager().beginTransaction().replace(R.id.id_container,new MyListFragment()).commit();
            }
        });
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(this,item.content+","+item.details,Toast.LENGTH_SHORT).show();
    }
}
