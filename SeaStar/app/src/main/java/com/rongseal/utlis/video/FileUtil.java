package com.rongseal.utlis.video;

import java.util.Arrays;
import java.util.HashSet;

public class FileUtil {
	public static final String[] VIDEO_EXTENSIONS = { "264", "3g2", "3gp",
			"3gp2", "3gpp", "3gpp2", "3mm", "3p2", "60d", "aep", "ajp", "amv",
			"amx", "arf", "asf", "asx", "avb", "avd", "avi", "avs", "avs",
			"axm", "bdm", "bdmv", "bik", "bin", "bix", "bmk", "box", "bs4",
			"bsf", "byu", "camre", "clpi", "cpi", "cvc", "d2v", "d3v", "dat",
			"dav", "dce", "dck", "ddat", "dif", "dir", "divx", "dlx", "dmb",
			"dmsm", "dmss", "dnc", "dpg", "dream", "dsy", "dv", "dv-avi",
			"dv4", "dvdmedia", "dvr-ms", "dvx", "dxr", "dzm", "dzp", "dzt",
			"evo", "eye", "f4p", "f4v", "fbr", "fbr", "fbz", "fcp", "flc",
			"flh", "fli", "flv", "flx", "gl", "grasp", "gts", "gvi", "gvp",
			"hdmov", "hkm", "ifo", "imovi", "imovi", "iva", "ivf", "ivr",
			"ivs", "izz", "izzy", "jts", "lsf", "lsx", "m15", "m1pg", "m1v",
			"m21", "m21", "m2a", "m2p", "m2t", "m2ts", "m2v", "m4e", "m4u",
			"m4v", "m75", "meta", "mgv", "mj2", "mjp", "mjpg", "mkv", "mmv",
			"mnv", "mod", "modd", "moff", "moi", "moov", "mov", "movie",
			"mp21", "mp21", "mp2v", "mp4", "mp4v", "mpe", "mpeg", "mpeg4",
			"mpf", "mpg", "mpg2", "mpgin", "mpl", "mpls", "mpv", "mpv2", "mqv",
			"msdvd", "msh", "mswmm", "mts", "mtv", "mvb", "mvc", "mvd", "mve",
			"mvp", "mxf", "mys", "ncor", "nsv", "nvc", "ogm", "ogv", "ogx",
			"osp", "par", "pds", "pgi", "piv", "playlist", "pmf", "prel",
			"pro", "prproj", "psh", "pva", "pvr", "pxv", "qt", "qtch", "qtl",
			"qtm", "qtz", "rcproject", "rdb", "rec", "rm", "rmd", "rmp", "rms",
			"rmvb", "roq", "rp", "rts", "rts", "rum", "rv", "sbk", "sbt",
			"scm", "scm", "scn", "sec", "seq", "sfvidcap", "smil", "smk",
			"sml", "smv", "spl", "ssm", "str", "stx", "svi", "swf", "swi",
			"swt", "tda3mt", "tivo", "tix", "tod", "tp", "tp0", "tpd", "tpr",
			"trp", "ts", "tvs", "vc1", "vcr", "vcv", "vdo", "vdr", "veg",
			"vem", "vf", "vfw", "vfz", "vgz", "vid", "viewlet", "viv", "vivo",
			"vlab", "vob", "vp3", "vp6", "vp7", "vpj", "vro", "vsp", "w32",
			"wcp", "webm", "wm", "wmd", "wmmp", "wmv", "wmx", "wp3", "wpl",
			"wtv", "wvx", "xfl", "xvid", "yuv", "zm1", "zm2", "zm3", "zmv" };

	private static final HashSet<String> vHashSet = new HashSet<String>(
			Arrays.asList(VIDEO_EXTENSIONS));

	/**
	 * 检测是否是视频文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isVideo(String path) {
		path=getFileExtension(path);
		return vHashSet.contains(path);
	}

	/**
	 * 获取文件后缀名
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileExtension(String path) {
		if (null != path) {
			// 后缀点 的位置
			int dex = path.lastIndexOf(".");
			// 截取后缀名
			return path.substring(dex + 1);
		}
		return null;
	}
}
