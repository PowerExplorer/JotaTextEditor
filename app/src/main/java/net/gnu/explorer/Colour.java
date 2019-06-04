package net.gnu.explorer;

public class Colour {

	
	public static final String KEY_INTENT_COMPRESS = "showCompressFrag";
	public static final String KEY_INTENT_DECOMPRESS = "showDecompressFrag";
	
	
	public static int SELECTED_IN_LIST = 0xFFFEF8BA;//0xFFFFF0A0
	public static int BASE_BACKGROUND = 0xFFFFFFE8;
	public static int IN_DATA_SOURCE_2 = 0xFFFFF8D9;
	public static int IS_PARTIAL = 0xFFFFF0CF;
	public static final int LIGHT_GREY = 0xff909090;
	public static int TEXT_COLOR = 0xff404040;
	public static int DOT = 0xffa0a0a0;
	public static int DIVIDER_COLOR = 0xff707070;
	public static int TEXT_COLOR_LIGHT = 0xfff0f0f0;
	public static int BASE_BACKGROUND_DARK = 0xff303030;
	public static final String PREVIOUS_SELECTED_FILES = "net.gnu.explorer.selectedFiles";
	
	public static final String ALL_SUFFIX = "*";
	public static final String ALL_SUFFIX_TITLE = "Select Files/Folders";
	public static final String ZIP_SUFFIX = ".zpaq; .7z; .bz2; .bzip2; .tbz2; .tbz; .001; .gz; .gzip; .tgz; .tar; .dump; .swm; .xz; .txz; .zip; .zipx; .jar; .apk; .xpi; .odt; .ods; .odp; .docx; .xlsx; .pptx; .epub; .apm; .ar; .a; .deb; .lib; .arj; .cab; .chm; .chw; .chi; .chq; .msi; .msp; .doc; .xls; .ppt; .cpio; .cramfs; .dmg; .ext; .ext2; .ext3; .ext4; .img; .fat; .hfs; .hfsx; .hxs; .hxi; .hxr; .hxq; .hxw; .lit; .ihex; .iso; .lzh; .lha; .lzma; .mbr; .mslz; .mub; .nsis; .ntfs; .rar; .r00; .rpm; .ppmd; .qcow; .qcow2; .qcow2c; .squashfs; .udf; .iso; .scap; .uefif; .vdi; .vhd; .vmdk; .wim; .esd; .xar; .pkg; .z; .taz";
	public static final String ZIP_TITLE = "Compressed file (" + ZIP_SUFFIX + ")";
	public static final int FILES_REQUEST_CODE = 13;
	public static final int SAVETO_REQUEST_CODE = 14;
	public static final int STARDICT_REQUEST_CODE = 16;
	public static final boolean MULTI_FILES = true;
	public static final int OUTLINE_REQUEST_CODE = 15;
	
	/**
	 * Select multi files and folders
	*/
	public static final String EXTRA_MULTI_SELECT = "org.openintents.extra.MULTI_SELECT";//"multiFiles";
	
    public static final String ACTION_PICK_FILE = "org.openintents.action.PICK_FILE";

    public static final String ACTION_PICK_DIRECTORY = "org.openintents.action.PICK_DIRECTORY";

    public static final String ACTION_MULTI_SELECT = "org.openintents.action.MULTI_SELECT";

    public static final String ACTION_SEARCH_STARTED = "org.openintents.action.SEARCH_STARTED";

    public static final String ACTION_SEARCH_FINISHED = "org.openintens.action.SEARCH_FINISHED";

    public static final String EXTRA_TITLE = "org.openintents.extra.TITLE";

    public static final String EXTRA_BUTTON_TEXT = "org.openintents.extra.BUTTON_TEXT";

    public static final String EXTRA_WRITEABLE_ONLY = "org.openintents.extra.WRITEABLE_ONLY";

    public static final String EXTRA_SEARCH_INIT_PATH = "org.openintents.extra.SEARCH_INIT_PATH";

