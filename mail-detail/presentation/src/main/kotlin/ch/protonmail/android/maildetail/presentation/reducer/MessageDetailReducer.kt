/*
 * Copyright (c) 2022 Proton Technologies AG
 * This file is part of Proton Technologies AG and Proton Mail.
 *
 * Proton Mail is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Mail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Mail. If not, see <https://www.gnu.org/licenses/>.
 */

package ch.protonmail.android.maildetail.presentation.reducer

import ch.protonmail.android.mailcommon.presentation.Effect
import ch.protonmail.android.mailcommon.presentation.model.TextUiModel
import ch.protonmail.android.mailcommon.presentation.reducer.BottomBarReducer
import ch.protonmail.android.maildetail.presentation.R
import ch.protonmail.android.maildetail.presentation.model.BottomSheetAction
import ch.protonmail.android.maildetail.presentation.model.MessageDetailEvent
import ch.protonmail.android.maildetail.presentation.model.MessageDetailOperation
import ch.protonmail.android.maildetail.presentation.model.MessageDetailState
import ch.protonmail.android.maildetail.presentation.model.MessageViewAction
import me.proton.core.util.kotlin.exhaustive
import javax.inject.Inject

class MessageDetailReducer @Inject constructor(
    private val messageMetadataReducer: MessageDetailMetadataReducer,
    private val bottomBarReducer: BottomBarReducer,
    private val bottomSheetReducer: BottomSheetReducer
) {

    fun newStateFrom(
        currentState: MessageDetailState,
        operation: MessageDetailOperation
    ): MessageDetailState = currentState.copy(
        messageMetadataState = currentState.toNewMessageStateFrom(operation),
        bottomBarState = currentState.toNewBottomBarStateFrom(operation),
        bottomSheetState = currentState.toNewBottomSheetStateFrom(operation),
        dismiss = currentState.toNewDismissStateFrom(operation),
        error = currentState.toNewErrorStateFrom(operation)
    )

    private fun MessageDetailState.toNewErrorStateFrom(operation: MessageDetailOperation) =
        if (operation is MessageDetailOperation.AffectingErrorBar) {
            when (operation) {
                is MessageDetailEvent.ErrorMarkingUnread -> Effect.of(TextUiModel(R.string.error_mark_unread_failed))
                is MessageDetailEvent.ErrorAddingStar -> Effect.of(TextUiModel(R.string.error_star_operation_failed))
                is MessageDetailEvent.ErrorRemovingStar ->
                    Effect.of(TextUiModel(R.string.error_unstar_operation_failed))
                MessageDetailEvent.ErrorMovingToTrash -> Effect.of(TextUiModel(R.string.error_move_to_trash_failed))
            }.exhaustive
        } else {
            error
        }

    private fun MessageDetailState.toNewDismissStateFrom(operation: MessageDetailOperation): Effect<Unit> {
        val dismissOperations = listOf(
            MessageViewAction.MarkUnread,
            MessageViewAction.Trash
        )
        return if (operation in dismissOperations) {
            Effect.of(Unit)
        } else {
            dismiss
        }
    }

    private fun MessageDetailState.toNewMessageStateFrom(operation: MessageDetailOperation) =
        if (operation is MessageDetailOperation.AffectingMessage) {
            messageMetadataReducer.newStateFrom(messageMetadataState, operation)
        } else {
            messageMetadataState
        }

    private fun MessageDetailState.toNewBottomBarStateFrom(operation: MessageDetailOperation) =
        if (operation is MessageDetailEvent.MessageBottomBarEvent) {
            bottomBarReducer.newStateFrom(bottomBarState, operation.bottomBarEvent)
        } else {
            bottomBarState
        }

    private fun MessageDetailState.toNewBottomSheetStateFrom(operation: MessageDetailOperation) =
        when (operation) {
            is MessageDetailEvent.MessageBottomSheetEvent -> bottomSheetReducer.newStateFrom(
                bottomSheetState,
                operation.bottomSheetEvent
            )
            is MessageViewAction.MoveToSelected -> bottomSheetReducer.newStateFrom(
                bottomSheetState,
                BottomSheetAction.MoveToDestinationSelected(operation.mailLabelId)
            )
            else -> bottomSheetState
        }


}
