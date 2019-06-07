package net.gnu.explorer;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gnu.common.view.SlidingHorizontalScroll;
import android.support.v7.widget.*;
import android.support.v4.app.*;
import net.gnu.texteditor.*;
import net.gnu.explorer.SlidingTabsFragment.*;
import android.os.*;
import java.util.*;
import android.view.animation.*;
import android.content.Intent;
import android.widget.Button;
import android.graphics.PorterDuff;
import android.app.Activity;
import net.gnu.util.Util;
import android.widget.ImageView;
import android.view.ViewGroup.LayoutParams;
import jp.sblo.pandora.jota.*;
import android.support.v7.app.*;

public class SlidingTabsFragment extends Fragment implements TabAction {

	private static final String TAG = "SlidingTabsFragment";
	private FragmentManager childFragmentManager;

	private SlidingHorizontalScroll mSlidingHorizontalScroll;

	private ViewPager mViewPager;
	public PagerAdapter pagerAdapter;
	int pageSelected = 1;

	private ArrayList<PagerItem> mTabs = new ArrayList<PagerItem>();
	
	public static SlidingTabsFragment newInstance() {
		final SlidingTabsFragment s = new SlidingTabsFragment();
		return s;
	}

	@Override
	public String toString() {
		return "pageSelected=" + pageSelected;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
							 final Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, "onCreateView " + savedInstanceState);
		return inflater.inflate(R.layout.fragment_sample, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final Bundle args = getArguments();
		Log.d(TAG, "onViewCreated args " + args + ", savedInstanceState " + savedInstanceState);
		setRetainInstance(true);
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		childFragmentManager = getChildFragmentManager();

		if (savedInstanceState == null) {
			pagerAdapter = new PagerAdapter(childFragmentManager);
			mViewPager.setAdapter(pagerAdapter);
			Log.d(TAG, "onViewCreated mViewPager " + mViewPager + ", mTabs " + mTabs);
			if (args != null) {
				mViewPager.setCurrentItem(args.getInt("pos", pageSelected), true);
			} else {
				mViewPager.setCurrentItem(pageSelected);
			}
		} else {
			mTabs.clear();
			final List<Fragment> fragments = childFragmentManager.getFragments();
			final String firstTag = savedInstanceState.getString("fake0");
			final String lastTag = savedInstanceState.getString("fakeEnd");
			String tag;
			PagerItem pagerItem;
			Frag frag;
			final int size = fragments.size();
			FragmentTransaction ft = null;
			ft = childFragmentManager.beginTransaction();
			for (int i = 0; i < size; i++) {
				tag = savedInstanceState.getString(i + "");
				frag = (Frag) childFragmentManager.findFragmentByTag(tag);
				if (frag != null) {
					pagerItem = new PagerItem(frag);
					//Log.d(TAG, "onViewCreated frag " + i + ", " + tag + ", " + frag.getTag() + ", " + pagerItem.dir + ", " + frag);
					mTabs.add(pagerItem);
					ft.remove(frag);
				}
			}
			if (firstTag != null) {
				final SlidingTabsFragment.PagerItem get0 = mTabs.get(0);
				get0.fakeFrag = (Frag) childFragmentManager.findFragmentByTag(firstTag);
				get0.fakeFrag.slidingTabsFragment = this;
				final SlidingTabsFragment.PagerItem last = mTabs.get((mTabs.size() - 1));
				last.fakeFrag = (Frag) childFragmentManager.findFragmentByTag(lastTag);
				last.fakeFrag.slidingTabsFragment = this;
				ft.remove(get0.fakeFrag);
				ft.remove(last.fakeFrag);
			}
			ft.commitNow();
			
			//Log.d(TAG, "mTabs=" + mTabs);
			//Log.d(TAG, "fragments=" + fragments);
			pagerAdapter = new PagerAdapter(childFragmentManager);
			mViewPager.setAdapter(pagerAdapter);
			mViewPager.setCurrentItem(savedInstanceState.getInt("pos", pageSelected), true);
		}
		mViewPager.setOffscreenPageLimit(16);

		// Give the SlidingTabLayout the ViewPager, this must be done AFTER the
		// ViewPager has had it's PagerAdapter set.
		mSlidingHorizontalScroll = (SlidingHorizontalScroll) view.findViewById(R.id.sliding_tabs);
		
		final TextEditorActivity activ = (TextEditorActivity)getActivity();
		if (activ instanceof TextEditorActivity) {
			final View v = mSlidingHorizontalScroll.getChildAt(0);
			final ViewGroup.LayoutParams lp = v.getLayoutParams();
			lp.height = (int)(30 * getResources().getDisplayMetrics().density);
			v.setLayoutParams(lp);
		}
		
		mSlidingHorizontalScroll.fra = SlidingTabsFragment.this;
		tabClicks = new TabClicks(12);

		mSlidingHorizontalScroll.setViewPager(mViewPager);
		mSlidingHorizontalScroll.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				@Override
				public void onPageScrolled(final int pageSelected, final float positionOffset,
										   final int positionOffsetPixel) {
//					Log.e("onPageScrolled", "pageSelected: " + pageSelected
//						+ ", positionOffset: " + positionOffset
//						+ ", positionOffsetPixel: " + positionOffsetPixel);
					if (positionOffset == 0 && positionOffsetPixel == 0) {
						final int size = mTabs.size();
						if (size > 1) {
							if (pageSelected == 0) {
								mViewPager.setCurrentItem(size, false);
							} else if (pageSelected == size + 1) {
								mViewPager.setCurrentItem(1, false);
							}
						}
					} 
				}

				@Override
				public void onPageSelected(final int position) {
					final int size = mTabs.size();
					Log.d(TAG, "onPageSelected: " + position + ", mTabs.size() " + size);
					pageSelected = position;
					if (size > 1) {
						if (position == 1 || position == size) {
							final int newpos = position == 1 ? (size - 1) : position == size ? 0 : (position - 1);
							final PagerItem pi = mTabs.get(newpos);
							
							Log.d(TAG, "onPageSelected: " + position + ", pi.frag " + pi.frag + ", pi.fakeFrag " + pi.fakeFrag);
							if (pi.fakeFrag != null) {
								pi.fakeFrag.clone(pi.frag, true);
							} else {
								pi.createFakeFragment();
							}
						}
						final ActionBar actionBar = activ.getSupportActionBar();
						if (actionBar != null) {
							final TextFrag textFrag = (TextFrag)pagerAdapter.getItem(position);
							actionBar.setCustomView(textFrag.mToolbarBase);
							textFrag.mEditor.requestFocus();
						}
					}
				}

				@Override
				public void onPageScrollStateChanged(final int state) {
					Log.d(TAG, "onPageScrollStateChanged1 state " + state + ", pageSelected " + pageSelected);
					if (state == 0) {
						final int size = mTabs.size();
						if (pageSelected == 0) {
							mViewPager.setCurrentItem(size, false);
							pageSelected = size;
						} else if (pageSelected == size + 1) {
							mViewPager.setCurrentItem(1, false);
							pageSelected = 1;
						}
						final ActionBar actionBar = activ.getSupportActionBar();
						if (actionBar != null) {
							final TextFrag textFrag = (TextFrag)pagerAdapter.getItem(pageSelected);
							actionBar.setCustomView(textFrag.mToolbarBase);
							textFrag.mEditor.requestFocus();
						}
					}
					Log.d(TAG, "onPageScrollStateChanged2 state " + state + ", pageSelected " + pageSelected);
				}
			});
		//Log.d(TAG, "mSlidingHorizontalScroll " + mSlidingHorizontalScroll);
		mSlidingHorizontalScroll.setCustomTabColorizer(new SlidingHorizontalScroll.TabColorizer() {
				@Override
				public int getIndicatorColor(int position) {
					return 0xFF039BE5;
				}
				@Override
				public int getDividerColor(int position) {
					return 0xff888888;
				}
			});
	}

	public boolean circular() {
		return true;
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume pagerAdapter=" + pagerAdapter + ", mTabs=" + mTabs + ", newIntent " + newIntent);
		super.onResume();
		if (newIntent) {
			newIntent = false;
			addFrag(main, pagerItem);
			main = null;
			pagerItem = null;
		}
	}
	
	private boolean newIntent = false;
	private TextFrag main = null;
	private PagerItem pagerItem = null;
	public void addTextTab(final Intent intent, String title) {
		if (title == null || title.length() == 0) {
			title = "Untitled " + ++TextFrag.no + ".txt";
		}
		Log.d(TAG, "addTextTab1 mViewPager=" + mViewPager + ", pagerAdapter=" + pagerAdapter + ", filename=" + title + ", mTabs=" + mTabs);
		main = TextFrag.newInstance(intent, title, null);
		pagerItem = new PagerItem(main);
		main.slidingTabsFragment = this;

		if (mViewPager != null && mTabs.size() == 0) {
			mTabs.add(pagerItem);
			pagerAdapter.notifyDataSetChanged();
			mViewPager.setCurrentItem(pagerAdapter.getCount() - 1);
			notifyTitleChange();
		} else if (mTabs.size() >= 1) {
			newIntent = true;
		} else {
			mTabs.add(pagerItem);
		}
		Log.d(TAG, "addTextTab2 " + title + ", " + mTabs);
	}

	public void addTab(final String path) {
		Log.d(TAG, "addTab path=" + path + ", mTabs=" + mTabs);
		final PagerItem pagerItem;
		Frag frag = null;
		frag = getCurrentFragment();
			if (getActivity() instanceof TextEditorActivity) {
				final TextFrag main = TextFrag.newInstance(null, "Untitled " + ++TextFrag.no + ".txt", path);
				main.slidingTabsFragment = this;
				pagerItem = new PagerItem(main);
			} else {
				return;
			}
		addFrag(frag, pagerItem);
	}

	private void addFrag(Frag frag, PagerItem pagerItem) {
		final FragmentTransaction ft = childFragmentManager.beginTransaction();
		final ArrayList<PagerItem> mTabs2 = new ArrayList<PagerItem>(mTabs);
		final int size = mTabs.size();
		int currentItem = 0;
		if (size > 1) {
			currentItem = mViewPager.getCurrentItem();
			Log.d(TAG, "addFrag1 currentItem " + currentItem + ", dir=" + frag.currentPathTitle + ", mTabs=" + mTabs);

			PagerItem pi = mTabs.get(0);
			ft.remove(pi.fakeFrag);
			pi.fakeFrag = null;

			pi = mTabs.get(size - 1);
			ft.remove(pi.fakeFrag);
			pi.fakeFrag = null;

			for (int j = 0; j < size; j++) {
				ft.remove(mTabs.remove(0).frag);
			}
			pagerAdapter.notifyDataSetChanged();
			ft.commitNow();

			mTabs.addAll(mTabs2);
			
			mTabs.add(currentItem++, pagerItem);
			mViewPager.setAdapter(pagerAdapter);
			mViewPager.setCurrentItem(currentItem, false);
		} else {
			PagerItem remove = mTabs.remove(0);
			ft.remove(remove.frag);
			pagerAdapter.notifyDataSetChanged();
			ft.commitNow();
			mTabs.add(remove);
			mTabs.add(pagerItem);
			pagerAdapter.notifyDataSetChanged();
			currentItem = 2;
			mViewPager.setCurrentItem(currentItem);
		}
		notifyTitleChange();
		final ActionBar actionBar = ((TextEditorActivity)getActivity()).getSupportActionBar();
		if (actionBar != null) {
			final TextFrag textFrag = ((TextFrag)pagerAdapter.getItem(currentItem));
			actionBar.setCustomView(textFrag.mToolbarBase);
			textFrag.mEditor.requestFocus();
		}
		Log.d(TAG, "addFrag2 " + frag.currentPathTitle + ", mViewPager.getCurrentItem() " + mViewPager.getCurrentItem() + ", " + mTabs);
	}

	public int realFragCount() {
		return mTabs.size();
	}

	public void closeTab(Frag m) {
		int i = 0;
		final ArrayList<PagerItem> mTabs2 = new ArrayList<PagerItem>(mTabs);
		for (PagerItem pi : mTabs) {
			if (pi.frag == m) {
				Log.i(TAG, "closeTab " + i);
				break;
			}
			i++;
		}
		Log.i(TAG, "closeTab " + i + ", " + m + ", " + mTabs);
		final FragmentTransaction ft = childFragmentManager.beginTransaction();
		SlidingTabsFragment.PagerItem pi;
		if (mTabs.size() > 1) {
			pi = mTabs.get(0);
			ft.remove(pi.fakeFrag);
			pi.fakeFrag = null;
			pi = mTabs.get(mTabs.size() - 1);
			ft.remove(pi.fakeFrag);
			pi.fakeFrag = null;
		}
		for (int j = mTabs2.size() - 1; j >= i; j--) {
			ft.remove(mTabs.remove(j).frag);
		}
		if (mTabs.size() == 1 && mTabs2.size() == 2) {
			pi = mTabs.remove(0);
			ft.remove(pi.frag);
			pi.fakeFrag = null;
		}
		//mTabs.clear();
		pagerAdapter.notifyDataSetChanged();
		ft.commitNow();

		mTabs2.remove(i);

		if (mTabs.size() == 0 && i > 0) {
			mTabs.add(mTabs2.get(0));
		}
		for (int j = i; j < mTabs2.size(); j++) {
			mTabs.add(mTabs2.get(j));
		}
		pagerAdapter.notifyDataSetChanged();
		mTabs2.clear();
		notifyTitleChange();
		int currentItem = i <= mTabs.size() - 1 && mTabs.size() > 1 ? i + 1: mTabs.size() == 1 ? 0 : i;
		mViewPager.setCurrentItem(i);
		final ActionBar actionBar = ((TextEditorActivity)getActivity()).getSupportActionBar();
		if (actionBar != null) {
			final TextFrag textFrag = (TextFrag)pagerAdapter.getItem(currentItem);
			actionBar.setCustomView(textFrag.mToolbarBase);
			textFrag.mEditor.requestFocus();
		}
	}

	public void closeCurTab() {
		final Frag main = getCurrentFragment();
		Log.d(TAG, "closeCurTab " + main);
		closeTab(main);
	}

	public void closeOtherTabs() {
		final Frag curFrag = getCurrentFragment();
		Log.d(TAG, "closeOtherTabs " + curFrag);
		final int size = mTabs.size();
		final int curIndex = indexOfMTabs(curFrag);
		
		final ArrayList<PagerItem> mTabs2 = new ArrayList<PagerItem>(mTabs);

		final FragmentTransaction ft = childFragmentManager.beginTransaction();
		SlidingTabsFragment.PagerItem pi = mTabs.get(0);
		ft.remove(pi.fakeFrag);
		pi.fakeFrag = null;
		pi = mTabs.get(size - 1);
		ft.remove(pi.fakeFrag);
		pi.fakeFrag = null;
		for (int j = 0; j < size; j++) {
			ft.remove(mTabs.remove(0).frag);
		}
		pagerAdapter.notifyDataSetChanged();
		ft.commitNow();

		mTabs.add(mTabs2.get(curIndex));
		mTabs2.clear();
		pagerAdapter.notifyDataSetChanged();
		notifyTitleChange();
		mViewPager.setCurrentItem(0);
		final ActionBar actionBar = ((TextEditorActivity)getActivity()).getSupportActionBar();
		if (actionBar != null) {
			final TextFrag textFrag = (TextFrag)pagerAdapter.getItem(0);
			actionBar.setCustomView(textFrag.mToolbarBase);
			textFrag.mEditor.requestFocus();
		}
	}

	public Frag getCurrentFragment() {
		final int currentItem = mViewPager.getCurrentItem();
		//Log.d(TAG, "getCurrentFragment = " + currentItem + ", " + side + ", " + mTabs);
		return pagerAdapter.getItem(currentItem);
	}

	public int indexOfMTabs(final Frag frag) {
		int i = 0;
		for (PagerItem pi : mTabs) {
			//Log.d(TAG, "indexOf frag " + frag + ", pi.frag " + pi.frag);
			if (frag == pi.frag) {
				return i;
			} else {
				i++;
			}
		}
		return -1;
	}

