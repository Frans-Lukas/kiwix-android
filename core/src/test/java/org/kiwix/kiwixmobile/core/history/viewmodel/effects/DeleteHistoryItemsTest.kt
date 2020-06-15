package org.kiwix.kiwixmobile.core.history.viewmodel.effects

import androidx.appcompat.app.AppCompatActivity
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.kiwix.kiwixmobile.core.dao.HistoryDao

internal class DeleteHistoryItemsTest {
  private val historyDao: HistoryDao = mockk()
  val activity: AppCompatActivity = mockk()
  private val item1 = historyItem()
  private val item2 = historyItem()

  @Test
  fun `delete with selected items only deletes the selected items`() {
    item1.isSelected = true
    DeleteHistoryItems(historyState(listOf(item1, item2)), historyDao).invokeWith(activity)
    verify { historyDao.deleteHistory(listOf(item1)) }
  }

  @Test
  fun `delete with no selected items deletes all items`() {
    item1.isSelected = false
    DeleteHistoryItems(historyState(listOf(item1, item2)), historyDao).invokeWith(activity)
    verify { historyDao.deleteHistory(listOf(item1, item2)) }
  }
}
