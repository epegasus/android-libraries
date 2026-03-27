package org.wysaid.nativePort;

public class NativeLibraryLoader {

    public static void load() {
        System.loadLibrary("CGE");
        System.loadLibrary("CGEExt");
        onLoad();
    }

    static native void onLoad();
}
