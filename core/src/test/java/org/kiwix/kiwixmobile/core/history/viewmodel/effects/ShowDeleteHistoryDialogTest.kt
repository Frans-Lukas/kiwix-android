package org.kiwix.kiwixmobile.core.history.viewmodel.effects

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.processors.PublishProcessor
import org.junit.jupiter.api.Test
import org.kiwix.kiwixmobile.core.history.HistoryActivity
import org.kiwix.kiwixmobile.core.history.viewmodel.Action
import org.kiwix.kiwixmobile.core.history.viewmodel.Action.UserClickedConfirmDelete
import org.kiwix.kiwixmobile.core.utils.DialogShower
import org.kiwix.kiwixmobile.core.utils.KiwixDialog.DeleteAllHistory

internal class ShowDeleteHistoryDialogTest {

  @Test
  fun `invoke with shows dialog that offers ConfirmDelete action`() {
    val actions = mockk<PublishProcessor<Action>>(relaxed = true)
    val activity = mockk<HistoryActivity>()
    val showDeleteHistoryDialog = ShowDeleteHistoryDialog(actions)
    val dialogShower = mockk<DialogShower>()
    every { activity.activityComponent.inject(showDeleteHistoryDialog) } answers {
      showDeleteHistoryDialog.dialogShower = dialogShower
      Unit
    }
    val lambdaSlot = slot<() -> Unit>()
    showDeleteHistoryDialog.invokeWith(activity)
    verify { dialogShower.show(DeleteAllHistory, capture(lambdaSlot)) }
    lambdaSlot.captured.invoke()
    verify { actions.offer(UserClickedConfirmDelete) }
  }
}
