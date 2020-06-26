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

package org.kiwix.kiwixmobile.main

import android.content.Intent
import android.view.ActionMode
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import org.kiwix.kiwixmobile.core.base.BaseFragmentActivityExtensions
import org.kiwix.kiwixmobile.core.base.BaseFragmentActivityExtensions.Super
import org.kiwix.kiwixmobile.core.base.BaseFragmentActivityExtensions.Super.ShouldCall
import org.kiwix.kiwixmobile.core.main.KiwixWebView
import org.kiwix.kiwixmobile.core.main.WebViewProvider

class NavigationHostFragment : NavHostFragment(), WebViewProvider, BaseFragmentActivityExtensions {
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    childFragmentManager.fragments.forEach { it.onActivityResult(requestCode, resultCode, data) }
  }

  override fun getCurrentWebView(): KiwixWebView? {
    return childFragmentManager.fragments.filterIsInstance<WebViewProvider>().firstOrNull()
      ?.getCurrentWebView()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    childFragmentManager.fragments.forEach {
      it.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  override fun onActionModeStarted(
    actionMode: ActionMode,
    activity: AppCompatActivity
  ): Super {
    var result = ShouldCall
    childFragmentManager.fragments.filterIsInstance<BaseFragmentActivityExtensions>().forEach {
      result = it.onActionModeStarted(actionMode, activity)
    }
    return result
  }

  override fun onActionModeFinished(
    actionMode: ActionMode,
    activity: AppCompatActivity
  ): Super {
    var result = ShouldCall
    childFragmentManager.fragments.filterIsInstance<BaseFragmentActivityExtensions>().forEach {
      result = it.onActionModeFinished(actionMode, activity)
    }
    return result
  }

  override fun onBackPressed(activity: AppCompatActivity): Super {
    var result = ShouldCall
    childFragmentManager.fragments.filterIsInstance<BaseFragmentActivityExtensions>().forEach {
      result = it.onBackPressed(activity)
    }
    return result
  }

  override fun onNewIntent(
    intent: Intent,
    activity: AppCompatActivity
  ): Super {
    var result = ShouldCall
    childFragmentManager.fragments.filterIsInstance<BaseFragmentActivityExtensions>().forEach {
      result = it.onNewIntent(intent, activity)
    }
    return result
  }

  override fun onCreateOptionsMenu(
    menu: Menu,
    activity: AppCompatActivity
  ): Super {
    var result = ShouldCall
    childFragmentManager.fragments.filterIsInstance<BaseFragmentActivityExtensions>().forEach {
      result = it.onCreateOptionsMenu(menu, activity)
    }
    return result
  }
}