package net.gnu.explorer;

import android.content.*;
import android.view.*;
import android.widget.*;
import android.util.*;
import net.gnu.texteditor.TextEditorActivity;
import jp.sblo.pandora.jota.*;

public class TabClicks {

	private static final String TAG = "TabClicks";

	private final int maxTabs;

	public TabClicks(final int maxTabs) {
		this.maxTabs = maxTabs;
	}

	public void click(final Context ctx, final SlidingTabsFragment.PagerAdapter adapter, final TabAction tabAction, final View v, final Frag fra) {
		Log.d(TAG, "click " + ctx + ", " + tabAction + ", " + v);
		if (tabAction == null || ctx == null || v == null) {
			return;
		}
		final PopupMenu popup = new PopupMenu(ctx, v);
		popup.getMenuInflater().inflate(R.menu.newtexttab, popup.getMenu());
		
		final int count = tabAction.realFragCount();
		final Menu menu = popup.getMenu();
		if (count == 1) {
			menu.findItem(R.id.close).setVisible(false);
			menu.findItem(R.id.closeOthers).setVisible(false);
		}

		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(final MenuItem item) {
						Log.d(TAG, "clicked " + item);
						final int itemId = item.getItemId();
						if (R.id.close == itemId) {
							tabAction.closeCurTab();
						} else if (R.id.closeOthers == itemId) {
							tabAction.closeOtherTabs();
						} else if (R.id.newTab == itemId) {
							if (count < maxTabs) {
								tabAction.addTab((String)null);
							} else {
								Toast.makeText(ctx, "Maximum " + count + " tabs only", Toast.LENGTH_SHORT).show();
							}
						} 
						return true;
					}
				});
		
		popup.show();
	}
}
