package com.example.misterweeman.ultimatenotakto.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.misterweeman.ultimatenotakto.App;
import com.example.misterweeman.ultimatenotakto.R;
import com.example.misterweeman.ultimatenotakto.fragments.SignInFragment;
import com.example.misterweeman.ultimatenotakto.services.MusicService;

import static com.example.misterweeman.ultimatenotakto.preferences.Utility.loadLocale;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "UltimateNotakto";
    private SignInFragment signInFragment;
    private boolean mIsBound = false;
    private MusicService mServ;
    private boolean firstTime = true;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        App.setLayout(this, R.layout.activity_main);
        doBindService();
        Intent music = new Intent(this,MusicService.class);
        startService(music);

        if (findViewById(R.id.signin_fragment) != null) {
            if (savedInstanceState == null) {
                signInFragment = new SignInFragment();
                signInFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.signin_fragment, signInFragment).commit();
            }
        }
    }

    protected void onResume() {
        super.onResume();
        if(!firstTime) {
            mServ.resumeMusic();
        }
    }

    // called when the user click "Crea Partita"
    public void goToNewGame(View view){

        Log.d(TAG, "goToNewGame()");
        if(isOnline()) {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.connection)
                    .setTitle(R.string.connection_title)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing: become a watcher
                        }
                    });
            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    // called when the user click "Statistiche"
    public void goToStats(View view){
        if(isOnline()) {
            Log.d(TAG, "goToStats()");
            Intent intent = new Intent(this, GameStatsActivity.class);
            startActivity(intent);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.connection)
                    .setTitle(R.string.connection_title)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing: become a watcher
                        }
                    });
            alertDialog = builder.create();
            alertDialog.show();

        }
    }

    // called when the user click "Opzioni"
    public void goToOptions(View view){

        Log.d(TAG, "goToOptions()");

        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mServ != null) {
            mServ.pauseMusic();
            firstTime = false;
        }
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "destroy");
        doUnbindService();
        if(!isChangingConfigurations()) {
            Intent music = new Intent(this, MusicService.class);
            stopService(music);
        }
        super.onDestroy();
    }

    //bind servica musica
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
