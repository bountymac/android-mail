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

package ch.protonmail.android.mailmailbox.presentation.mailbox.previewdata

import ch.protonmail.android.mailcommon.presentation.Effect
import ch.protonmail.android.maillabel.domain.model.MailLabel
import ch.protonmail.android.maillabel.domain.model.MailLabelId
import ch.protonmail.android.maillabel.presentation.text
import ch.protonmail.android.mailmailbox.presentation.mailbox.model.MailboxListState
import ch.protonmail.android.mailmailbox.presentation.mailbox.model.MailboxState
import ch.protonmail.android.mailmailbox.presentation.mailbox.model.MailboxTopAppBarState
import ch.protonmail.android.mailmailbox.presentation.mailbox.model.UnreadFilterState

object MailboxStatePreviewData {

    val Loading = MailboxState.Loading

    val Inbox = MailboxState(
        mailboxListState = MailboxListState.Data(
            currentMailLabel = MailLabel.System(MailLabelId.System.Inbox),
            openItemEffect = Effect.empty(),
            scrollToMailboxTop = Effect.empty()
        ),
        topAppBarState = MailboxTopAppBarState.Data.DefaultMode(
            currentLabelName = MailLabel.System(MailLabelId.System.Inbox).text()
        ),
        unreadFilterState = UnreadFilterState.Data(
            isFilterEnabled = false,
            numUnread = 1
        ),
        networkStatusEffect = Effect.empty()
    )

    val AllMail = MailboxState(
        mailboxListState = MailboxListState.Data(
            currentMailLabel = MailLabel.System(MailLabelId.System.AllMail),
            openItemEffect = Effect.empty(),
            scrollToMailboxTop = Effect.empty()
        ),
        topAppBarState = MailboxTopAppBarState.Data.DefaultMode(
            currentLabelName = MailLabel.System(MailLabelId.System.AllMail).text()
        ),
        unreadFilterState = UnreadFilterState.Data(
            isFilterEnabled = false,
            numUnread = 1
        ),
        networkStatusEffect = Effect.empty()
    )
}
