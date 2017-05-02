/*****************************************************************************
 *
 *                      SINEE PROPRIETARY INFORMATION
 *
 *          The information contained herein is proprietary to Sinee
 *           and shall not be reproduced or disclosed in whole or in part
 *                    or used for any design or manufacture
 *              without direct written authorization from Sinee.
 *
 *            Copyright (c) 2012 by Sinee.  All rights reserved.
 *
 *****************************************************************************/
package com.prj.sdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * @author Action Tan
 * @date May 17, 2012
 */
public class MBGallery extends Gallery {
    public MBGallery(Context context) {
        super(context);
    }

    public MBGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int kEvent;
        if (isScrollingLeft(e1, e2)) { // Check if scrolling left
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        } else { // Otherwise scrolling right
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;
    }

    @Override
    public void setUnselectedAlpha(float unselectedAlpha) {
        unselectedAlpha = 1.0f;
        super.setUnselectedAlpha(unselectedAlpha);
    }

}
