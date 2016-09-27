/*
 *  Copyright (C) 2014 The AppCan Open Source Project.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.zywx.wbpalmstar.plugin.uexsegmentcontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragGridView extends GridView {
    private int itemWidth;
    private int itemHeight;
    private int itemTotalCount;
    private int nColumns = 4;
    private int nRows;
    private int Remainder;
    public int downX;
    public int downY;
    private int windowX;
    private int windowY;
    private int win_view_x;
    private int win_view_y;
    private int dragOffsetX;
    private int dragOffsetY;
    private int startPosition;
    private int dragPosition;
    private int dropPosition;
    private int holdPosition;
    private String LastAnimationID;
    private View dragImageView = null;
    private View dragItemView = null;
    private boolean isMoving = false;
    private double dragScale = 1.2D;
    private WindowManager windowManager = null;
    private WindowManager.LayoutParams windowParams = null;
    private boolean edit = false;
    private OnItemEditLongClickListener onItemEditLongClickListener;

    public DragGridView(Context context) {
        super(context);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void isEditFlag(boolean edit) {
        this.edit = edit;
    }

    public void setOnItemEditLongClickListener(
            OnItemEditLongClickListener onItemEditLongClickListener) {
        this.onItemEditLongClickListener = onItemEditLongClickListener;
    }

    private void onDrag(int x, int y, int rawx, int rawy) {
        if (dragImageView != null) {
            windowParams.alpha = 0.6f;
            windowParams.x = rawx - win_view_x;
            windowParams.y = rawy - win_view_y;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
    }

    private void onDrop(int x, int y) {
        int tempPostion = pointToPosition(x, y);
        //GridViewAdapter mDragAdapter = (GridViewAdapter) getAdapter(); 
        if (tempPostion != AdapterView.INVALID_POSITION) {
            dropPosition = tempPostion;
            //mDragAdapter.exchange(dragPosition, dropPosition);
            //mDragAdapter.notifyDataSetInvalidated();
        }
    }


    public void startDrag(Bitmap dragBitmap, int x, int y) {
        stopDrag();
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowParams.x = x - win_view_x;
        windowParams.y = y - win_view_y;
        windowParams.width = (int) (dragScale * dragBitmap.getWidth());
        windowParams.height = (int) (dragScale * dragBitmap.getHeight());
        this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this.windowParams.format = PixelFormat.TRANSLUCENT;
        this.windowParams.windowAnimations = 0;
        ImageView iv = new ImageView(getContext());
        iv.setImageBitmap(dragBitmap);
        windowManager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        windowManager.addView(iv, windowParams);
        dragImageView = iv;
    }

    private void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }

    public Animation getMoveAnimation(float toXValue, float toYValue) {
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0F, Animation.RELATIVE_TO_SELF,
                toXValue, Animation.RELATIVE_TO_SELF, 0.0F,
                Animation.RELATIVE_TO_SELF, toYValue);
        mTranslateAnimation.setFillAfter(true);
        mTranslateAnimation.setDuration(300L);
        return mTranslateAnimation;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_UP:
                    stopDrag();
                    onDrop(x, y);
                    requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_MOVE:
                    onDrag(x, y, (int) ev.getRawX(), (int) ev.getRawY());
                    if (!isMoving) {
                        onMove(x, y);
                    }
                    if (pointToPosition(x, y) != AdapterView.INVALID_POSITION) {
                        break;
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    downX = (int) ev.getX();
                    windowX = (int) ev.getX();
                    downY = (int) ev.getY();
                    windowY = (int) ev.getY();
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    public void onMove(int x, int y) {
        int dposition = pointToPosition(x, y);
        if (dposition == -1 || dposition < 1) {
            return;
        }
        isMoving = true;
        dropPosition = dposition;
        if (dragPosition != startPosition) {
            dragPosition = startPosition;
        }
        int moveCount;
        if ((dragPosition == startPosition) || (dragPosition != dropPosition)) {
            moveCount = dropPosition - dragPosition;
        } else {
            moveCount = 0;
        }
        if (moveCount == 0) {
            isMoving = false;
            return;
        }
        int moveCount_abs = Math.abs(moveCount);
        //ViewGroup viewGroup = (ViewGroup) getChildAt(dragPosition);
        //viewGroup.setVisibility(View.INVISIBLE);//隐藏拖动的视图
        float to_x = 1;
        float to_y;
        float x_vlaue = 1.0f;
        float y_vlaue = 1.0f;
        for (int i = 0; i < moveCount_abs; i++) {
            to_x = x_vlaue;
            to_y = y_vlaue;
            if (moveCount > 0) {
                holdPosition = dragPosition + i + 1;
                if (dragPosition / nColumns == holdPosition / nColumns) {
                    to_x = -x_vlaue;
                    to_y = 0;
                } else if (holdPosition % 4 == 0) {
                    to_x = 3 * x_vlaue;
                    to_y = -y_vlaue;
                } else {
                    to_x = -x_vlaue;
                    to_y = 0;
                }
            } else {
                holdPosition = dragPosition - i - 1;
                if (dragPosition / nColumns == holdPosition / nColumns) {
                    to_x = x_vlaue;
                    to_y = 0;
                } else if ((holdPosition + 1) % 4 == 0) {
                    to_x = -3 * x_vlaue;
                    to_y = y_vlaue;
                } else {
                    to_x = x_vlaue;
                    to_y = 0;
                }
            }
            final ViewGroup moveGroup = (ViewGroup) getChildAt(holdPosition);
            Animation moveAnimation = getMoveAnimation(to_x, to_y);
            if (holdPosition == dropPosition) {
                LastAnimationID = moveAnimation.toString();
            }
            moveAnimation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    isMoving = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    moveGroup.clearAnimation();
                    if (animation.toString().equalsIgnoreCase(LastAnimationID)) {
                        GridViewAdapter adapter = (GridViewAdapter) getAdapter();
                        adapter.exchange(startPosition, dropPosition);
                        //adapter.notifyDataSetInvalidated();
                        startPosition = dropPosition;
                        dragPosition = dropPosition;
                        isMoving = false;
                    }
                }
            });
            moveGroup.startAnimation(moveAnimation);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                windowX = (int) ev.getX();
                windowY = (int) ev.getY();
                setOnItemDragListener(ev);
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setOnItemDragListener(final MotionEvent ev) {
        setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (edit) {
                    int x = (int) ev.getX();//点击的x位置
                    int y = (int) ev.getY();//点击的y位置
                    startPosition = position;
                    dragPosition = position;
                    if (position <= 0) {
                        return false;
                    }
                    ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
                    itemWidth = dragViewGroup.getWidth();//拖动item的宽
                    itemHeight = dragViewGroup.getHeight();//高
                    itemTotalCount = DragGridView.this.getCount();
                    int rows = itemTotalCount / nColumns;
                    Remainder = itemTotalCount % nColumns;
                    if (Remainder != 0) {
                        nRows = rows + 1;
                    } else {
                        nRows = rows;
                    }
                    if (dragPosition != AdapterView.INVALID_POSITION) {
                        win_view_x = windowX - dragViewGroup.getLeft();//得到x半径
                        win_view_y = windowY - dragViewGroup.getTop();//得到y半径
                        dragOffsetX = (int) (ev.getRawX() - x);//控件偏移量x
                        dragOffsetY = (int) (ev.getRawY() - y);//控件偏移量y
                        dragItemView = dragViewGroup;
                        dragViewGroup.destroyDrawingCache();
                        dragViewGroup.setDrawingCacheEnabled(true);
                        Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup.getDrawingCache());//得到控件缩略图
                        startDrag(dragBitmap, (int) ev.getRawX(), (int) ev.getRawY());
                        isMoving = false;
                        requestDisallowInterceptTouchEvent(true);
                    }
                    return false;
                } else {
                    if (onItemEditLongClickListener != null) {
                        onItemEditLongClickListener.onItemEditLongClick();
                    }
                    return false;
                }
            }
        });
    }

    public interface OnItemEditLongClickListener {
        void onItemEditLongClick();
    }
}  