package com.nefi.chainrat.camera;

import java.util.TreeMap;

/**
 * Picture capturing listener
 *
 * @author hzitoun (zitoun.hamed@gmail.com)
 */
public interface PictureCapturingListener {

    void onCaptureDone(final byte[] pictureData);
}
