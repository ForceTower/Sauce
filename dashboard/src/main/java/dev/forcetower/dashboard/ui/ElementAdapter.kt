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

package dev.forcetower.dashboard.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.forcetower.core.extensions.inflate
import dev.forcetower.dashboard.R
import dev.forcetower.dashboard.databinding.ItemNextClassResumeBinding

class ElementAdapter : RecyclerView.Adapter<ElementAdapter.ElementHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementHolder {
        return when (viewType) {
            R.layout.item_next_class_resume -> ElementHolder.NextClassElement(parent.inflate(R.layout.item_next_class_resume))
            else -> throw IllegalStateException("Invalid viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ElementHolder, position: Int) {

    }

    override fun getItemCount() = 1

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_next_class_resume
    }

    sealed class ElementHolder(view: View) : RecyclerView.ViewHolder(view) {
        class NextClassElement(val binding: ItemNextClassResumeBinding) : ElementHolder(binding.root)
    }
}