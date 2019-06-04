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

public class SlidingTabsFragment extends Fragment implements TabAction {

	private static final String TAG = "SlidingTabsFragment";
	private FragmentManager childFragmentManager;

	private SlidingHorizontalScroll mSlidingHorizontalScroll;

	private ViewPager mViewPager;
	public PagerAdapter pagerAdapter;
	int pageSelected = 1;

	private ArrayList<PagerItem> mTabs = new ArrayList<PagerItem>();
	public static final enum Side {LEFT, RIGHT, MONO};
	Side side;// = Side.LEFT;
	int width;
	
	public static SlidingTabsFragment newInstance(Side side) {
		final SlidingTabsFragment s = new SlidingTabsFragment();
		s.side = side;
		return s;
	}

	@Override
	public String toString() {
		return side + ", pageSelected=" + pageSelected + ", width=" + width;
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
		Log.d(TAG, "onViewCreated side " + side + ", args " + args + ", savedInstanceState " + savedInstanceState);
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
			if (side == null) {
				ft = childFragmentManager.beginTransaction();
			}
			for (int i = 0; i < size; i++) {
				tag = savedInstanceState.getString(i + "");
				frag = (Frag) childFragmentManager.findFragmentByTag(tag);
				if (frag != null) {
					pagerItem = new PagerItem(frag);
					//Log.d(TAG, "onViewCreated frag " + i + ", " + tag + ", " + frag.getTag() + ", " + pagerItem.dir + ", " + frag);
					mTabs.add(pagerItem);
					if (side == null) {
						ft.remove(frag);
					}
				}
			}
			if (firstTag != null) {
				final SlidingTabsFragment.PagerItem get0 = mTabs.get(0);
				get0.fakeFrag = (Frag) childFragmentManager.findFragmentByTag(firstTag);
				get0.fakeFrag.slidingTabsFragment = this;
				final SlidingTabsFragment.PagerItem last = mTabs.get((mTabs.size() - 1));
				last.fakeFrag = (Frag) childFragmentManager.findFragmentByTag(lastTag);
				last.fakeFrag.slidingTabsFragment = this;
				if (side == null) {
					ft.remove(get0.fakeFrag);
					ft.remove(last.fakeFrag);
				}
			}
			if (side == null) {
				ft.commitNow();
				final int s  = savedInstanceState.getInt("side", 0);
				side = (s == Side.LEFT.ordinal()) ? Side.LEFT : ((s == Side.RIGHT.ordinal()) ? Side.RIGHT : Side.MONO);
				width = savedInstanceState.getInt("width", 0);
			}
			
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
		
		final Activity activ = getActivity();
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
					Log.d(TAG, "onPageSelected: " + position + ", mTabs.size() " + size + ", side " + side);
//					if (position==3) {
//						throw new RuntimeException("here");
//					}
					pageSelected = position;
					if (size > 1) {
						if (position == 1 || position == size) {
							final int newpos = position == 1 ? (size - 1) : position == size ? 0 : (position - 1);
							final PagerItem pi = mTabs.get(newpos);
							Log.d(TAG, "onPageSelected: " + position + ", side " + side + ", pi.frag " + pi.frag + ", pi.fakeFrag " + pi.fakeFrag);
							if (pi.fakeFrag != null) {
								pi.fakeFrag.clone(pi.frag, true);
//								if (pi.fakeFrag.status != null) {
//									pi.fakeFrag.status.setBackgroundColor(ExplorerActivity.IN_DATA_SOURCE_2);
//								}
//								if (pi.frag instanceof FileFrag && ((FileFrag)pi.frag).gridLayoutManager != null) {
//									final FileFrag fileFrag = (FileFrag)pi.frag;
//									FileFrag fakeFrag = (FileFrag)pi.fakeFrag;
//									fakeFrag.selectedInList1 = fileFrag.selectedInList1;
//									fakeFrag.tempOriDataSourceL1 = fileFrag.tempOriDataSourceL1;
//									fakeFrag.tempSelectedInList1 = fileFrag.tempSelectedInList1;
//									fakeFrag.status.setVisibility(fileFrag.status.getVisibility());
//									fakeFrag.commands.setVisibility(fileFrag.commands.getVisibility());
//									if (fileFrag.selStatus != null) {
//										fakeFrag.selStatus.setVisibility(fileFrag.selStatus.getVisibility());
//										fakeFrag.rightStatus.setVisibility(fileFrag.rightStatus.getVisibility());
//										fakeFrag.rightStatus.setText(fileFrag.rightStatus.getText());
//									}
//									fakeFrag.selectionStatus.setVisibility(fileFrag.selectionStatus.getVisibility());
//									fakeFrag.selectionStatus.setText(fileFrag.selectionStatus.getText());
//									
//									final int index = fileFrag.gridLayoutManager.findFirstVisibleItemPosition();
//									final View vi = fileFrag.listView.getChildAt(0); 
//									final int top = (vi == null) ? 0 : vi.getTop();
//									fakeFrag.gridLayoutManager.scrollToPositionWithOffset(index, top);
//								}
							} else {
								pi.createFakeFragment();
							}
						}
					}
					final Frag createFragment = pagerAdapter.getItem(position);
					//final Activity activ = getActivity();
					
				}

