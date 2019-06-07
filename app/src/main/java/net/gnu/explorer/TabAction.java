package net.gnu.explorer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.content.Intent;

public interface TabAction {
	TabClicks tabClicks;
	public boolean circular();
	public void closeCurTab();
	public void closeTab(Frag m);
	public void closeOtherTabs();
	public void addTab(final String path);//String dir, String suffix, boolean multi, Bundle bundle
	public void addTextTab(final Intent intent, final String title);
	public int realFragCount();
	
}


