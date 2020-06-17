/*
 * Kiwix Android
 * Copyright (c) 2019 Kiwix <android.kiwix.org>
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
package org.kiwix.kiwixmobile.library

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.zim_list.file_management_no_files
import kotlinx.android.synthetic.main.zim_list.zim_swiperefresh
import kotlinx.android.synthetic.main.zim_list.zimfilelist
import org.kiwix.kiwixmobile.R
import org.kiwix.kiwixmobile.core.base.BaseActivity
import org.kiwix.kiwixmobile.core.base.BaseFragment
import org.kiwix.kiwixmobile.core.extensions.ActivityExtensions.viewModel
import org.kiwix.kiwixmobile.core.extensions.toast
import org.kiwix.kiwixmobile.core.utils.LanguageUtils
import org.kiwix.kiwixmobile.core.utils.REQUEST_STORAGE_PERMISSION
import org.kiwix.kiwixmobile.core.utils.SharedPreferenceUtil
import org.kiwix.kiwixmobile.core.zim_manager.fileselect_view.adapter.BookOnDiskDelegate
import org.kiwix.kiwixmobile.core.zim_manager.fileselect_view.adapter.BooksOnDiskAdapter
import org.kiwix.kiwixmobile.kiwixActivityComponent
import org.kiwix.kiwixmobile.zim_manager.ZimManageViewModel
import org.kiwix.kiwixmobile.zim_manager.fileselect_view.FileSelectListState
import javax.inject.Inject

private const val WAS_IN_ACTION_MODE = "WAS_IN_ACTION_MODE"

class LocalLibraryFragment : BaseFragment() {

  @Inject override lateinit var sharedPreferenceUtil: SharedPreferenceUtil
  @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

  private var actionMode: ActionMode? = null
  private val disposable = CompositeDisposable()

  private val zimManageViewModel by lazy {
    requireActivity().viewModel<ZimManageViewModel>(viewModelFactory)
  }
  private val bookDelegate: BookOnDiskDelegate.BookDelegate by lazy {
    BookOnDiskDelegate.BookDelegate(sharedPreferenceUtil,
      { offerAction(ZimManageViewModel.FileSelectActions.RequestOpen(it)) },
      { offerAction(ZimManageViewModel.FileSelectActions.RequestMultiSelection(it)) },
      { offerAction(ZimManageViewModel.FileSelectActions.RequestSelect(it)) })
  }

  private val booksOnDiskAdapter: BooksOnDiskAdapter by lazy {
    BooksOnDiskAdapter(bookDelegate, BookOnDiskDelegate.LanguageDelegate)
  }

  override fun inject(baseActivity: BaseActivity) {
    baseActivity.kiwixActivityComponent.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    LanguageUtils(requireActivity())
      .changeFont(requireActivity().layoutInflater, sharedPreferenceUtil)
    return inflater.inflate(R.layout.zim_list, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    zim_swiperefresh.setOnRefreshListener(::requestFileSystemCheck)
    zimfilelist.run {
      adapter = booksOnDiskAdapter
      layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
      setHasFixedSize(true)
    }
    zimManageViewModel.fileSelectListStates.observe(viewLifecycleOwner, Observer(::render))
    disposable.add(sideEffects())
    zimManageViewModel.deviceListIsRefreshing.observe(viewLifecycleOwner, Observer {
      zim_swiperefresh.isRefreshing = it!!
    })
    if (savedInstanceState != null && savedInstanceState.getBoolean(WAS_IN_ACTION_MODE)) {
      zimManageViewModel.fileSelectActions.offer(ZimManageViewModel.FileSelectActions.RestartActionMode)
    }

    disposable.add(zimManageViewModel.libraryTabIsVisible.subscribe { finishActionMode() })
  }

  private fun finishActionMode() {
    actionMode?.finish()
  }

  private fun sideEffects() = zimManageViewModel.sideEffects.subscribe(
    {
      val effectResult = it.invokeWith(requireActivity() as AppCompatActivity)
      if (effectResult is ActionMode) {
        actionMode = effectResult
      }
    }, Throwable::printStackTrace
  )

  private fun render(state: FileSelectListState) {
    val items = state.bookOnDiskListItems
    bookDelegate.selectionMode = state.selectionMode
    booksOnDiskAdapter.items = items
    actionMode?.title = String.format("%d", state.selectedBooks.size)
    file_management_no_files.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
  }

  override fun onResume() {
    super.onResume()
    checkPermissions()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean(WAS_IN_ACTION_MODE, actionMode != null)
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable.clear()
  }

  private fun offerAction(action: ZimManageViewModel.FileSelectActions) {
    zimManageViewModel.fileSelectActions.offer(action)
  }

  private fun checkPermissions() {
    if (ContextCompat.checkSelfPermission(
        requireActivity(),
        Manifest.permission.READ_EXTERNAL_STORAGE
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      context.toast(R.string.request_storage)
      requestPermissions(
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        REQUEST_STORAGE_PERMISSION
      )
    } else {
      requestFileSystemCheck()
    }
  }

  private fun requestFileSystemCheck() {
    zimManageViewModel.requestFileSystemCheck.onNext(Unit)
  }
}
