package net.gnu.explorer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
//import com.afollestad.materialdialogs.DialogAction;
//import com.afollestad.materialdialogs.MaterialDialog;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.gnu.texteditor.TextFrag;
import android.view.MotionEvent;
import android.app.Activity;
import net.gnu.texteditor.*;
import android.app.*;
import android.content.*;

public abstract class Frag extends Fragment implements Cloneable, Serializable {

	private static final String TAG = "Frag";

	public String currentPathTitle = "";
	protected String title;

	protected ViewGroup sortBarLayout;
	public TextEditorActivity activity;
	protected FragmentActivity fragActivity;
	public SharedPreferences sharedPref;
	
	public SlidingTabsFragment slidingTabsFragment;
	
	protected boolean fake = false;
	private Toast toast = null;
    
	public static Frag getFrag(final SlidingTabsFragment sliding, final String path) {
		Frag frag = null;
		frag = TextFrag.newInstance(null, "Text", path);
		
		if (frag != null) {
			frag.currentPathTitle = path;
			frag.slidingTabsFragment = sliding;
		}
		return frag;
	}

	public void updateList() {} //TODO
	public void load(String path) {}
	public void load(String path, Runnable run) {}
	
	public void clone(final Frag frag, final boolean fake) {
		this.title = frag.title;
		this.currentPathTitle = frag.currentPathTitle;
		this.slidingTabsFragment = frag.slidingTabsFragment;
		this.fake = fake;
	}

	public Frag clone(final boolean fake) {
		final Frag frag = Frag.getFrag(slidingTabsFragment, currentPathTitle);
		frag.clone(this, fake);
		return frag;
	}

	public String getTitle() {
		if (currentPathTitle != null && currentPathTitle.length() > 0) {
			return currentPathTitle.substring(currentPathTitle.lastIndexOf("/") + 1);
		} else {
			return title;
		}
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		//Log.d(TAG, "onSaveInstanceState" + path + ", " + outState);
		super.onSaveInstanceState(outState);
		outState.putString(Constants.EXTRA_ABSOLUTE_PATH, currentPathTitle);
		outState.putString("title", title);
	}

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ((fragActivity = getActivity()) instanceof TextEditorActivity) {
			activity = (TextEditorActivity)fragActivity;
		}
		sharedPref = PreferenceManager.getDefaultSharedPreferences(fragActivity);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		//Log.d(TAG, "onViewCreated " + savedInstanceState);
		super.onViewCreated(view, savedInstanceState);
		if ((fragActivity = getActivity()) instanceof TextEditorActivity) {
			activity = (TextEditorActivity)fragActivity;
		}

		setRetainInstance(true);
		final Bundle args = getArguments();
		if ((currentPathTitle == null || currentPathTitle.length() == 0) && args != null) {
			title = args.getString("title");
			currentPathTitle = args.getString(Constants.EXTRA_ABSOLUTE_PATH);
		}
		if (savedInstanceState != null) {
			title = savedInstanceState.getString("title");
			currentPathTitle = savedInstanceState.getString(Constants.EXTRA_ABSOLUTE_PATH);
		}
        
	}

	@Override
	public void onLowMemory() {
		Log.d(TAG, "onLowMemory " + Runtime.getRuntime().freeMemory());
		super.onLowMemory();
		//Glide.get(getContext()).clearMemory();
	}

	@Override
	public void onStart() {
		//Log.d(TAG, "onStart");
		if ((fragActivity = getActivity()) instanceof TextEditorActivity) {
			activity = (TextEditorActivity)fragActivity;
		}
		super.onStart();
	}

	@Override
    public void onResume() {
        //Log.d(TAG, "onResume " + title);
		super.onResume();
		if ((fragActivity = getActivity()) instanceof TextEditorActivity) {
			activity = (TextEditorActivity)fragActivity;
		}
        //startFileObserver();
        //fixIcons(false);
    }

    protected void showToast(final int messageId) {
		showToast(getString(messageId));
	}

	protected void showToast(final String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(fragActivity, message, Toast.LENGTH_SHORT);
        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(fragActivity, message, Toast.LENGTH_SHORT);
        } else {
			this.toast.cancel();
            // Updating toast message is showing
            this.toast.setText(message);
        }
        // Showing toast finally
        this.toast.show();
    }

	@Override
	public void onAttach(final Activity activity) {
		//Log.d(TAG, "onAttach " + title);
		super.onAttach(activity);
		this.fragActivity = (FragmentActivity) activity;
		if (fragActivity instanceof TextEditorActivity) {
			this.activity = (TextEditorActivity)fragActivity;
		}
	}

	@Override
	public void onDetach() {
		//Log.d(TAG, "onDetach " + title);
		super.onDetach();
		this.fragActivity = null;
		//this.activity = null;
	}

	@Override
	public String toString() {
		return "fake " + fake;
	}
	// Print all activity info in current task back stack.
	public static void printCurrentTaskActivityList(Context context, String tagName)
	{
		ActivityManager activityManager = (ActivityManager) context.getSystemService( context.ACTIVITY_SERVICE );

		List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();

		if(appTaskList!=null)
		{
			int size = appTaskList.size();
			for(int i=0;i<size;i++)
			{
				ActivityManager.AppTask appTask = appTaskList.get(i);
				ActivityManager.RecentTaskInfo recentTaskInfo = appTask.getTaskInfo();

				int taskId = recentTaskInfo.persistentId;

				int activityNumber = recentTaskInfo.numActivities;

				ComponentName origActivity = recentTaskInfo.origActivity;

				ComponentName baseActivity = recentTaskInfo.baseActivity;

				ComponentName topActivity = recentTaskInfo.topActivity;

				Log.d(tagName, "Task Id : " + taskId);
				Log.d(tagName, "Activity Number : " + activityNumber);

				if(origActivity!=null) {
					Log.d(tagName, "Original Activity : " + origActivity.toString());
				}

				if(baseActivity!=null) {
					Log.d(tagName, "Base Activity : " + baseActivity.toString());
				}

				if(topActivity!=null) {
					Log.d(tagName, "Top Activity : " + topActivity.toString());
				}

				Log.d(tagName, "************************************************");
			}
		}
	}
	
	// Print fragment manager managed fragment in debug log.
//	public static void printActivityFragmentList(FragmentManager fragmentManager)
//	{
//		// Get all Fragment list.
//		List<Fragment> fragmentList = fragmentManager.getFragment();
//
//		if(fragmentList!=null)
//		{
//			int size = fragmentList.size();
//			for(int i=0;i<size;i++)
//			{
//				Fragment fragment = fragmentList.get(i);
//
//				if(fragment!=null) {
//					String fragmentTag = fragment.getTag();
//					Log.d(TAG, fragmentTag);
//				}
//			}
//
//			Log.d(TAG, "***********************************");
//		}
//	}
	
	
}