//	@Override
//	public void onResume() {
//		Log.d(TAG, "onResume");
//		super.onResume();
//		notifyTitleChange();
//	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//Log.d(TAG, "onSaveInstanceState1 " + outState + ", " + childFragmentManager.getFragments());
		try {
			if (mTabs != null && mTabs.size() > 0) {
				int i = 0;
				for (PagerItem pi : mTabs) {
					Log.d(TAG, "onSaveInstanceState pi.frag.getTag() " + pi.frag.getTag() + ", " + pi);
					//childFragmentManager.putFragment(outState, "tabb" + i++, pi.frag);
					outState.putString(i++ + "", pi.frag.getTag());
					outState.putString(pi.frag.getTag(), pi.frag.currentPathTitle);
				}
				if (mTabs.size() > 1) {
					//Log.d(TAG, "fakeStart 0 tag" + mTabs.get(0).fakeFrag.getTag());
					outState.putString("fake0", mTabs.get(0).fakeFrag.getTag());
					//Log.d(TAG, "fakeEnd tag  " + mTabs.get(mTabs.size()-1).fakeFrag.getTag());
					outState.putString("fakeEnd", mTabs.get(mTabs.size() - 1).fakeFrag.getTag());
				}
			}
			outState.putInt("pos", mViewPager.getCurrentItem());
		} catch (Exception e) {
			// Logger.log(e,"puttingtosavedinstance",getActivity());
			e.printStackTrace();
		}
		//Log.d(TAG, "onSaveInstanceState2 " + outState + ", " + childFragmentManager);
	}

