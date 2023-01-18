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
import ch.protonmail.android.maildetail.presentation.model.BottomSheetEffect
import ch.protonmail.android.maildetail.presentation.model.BottomSheetState
import ch.protonmail.android.maildetail.presentation.model.MoveToBottomSheetState
import ch.protonmail.android.maildetail.presentation.model.MoveToBottomSheetState.Data
import ch.protonmail.android.maildetail.presentation.model.MoveToBottomSheetState.Loading
import ch.protonmail.android.maildetail.presentation.model.MoveToBottomSheetState.MoveToBottomSheetAction
import ch.protonmail.android.maildetail.presentation.model.MoveToBottomSheetState.MoveToBottomSheetEvent
import ch.protonmail.android.maildetail.presentation.model.MoveToBottomSheetState.MoveToBottomSheetOperation
import ch.protonmail.android.maillabel.domain.model.MailLabelId
import ch.protonmail.android.maillabel.presentation.MailLabelUiModel
import javax.inject.Inject

class MoveToBottomSheetReducer @Inject constructor() {

    fun newStateFrom(
        currentState: BottomSheetState?,
        event: MoveToBottomSheetOperation
    ): BottomSheetState? {
        return when (event) {
            is MoveToBottomSheetAction.MoveToDestinationSelected -> when (
                val contentState =
                    currentState?.contentState
            ) {
                is Data -> BottomSheetState(
                    contentState.toNewSelectedState(event.mailLabelId),
                    currentState.bottomSheetEffect
                )
                else -> currentState
            }
            is MoveToBottomSheetAction.Requested -> BottomSheetState(null, Effect.of(BottomSheetEffect.Show))
            is MoveToBottomSheetEvent.ActionData -> when (val contentState = currentState?.contentState) {
                is Data -> BottomSheetState(
                    contentState.copy(
                        moveToDestinations = event.moveToDestinations
                    ).let { if (it.selected != null) it.toNewSelectedState(it.selected.id) else it },
                    currentState.bottomSheetEffect
                )
                else -> BottomSheetState(
                    Data(event.moveToDestinations, null),
                    currentState?.bottomSheetEffect ?: Effect.empty()
                )
            }
        }
    }

    private fun MoveToBottomSheetState.toNewSelectedState(mailLabelId: MailLabelId): MoveToBottomSheetState {
        return when (this) {
            is Loading -> this
            is Data -> {
                val listWIthSelectedLabel = moveToDestinations.map { it.setSelectedIfLabelIdMatch(mailLabelId) }
                this.copy(
                    moveToDestinations = listWIthSelectedLabel,
                    selected = listWIthSelectedLabel.first { it.id == mailLabelId }
                )
            }
        }
    }

    private fun MailLabelUiModel.setSelectedIfLabelIdMatch(mailLabelId: MailLabelId) =
        when (this) {
            is MailLabelUiModel.Custom -> copy(isSelected = id == mailLabelId)
            is MailLabelUiModel.System -> copy(isSelected = id == mailLabelId)
        }

}
