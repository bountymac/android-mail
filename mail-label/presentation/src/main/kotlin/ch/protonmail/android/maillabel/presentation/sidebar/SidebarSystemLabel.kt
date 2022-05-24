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

package ch.protonmail.android.maillabel.presentation.sidebar

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ch.protonmail.android.maillabel.presentation.MailLabelUiModel
import ch.protonmail.android.maillabel.presentation.MailLabelsUiModel
import ch.protonmail.android.maillabel.presentation.sidebar.SidebarLabelAction.Select
import me.proton.core.compose.component.ProtonSidebarItem
import me.proton.core.compose.component.ProtonSidebarLazy
import me.proton.core.compose.theme.ProtonTheme

fun LazyListScope.sidebarSystemLabelItems(
    items: List<MailLabelUiModel.System>,
    onLabelAction: (SidebarLabelAction) -> Unit,
) {
    items(items = items, key = { it.id.labelId.id }) {
        SidebarSystemLabel(it, onLabelAction)
    }
}

@Composable
private fun SidebarSystemLabel(
    item: MailLabelUiModel.System,
    onLabelAction: (SidebarLabelAction) -> Unit,
) {
    ProtonSidebarItem(
        icon = painterResource(item.icon),
        text = stringResource(item.text.value),
        count = item.count,
        isSelected = item.isSelected,
        onClick = { onLabelAction(Select(item.id)) }
    )
}

@SuppressLint("VisibleForTests")
@Preview(
    name = "Sidebar System Labels in light mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Sidebar System Labels in dark mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PreviewSidebarSystemLabelItems() {
    ProtonTheme {
        ProtonSidebarLazy {
            sidebarSystemLabelItems(
                items = MailLabelsUiModel.PreviewForTesting.systems,
                onLabelAction = {},
            )
        }
    }
}
