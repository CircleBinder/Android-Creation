package circlebinder.creation.app.phone;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import circlebinder.creation.app.BaseActivity;
import circlebinder.R;
import circlebinder.creation.initialize.DatabaseInitializeService;
import circlebinder.creation.initialize.IInitializeBindService;
import circlebinder.creation.initialize.IInitializeServiceCallback;

public final class DatabaseInitializeActivity extends BaseActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, DatabaseInitializeActivity.class);
    }

    private final Handler handler = new Handler();
    private boolean serviceBind;
    private IInitializeBindService mService;
    private IInitializeServiceCallback callback = new IInitializeServiceCallback.Stub() {
        @Override
        public void initializeCompleted() throws RemoteException {
            handler.post(() -> {
                Fragment callback1 = getFragmentManager()
                        .findFragmentByTag(getString(R.string.fragment_tag_data_initialize));
                if (callback1 instanceof IInitializeServiceCallback) {
                    try {
                        ((IInitializeServiceCallback) callback1).initializeCompleted();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new IllegalStateException("not implements");
                }
            });
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInitializeBindService.Stub.asInterface(service);
            try {
                mService.setObserver(callback);
                mService.AsyncStart();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_initialize);
        serviceBind = true;
        bindService(new Intent(this, DatabaseInitializeService.class), serviceConnection, 0);
    }


    @Override
    public void onDestroy() {
        if (serviceBind) {
            unbindService(serviceConnection);
            serviceBind = false;
        }
        super.onDestroy();
    }

}
