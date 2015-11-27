package p1.nd.khan.jubair.mohammadd.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.Bind;

/**
 * Created by laptop on 11/26/2015.
 */
public class BaseActivity extends AppCompatActivity {

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private final String LOG_TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setToolbar(boolean showHomeUp, boolean showTitle) {
        setToolbar(mToolbar, showHomeUp, showTitle);
    }

    public void setToolbar(Toolbar mToolbar, boolean showHomeUp, boolean showTitle) {
        if (mToolbar != null) {
            Log.v(LOG_TAG,"setToolbar - if");

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeUp);
            getSupportActionBar().setDisplayShowTitleEnabled(showTitle);
        }
        else
        {
            Log.v(LOG_TAG,"setToolbar - else");
        }
    }
}
