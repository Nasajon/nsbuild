package br.com.nasajon.nsjbuild.model;

public enum BuildTarget {
	compile, build;

	public String toCallString() {
		
		if (this.equals(build)) {
			return "Build";
		} else if (this.equals(compile)) {
			return "default";
		} else {
			return super.toString();
		}
	}
	
	public static boolean isBuildTarget(String text) {
		for (BuildTarget bt : BuildTarget.values()) {
			if (bt.toString().equals(text)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String toSeparatedString(String separator) {
		
		String result = null;
		
		for (BuildTarget bt : BuildTarget.values()) {
			if (result == null) {
				result = bt.toString();
			} else {
				result += separator + bt.toString();
			}
		}
		
		if (result == null) {
			result = "";
		}
		
		return result;
	}
}
