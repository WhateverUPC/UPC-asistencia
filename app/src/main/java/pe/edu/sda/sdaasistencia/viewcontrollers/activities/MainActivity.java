package pe.edu.sda.sdaasistencia.viewcontrollers.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import pe.edu.sda.sdaasistencia.R;
import pe.edu.sda.sdaasistencia.viewcontrollers.fragments.HomeFragment;
import pe.edu.sda.sdaasistencia.viewcontrollers.fragments.NewQrFragment;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager manager = getFragmentManager();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment homeFragment = new HomeFragment();
                    manager.beginTransaction().replace(R.id.contentFrameLayout, homeFragment).commit();
                    return true;
                case R.id.navigation_attendance:
                    Intent qrAttendanceIntent = new Intent(getApplicationContext(), QrAttendance.class);
                    startActivity(qrAttendanceIntent);
                    return true;
                case R.id.navigation_new_qr:
                    NewQrFragment newQrFragment = new NewQrFragment();
                    manager.beginTransaction().replace(R.id.contentFrameLayout, newQrFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setItemIconTintList(null);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if(savedInstanceState == null){ //verificar si es el primer onCreate
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }

}
