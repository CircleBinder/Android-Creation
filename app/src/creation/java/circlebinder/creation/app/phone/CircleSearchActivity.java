package circlebinder.creation.app.phone;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import net.ichigotake.common.app.ActivityFactory;
import net.ichigotake.common.app.ActivityNavigation;

import circlebinder.common.search.CircleSearchOption;
import circlebinder.creation.app.BaseActivity;
import circlebinder.R;
import circlebinder.creation.search.OnCircleSearchOptionListener;

public final class CircleSearchActivity extends BaseActivity implements OnCircleSearchOptionListener {

    public static ActivityFactory factory() {
        return new CircleSearchActivityFactory();
    }

    private static class CircleSearchActivityFactory implements ActivityFactory {

        @Override
        public Intent create(Context context) {
            return new Intent(context, CircleSearchActivity.class);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_search);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.common_circle_search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return ActivityNavigation.back(this, menuItem)
                || super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void setSearchOption(CircleSearchOption searchOption) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.activity_circle_search_container);
        if (fragment != null && fragment.isResumed() && fragment instanceof OnCircleSearchOptionListener) {
            ((OnCircleSearchOptionListener)fragment).setSearchOption(searchOption);
        }
    }

}
