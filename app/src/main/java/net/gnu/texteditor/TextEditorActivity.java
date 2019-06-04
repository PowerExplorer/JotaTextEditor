package net.gnu.texteditor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import net.gnu.explorer.SlidingTabsFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import net.gnu.explorer.*;
import android.content.*;
import android.preference.*;
import jp.sblo.pandora.jota.*;

public class TextEditorActivity extends StorageCheckActivity {//

    private static final String TAG = "TextEditorActivity";

    private SlidingTabsFragment slideFrag;
	private FragmentManager supportFragmentManager;
	public TextFrag main;
	
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean hideTitleBar = sp.getBoolean(SettingsActivity.KEY_HIDETITLEBAR , false);
        //Log.d(TAG, hideTitleBar + " hideTitleBar");
		if (hideTitleBar) {
			if ( JotaTextEditor.sIceCreamSandwich ){
                setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
            } else {
                setTheme(R.style.Theme_AppCompat_NoActionBar);
            }
		} else {
			setTheme(R.style.Theme_AppCompat_DayNight);
		}
        setContentView(R.layout.player);

		supportFragmentManager = getSupportFragmentManager();
		if (savedInstanceState == null) {
            slideFrag = SlidingTabsFragment.newInstance(SlidingTabsFragment.Side.LEFT);
        } else {
			slideFrag = (SlidingTabsFragment) supportFragmentManager.findFragmentByTag("slideFrag");
		}
		final FragmentTransaction transaction = supportFragmentManager.beginTransaction();
		transaction.replace(R.id.content_fragment, slideFrag, "slideFrag");
		transaction.commit();
		final Intent intent = getIntent();
		setIntent(null);
		Log.d(TAG, "onCreate intent " + intent + ", savedInstanceState=" + savedInstanceState);
		if (savedInstanceState == null) {
			if (intent != null && intent.getData() != null) {
				slideFrag.addTextTab(intent, intent.getData().getLastPathSegment());//, "utf-8", false, '\n');
			} else {
				slideFrag.addTextTab((Intent)null, (String)null);
			}
		}
    }
	
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart intent=" + getIntent() + ", main=" + main);
		super.onStart();
		if (main == null) {
			main = (TextFrag) slideFrag.getCurrentFragment();
		}
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume main=" + main);
		super.onResume();
		if (main == null) {
			main = (TextFrag) slideFrag.getCurrentFragment();
		}
		Log.d(TAG, "onResume main=" + main);
	}
	
	public void quit() {
		Log.d(TAG, "quit " + main);
		TextFrag.saved = 0;
		TextFrag.count = slideFrag.pagerAdapter.getCount();
		for (int i = TextFrag.count == 1 ? 0 : TextFrag.count - 2; TextFrag.count == 1 ? i==0 : i > 0; i--) {
			final TextFrag item = (TextFrag) slideFrag.pagerAdapter.getItem(slideFrag.pagerAdapter.getCount() == 1 ? 0 : i);
			item.confirmSave(item.mProcQuit);
		}
		//m.confirmSave(m.mProcQuit);
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent " + intent + ", " + intent.getData());
		super.onNewIntent(intent);
		//m.onNewIntent(intent);
		//intent = getIntent();
		//setIntent(null);
		if (intent != null && !Intent.ACTION_MAIN.equals(intent.getAction())) {
			slideFrag.addTextTab(intent, intent.getData().getLastPathSegment());//, "utf-8", false, '\n');
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		return main.onKeyDown(keyCode, event);
	}
	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
		return main.onKeyUp(keyCode, event);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//this.menu = menu;
		Log.d(TAG, "onCreateOptionsMenu " + menu);
        super.onCreateOptionsMenu(menu);
		
        //getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }
	
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		return main.onMenuItemSelected(featureId, item);
////		if (main.onMenuItemSelected(featureId, item)) {
////			return true;
////		} else {
////			return super.onMenuItemSelected(featureId, item);
////		}
//    }
}
