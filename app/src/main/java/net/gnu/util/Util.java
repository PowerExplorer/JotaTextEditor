package net.gnu.util;

public class Util {
	public static String arrayToString(final Object[] list, final boolean number, final String sep) {
		if (list == null || list.length == 0) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		final int len = list.length;
		int c = 0;
		if (!number) {
			for (Object obj : list) {
				sb.append(obj);
				if (++c < len) {
					sb.append(sep);
				}
			}
		} else {
			for (Object obj : list) {
				sb.append(++c).append(": ").append(obj);
				if (c < len) {
					sb.append(sep);
				}
			}
		}
		return sb.toString();
	}
}
