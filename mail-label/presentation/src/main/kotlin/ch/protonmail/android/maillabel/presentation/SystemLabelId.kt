/*
 * Copyright (c) 2021 Proton Technologies AG
 * This file is part of Proton Technologies AG and ProtonMail.
 *
 * ProtonMail is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ProtonMail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ProtonMail.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.protonmail.android.maillabel.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ch.protonmail.android.maillabel.domain.model.SystemLabelId

@StringRes
fun SystemLabelId.textRes() = when (this) {
    SystemLabelId.Inbox -> R.string.label_title_inbox
    SystemLabelId.AllDrafts -> R.string.label_title_all_drafts
    SystemLabelId.AllSent -> R.string.label_title_all_sent
    SystemLabelId.Trash -> R.string.label_title_trash
    SystemLabelId.Spam -> R.string.label_title_spam
    SystemLabelId.AllMail -> R.string.label_title_all_mail
    SystemLabelId.Archive -> R.string.label_title_archive
    SystemLabelId.Sent -> R.string.label_title_sent
    SystemLabelId.Drafts -> R.string.label_title_drafts
    SystemLabelId.Outbox -> R.string.label_title_outbox
    SystemLabelId.Starred -> R.string.label_title_starred
}

@DrawableRes
fun SystemLabelId.iconRes() = when (this) {
    SystemLabelId.Inbox -> R.drawable.ic_proton_inbox
    SystemLabelId.AllDrafts -> R.drawable.ic_proton_inbox
    SystemLabelId.AllSent -> R.drawable.ic_proton_inbox
    SystemLabelId.Trash -> R.drawable.ic_proton_trash
    SystemLabelId.Spam -> R.drawable.ic_proton_fire
    SystemLabelId.AllMail -> R.drawable.ic_proton_envelopes
    SystemLabelId.Archive -> R.drawable.ic_proton_archive_box
    SystemLabelId.Sent -> R.drawable.ic_proton_paper_plane
    SystemLabelId.Drafts -> R.drawable.ic_proton_file_lines
    SystemLabelId.Outbox -> R.drawable.ic_proton_inbox
    SystemLabelId.Starred -> R.drawable.ic_proton_star
}