				@Override
				public void onPageScrollStateChanged(final int state) {
					Log.d(TAG, "onPageScrollStateChanged1 state " + state + ", pageSelected " + pageSelected + ", " + side);
					if (state == 0) {
						final int size = mTabs.size();
						if (pageSelected == 0) {
							mViewPager.setCurrentItem(size, false);
							pageSelected = size;
						} else if (pageSelected == size + 1) {
							mViewPager.setCurrentItem(1, false);
							pageSelected = 1;
						}
					}
					Log.d(TAG, "onPageScrollStateChanged2 state " + state + ", pageSelected " + pageSelected + ", " + side);
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

	void addTab(final String[] previousSelectedStr) {
		mTabs.add(new PagerItem(Frag.getFrag(this, Frag.TYPE.SELECTION, Util.arrayToString(previousSelectedStr, false, "|"))));
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
			//addFrag(main, pagerItem);
			pagerAdapter.notifyDataSetChanged();
			mViewPager.setCurrentItem(pagerAdapter.getCount() - 1);
			notifyTitleChange();
			//main.onPrepareOptionsMenu(((MainActivity)getActivity()).menu);
		} else if (mTabs.size() >= 1) {
			newIntent = true;
		} else {
			mTabs.add(pagerItem);
		}
		Log.d(TAG, "addTextTab2 " + title + ", " + mTabs);
	}

	public void addTab(final Frag.TYPE t, final String path) {
		Log.d(TAG, "addTab TYPE " + t + ", path=" + path + ", mTabs=" + mTabs);
		final PagerItem pagerItem;
		Frag frag = null;
		if (t == null) {
			frag = getCurrentFragment();
			if (getActivity() instanceof TextEditorActivity) {
				final TextFrag main = TextFrag.newInstance(null, "Untitled " + ++TextFrag.no + ".txt", path);
				main.slidingTabsFragment = this;
				pagerItem = new PagerItem(main);
			} else if (frag.type == Frag.TYPE.EXPLORER) {
				final Frag clone = frag.clone(false);
				//Log.e(TAG, "addTab frag " + frag);
				//Log.e(TAG, "addTab clone " + clone);
				pagerItem = new PagerItem(clone);
			} else {
				return;
			}
		} else {
			frag = Frag.getFrag(this, t, path);
			pagerItem = new PagerItem(frag);
		}
		addFrag(frag, pagerItem);
	}

	private void addFrag(Frag frag, PagerItem pagerItem) {
		final FragmentTransaction ft = childFragmentManager.beginTransaction();
		final ArrayList<PagerItem> mTabs2 = new ArrayList<PagerItem>(mTabs);
		final int size = mTabs.size();
		if (size > 1) {
			int currentItem = mViewPager.getCurrentItem();
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

			for (PagerItem pi2 : mTabs2) {
				mTabs.add(pi2);
			}
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
			mViewPager.setCurrentItem(mTabs.size());
		}
		notifyTitleChange();
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
		mViewPager.setCurrentItem(i <= mTabs.size() - 1 && mTabs.size() > 1 ? i + 1: mTabs.size() == 1 ? 0 : i);
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
		final Activity activ = getActivity();
		
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
	}

	public Frag getCurrentFragment() {
		final int currentItem = mViewPager.getCurrentItem();
		//Log.d(TAG, "getCurrentFragment = " + currentItem + ", " + side + ", " + mTabs);
		return pagerAdapter.getItem(currentItem);
	}

	public Frag getFragmentIndex(final int idx) {
		Log.d(TAG, "getContentFragment index " + idx + ", " + side + ", " + mTabs);
		return pagerAdapter.getItem(idx);
	}

	public int indexOfAdapter(final Frag frag) {
		int i = 0;
		for (PagerItem pi : mTabs) {
			//Log.d(TAG, "indexOf frag " + frag + ", pi.frag " + pi.frag);
			if (frag == pi.frag) {
				return mTabs.size() == 1 ? i : i + 1;
			} else {
				i++;
			}
		}
		return -1;
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

	public Frag getFrag(final Frag.TYPE t) {
		for (PagerItem pi : mTabs) {
			//Log.d(TAG, "indexOf frag " + frag + ", pi.frag " + pi.frag);
			if (t == pi.frag.type) {
				return pi.frag;
			} 
		}
		return null;
	}

	public int getFragIndex(final Frag.TYPE t) {
		final int count = pagerAdapter.getCount();
		if (count > 1) {
			for (int i = 1; i < count - 1; i++) {
				if (pagerAdapter.getItem(i).type == t) {
					return i;
				}
			}
			return -1;
		} else {
			return pagerAdapter.getItem(0).type == t ? 0 : -1;
		}
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
					Log.d(TAG, "onSaveInstanceState pi.frag.getTag() " + pi.frag.getTag() + ", " + side + ", " + pi);
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
			outState.putInt("width", width);
			outState.putInt("side", side.ordinal());
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
			return frag.type.ordinal();
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
		int numOfPages = 1;

		PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public float getPageWidth(int position) {
			return 1f / numOfPages;
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
