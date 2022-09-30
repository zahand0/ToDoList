package com.example.todolist.ui.utils

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(private val limitScrollX: Int) : ItemTouchHelper.Callback() {

    // limit of swipe, same as delete button width
    private var currentScrollX = 0
    private var currentScrollXWhenInActive = 0
    private var initXWhenInActive = 0f
    private var firstInActive = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = 0
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return Integer.MAX_VALUE.toFloat()
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return Integer.MAX_VALUE.toFloat()
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            if (dX == 0f) {
                currentScrollX = viewHolder.itemView.scrollX
                firstInActive = true
            }

            if (isCurrentlyActive) {
                // swipe with finger

                var scrollOffset = currentScrollX + (-dX).toInt()
                // limit swipe
                if (scrollOffset > limitScrollX) {
                    // swipe left
                    scrollOffset = limitScrollX
                } else if (scrollOffset < -limitScrollX) {
                    // swipe right
                    scrollOffset = -limitScrollX
                }

                viewHolder.itemView.scrollTo(scrollOffset, 0)

            } else {
                // swipe with auto animation
                if (firstInActive) {
                    firstInActive = false
                    currentScrollXWhenInActive = viewHolder.itemView.scrollX
                    initXWhenInActive = dX
                }

                if (viewHolder.itemView.scrollX in 1 until limitScrollX) {
                    viewHolder.itemView.scrollTo(
                        (currentScrollXWhenInActive * dX / initXWhenInActive).toInt(),
                        0
                    )
                } else
                    if (viewHolder.itemView.scrollX in -limitScrollX + 1 until -1) {
                        viewHolder.itemView.scrollTo(
                            (currentScrollXWhenInActive * dX / initXWhenInActive).toInt(),
                            0
                        )
                    }
            }
        }
    }


    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)

        if (viewHolder.itemView.scrollX > limitScrollX) {
            viewHolder.itemView.scrollTo(limitScrollX, 0)
        } else if (viewHolder.itemView.scrollX < -limitScrollX) {
            viewHolder.itemView.scrollTo(-limitScrollX, 0)
        } else if ((-limitScrollX < viewHolder.itemView.scrollX) && (viewHolder.itemView.scrollX < limitScrollX)) {
            viewHolder.itemView.scrollTo(0, 0)
        }
    }
}