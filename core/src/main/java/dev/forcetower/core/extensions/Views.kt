/*
 * This file is part of the UNES Open Source Project.
 * UNES is licensed under the GNU GPLv3.
 *
 * Copyright (c) 2019. João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.forcetower.core.extensions

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

fun View.inflater(): LayoutInflater = LayoutInflater.from(context)

inline fun <reified T : ViewDataBinding> ViewGroup.inflate(@LayoutRes res: Int, attachToParent: Boolean = false): T {
    val inflater = inflater()
    return DataBindingUtil.inflate(inflater, res, this, attachToParent)
}

inline fun <reified T : ViewDataBinding> LayoutInflater.inflate(@LayoutRes res: Int): T {
    return DataBindingUtil.inflate(this, res, null, false)
}


fun RecyclerView.clearDecorations() {
    if (itemDecorationCount > 0) {
        for (i in itemDecorationCount - 1 downTo 0) {
            removeItemDecorationAt(i)
        }
    }
}

fun Context.getPixelsFromDp(dp: Int): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)

fun Context.calculateNoOfColumns(columnWidthDp: Int, space: Int = 4): Int {
    val spacing = getPixelsFromDp(space)
    val displayMetrics = resources.displayMetrics
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    return (screenWidthDp / (columnWidthDp + spacing) + 0.5).toInt()
}

fun Context.getAttribute(attr: Int): Int {
    val typedValue = TypedValue()
    val theme = theme
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

fun View.closeKeyboard() {
    val service = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    service?.hideSoftInputFromWindow(windowToken, 0)
}

fun View.doOnApplyWindowInsets(f: (View, WindowInsetsCompat, ViewPaddingState) -> Unit) {
    // Create a snapshot of the view's padding state
    val paddingState = createStateForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, paddingState)
        insets
    }
    requestApplyInsetsWhenAttached()
}

/**
 * Call [View.requestApplyInsets] in a safe away. If we're attached it calls it straight-away.
 * If not it sets an [View.OnAttachStateChangeListener] and waits to be attached before calling
 * [View.requestApplyInsets].
 */
fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

private fun createStateForView(view: View) = ViewPaddingState(view.paddingLeft,
    view.paddingTop, view.paddingRight, view.paddingBottom, view.paddingStart, view.paddingEnd)

data class ViewPaddingState(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val start: Int,
    val end: Int
)