//	@Override 
//	public void onDestroyView() {
//		//mViewPager.setAdapter(null);
//		super.onDestroyView();
//	}
	
	public void notifyTitleChange() {
		mSlidingHorizontalScroll.setViewPager(mViewPager);
	}

	public void setCurrentItem(final int pos, final boolean smooth) {
		mViewPager.setCurrentItem(pos, smooth);
	}

	void addPagerItem(final Frag frag1) {
		mTabs.add(new PagerItem(frag1));
	}

	private class PagerItem implements Parcelable {
		private static final String TAG = "PagerItem";
		private final Frag frag;
		private Frag fakeFrag;

		private PagerItem(final Frag frag1) {
			//Log.d(TAG, "tag=" + frag1.getTag() + ", " + frag1);
			this.frag = frag1;
			this.frag.slidingTabsFragment = SlidingTabsFragment.this;
		}

		protected PagerItem(Parcel in) {
			frag = (Frag) in.readSerializable();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeSerializable(frag);
		}

		public final Parcelable.Creator<PagerItem> CREATOR = new Parcelable.Creator<PagerItem>() {
			public PagerItem createFromParcel(Parcel in) {
				return new PagerItem(in);
			}

			public PagerItem[] newArray(int size) {
				return new PagerItem[size];
			}
		};

		@Override
		public Object clone() {
			return new PagerItem(frag.clone(true));
		}

		/**
		 * @return A new {@link Fragment} to be displayed by a {@link ViewPager}
		 */
//		private Frag createFragment(SlidingTabsFragment s) {
//			Log.d(TAG, "createFragment() " + frag);
////			if (frag == null) {
////				//frag = ContentFragment.newInstance(s, dir, suffix, multi, bundle);
////				frag = new Frag();
//			frag.slidingTabsFragment = s;
//			//}
////			if (fakeFrag != null) {
////				fakeFrag.clone(frag);
////			}
//			return frag;
//		}

		private Frag createFakeFragment() {
			Log.d(TAG, "createFakeFragment() fakeFrag " + fakeFrag + ", frag " + frag);
//			if (frag == null && fakeFrag == null) {
//				//fakeFrag = ContentFragment.newInstance(s, dir, suffix, multi, bundle);
//				fakeFrag = frag.clone();//createFragment(s).clone();
//			} else 
			if (fakeFrag == null) {
				//fakeFrag = ContentFragment.newOriFakeInstance(frag);
				fakeFrag = frag.clone(true);
			} else if (fakeFrag != null && frag != null) {
				fakeFrag.clone(frag, true);
				//fakeFrag.refreshDirectory();
			}
			//fakeFrag.slidingTabsFragment = s;
			return fakeFrag;
		}

		public String getTitle() {
			return frag.getTitle();
		}

		@Override
		public String toString() {
			return "frag=" + frag + ", fakeFrag=" + fakeFrag;
		}
	}

	public class PagerAdapter extends FragmentPagerAdapter {
		
		PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Frag getItem(final int position) {
			final int size = mTabs.size();
			//Log.d(TAG, "getItem " + position + "/" + size + ", " + side);
			if (size > 1) {
				if (position == 0) {
					return mTabs.get(size - 1).createFakeFragment();
				} else if (position == size + 1) {
					return mTabs.get(0).createFakeFragment();
				} else {
					return mTabs.get(position - 1).frag;
				}
			} else {
				return mTabs.get(0).frag;
			}
		}

		@Override
		public int getCount() {
			final int size = mTabs.size();
			if (size > 1) {
				return size + 2;
			} else {
				return size;
			}
		}

		@Override
		public CharSequence getPageTitle(final int position) {
			final int size = mTabs.size();
			if (size > 1) {
				if (position == 0 || position == size + 1) {
					return "";
				} else {
					return mTabs.get(position - 1).getTitle();
				}
			} else {
				return mTabs.get(position).getTitle();
			}
		}

		@Override
		public int getItemPosition(final Object object) {
			for (PagerItem pi : mTabs) {
				if (pi.frag == object) {
					//Log.d(TAG, "getItemPosition POSITION_UNCHANGED" + ", " + object);
					return POSITION_UNCHANGED;
				}
			}
			//Log.d(TAG, "getItemPosition POSITION_NONE" + ", " + object);
			return POSITION_NONE;
		}
	}

}
