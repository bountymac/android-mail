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

package ch.protonmail.android.mailmailbox.presentation.mailbox.model

import ch.protonmail.android.mailcommon.presentation.Effect
import ch.protonmail.android.maillabel.domain.model.MailLabel
import ch.protonmail.android.maillabel.domain.model.MailLabelId
import ch.protonmail.android.mailmailbox.domain.model.OpenMailboxItemRequest

sealed interface MailboxListState {

    val selectionModeEnabled: Boolean

    sealed interface Data : MailboxListState {

        val currentMailLabel: MailLabel

        data class ViewMode(
            override val currentMailLabel: MailLabel,
            override val selectionModeEnabled: Boolean,
            val openItemEffect: Effect<OpenMailboxItemRequest>,
            val scrollToMailboxTop: Effect<MailLabelId>,
            val offlineEffect: Effect<Unit>,
            val refreshErrorEffect: Effect<Unit>,
            val refreshRequested: Boolean
        ) : Data

        data class SelectionMode(
            override val currentMailLabel: MailLabel,
            override val selectionModeEnabled: Boolean,
            val selectedMailboxItems: Set<SelectedMailboxItem>
        ) : Data {

            data class SelectedMailboxItem(
                val id: String,
                val isRead: Boolean
            )
        }
    }

    data class Loading(override val selectionModeEnabled: Boolean) : MailboxListState
}

