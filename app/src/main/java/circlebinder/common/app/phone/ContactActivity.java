package circlebinder.common.app.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import net.ichigotake.common.app.ActivityNavigation;

import net.ichigotake.common.app.BaseActivity;
import net.ichigotake.common.app.IntentFactory;

import circlebinder.R;

public final class ContactActivity extends BaseActivity {

    public static IntentFactory from() {
        return new IntentFactory() {
            @Override
            public Intent createIntent(Context context) {
                return new Intent(context, ContactActivity.class);
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_contact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return ActivityNavigation.back(this, menuItem)
                || super.onOptionsItemSelected(menuItem);
    }

}
