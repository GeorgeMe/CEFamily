package com.zxing.android;

public final class Intents {
	  private Intents() {
	  }

	  public static final class Scan {
		    /**
		     * Optional parameter to specify the id of the camera from which to recognize barcodes.
		     * Overrides the default camera that would otherwise would have been selected.
		     * If provided, should be an int.
		     */
		    public static final String CAMERA_ID = "SCAN_CAMERA_ID";		  
	  }
}
