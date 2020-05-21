package org.kiwix.kiwixmobile.core.history.viewmodel.effects

/*
 * Kiwix Android
 * Copyright (c) 2020 Kiwix <android.kiwix.org>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import org.kiwix.kiwixmobile.core.base.SideEffect

data class ToggleShowAllHistorySwitchAvailability(
  private val showAllHistorySwitch: Switch
) : SideEffect<Unit> {
  override fun invokeWith(activity: AppCompatActivity) {
    showAllHistorySwitch.isEnabled = !showAllHistorySwitch.isEnabled
  }
}