    public static final String EXTRA_SEARCH_QUERY = "org.openintents.extra.SEARCH_QUERY";
    //public static final String EXTRA_DIR_PATH = "org.openintents.extra.DIR_PATH";
    public static final String EXTRA_ABSOLUTE_PATH = "org.openintents.extra.ABSOLUTE_PATH";
    public static final String EXTRA_FILTER_FILETYPE = "org.openintents.extra.FILTER_FILETYPE";
    public static final String EXTRA_FILTER_MIMETYPE = "org.openintents.extra.FILTER_MIMETYPE";
    public static final String EXTRA_DIRECTORIES_ONLY = "org.openintents.extra.DIRECTORIES_ONLY";
    public static final String EXTRA_DIALOG_FILE_HOLDER = "org.openintents.extra.DIALOG_FILE";
    public static final String EXTRA_IS_GET_CONTENT_INITIATED = "org.openintents.extra.ENABLE_ACTIONS";
    public static final String EXTRA_FILENAME = "org.openintents.extra.FILENAME";
    public static final String EXTRA_FROM_OI_FILEMANAGER = "org.openintents.extra.FROM_OI_FILEMANAGER";
	
	String dir = "";
	
	public static final String DOC_FILES_SUFFIX =
	".doc; .docx; .txt; .html; .odt; .rtf; .epub; .fb2; .pdf; .pps; .ppt; .pptx; .xls; .xlsx; " +
	".ods; .odp; .pub; .vsd; .htm; .xml; .xhtml; .java; .c; .cpp; .h; .md; .lua; .sh; bat; .list; .depend; .js; .jsp; .mk; .config; .configure; .machine; .asm; .css; .desktop; .inc; .shtm; .shtml; .i; .plist; .pro; .py; .s; .xpm; .ini";
	public static final String TRANSLATE_FILES_SUFFIX =
	".doc; .docx; .txt; .html; .odt; .rtf; .epub; .fb2; .pdf; .pps; .ppt; .pptx; .xls; .xlsx; " +
	".ods; .odp; .htm; .xhtml; .shtm; .shtml;";
	public static final String ORI_SUFFIX_TITLE = "Origin Document (" + DOC_FILES_SUFFIX + ")";
	public static final String MODI_SUFFIX_TITLE = "Modified Document (" + DOC_FILES_SUFFIX + ")";
	public static final String TXT_SUFFIX = ".txt";
	public static final String TXT_SUFFIX_TITLE = "Dictionary Text (" + TXT_SUFFIX + ")";
	public static final String IFO_SUFFIX = ".ifo";
	public static final String IFO_SUFFIX_TITLE = "Dictionary File (" + IFO_SUFFIX + ")";
	
	public final static int REQUEST_CODE_PREFERENCES = 1, REQUEST_CODE_SRV_FORM = 2, REQUEST_CODE_OPEN = 3;
    public final static int FIND_ACT = 1017, SMB_ACT = 2751, FTP_ACT = 4501, SFTP_ACT = 2450;
    public final static String PREF_RESTORE_ACTION = "com.ghostsq.commander.PREF_RESTORE";

	final static String DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><script src=\"file:///android_asset/run_prettify.js?skin=sons-of-obsidian\"></script></head><body bgcolor=\"#000000\"><pre class=\"prettyprint \">";
	final static String END_PRE = "</pre></body></html>";
	
	
	
	public static final int INTENT_WRITE_REQUEST_CODE = 1;

	public static final String KEY_PREF_OTG = "uri_usb_otg";
	public static final String KEY_INTENT_PROCESS_VIEWER = "openprocesses";
	public static final String TAG_INTENT_FILTER_FAILED_OPS = "failedOps";
    public static final String TAG_INTENT_FILTER_GENERAL = "general_communications";
    public static final String ARGS_KEY_LOADER = "loader_cloud_args_service";
	
	
}
