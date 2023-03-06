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

package ch.protonmail.android.navigation.route

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import ch.protonmail.android.feature.account.RemoveAccountDialog
import ch.protonmail.android.feature.account.RemoveAccountDialog.USER_ID_KEY
import ch.protonmail.android.mailcommon.domain.model.ConversationId
import ch.protonmail.android.maildetail.presentation.ui.ConversationDetailScreen
import ch.protonmail.android.maildetail.presentation.ui.MessageDetailScreen
import ch.protonmail.android.mailmailbox.domain.model.MailboxItemType
import ch.protonmail.android.mailmailbox.presentation.mailbox.MailboxScreen
import ch.protonmail.android.mailmessage.domain.entity.MessageId
import ch.protonmail.android.mailsettings.presentation.settings.MainSettingsScreen
import ch.protonmail.android.navigation.model.Destination
import me.proton.core.compose.navigation.get
import me.proton.core.domain.entity.UserId
import me.proton.core.util.kotlin.takeIfNotBlank

internal fun NavGraphBuilder.addConversationDetail(
    navController: NavHostController,
    showSnackbar: (message: String) -> Unit
) {
    composable(route = Destination.Screen.Conversation.route) {
        ConversationDetailScreen(
            onExit = { notifyUserMessage ->
                navController.popBackStack()
                notifyUserMessage?.let(showSnackbar)
            }
        )
    }
}

internal fun NavGraphBuilder.addMailbox(
    navController: NavHostController,
    openDrawerMenu: () -> Unit,
    showOfflineSnackbar: () -> Unit,
    showFeatureMissingSnackbar: () -> Unit
) {
    composable(route = Destination.Screen.Mailbox.route) {
        MailboxScreen(
            actions = MailboxScreen.Actions.Empty.copy(
                navigateToMailboxItem = { request ->
                    navController.navigate(
                        when (request.itemType) {
                            MailboxItemType.Message -> Destination.Screen.Message(MessageId(request.itemId.value))
                            MailboxItemType.Conversation ->
                                Destination.Screen.Conversation(ConversationId(request.itemId.value))
                        }
                    )
                },
                openDrawerMenu = openDrawerMenu,
                showOfflineSnackbar = showOfflineSnackbar,
                showFeatureMissingSnackbar = showFeatureMissingSnackbar
            )
        )
    }
}

internal fun NavGraphBuilder.addMessageDetail(
    navController: NavHostController,
    showSnackbar: (notifyUserMessage: String) -> Unit,
    showFeatureMissingSnackbar: () -> Unit,
    openMessageBodyLink: (uri: Uri) -> Unit
) {
    composable(route = Destination.Screen.Message.route) {
        MessageDetailScreen(
            onExit = { notifyUserMessage ->
                navController.popBackStack()
                notifyUserMessage?.let(showSnackbar)
            },
            openMessageBodyLink = openMessageBodyLink,
            showFeatureMissingSnackbar = showFeatureMissingSnackbar
        )
    }
}

internal fun NavGraphBuilder.addRemoveAccountDialog(navController: NavHostController) {
    dialog(route = Destination.Dialog.RemoveAccount.route) {
        RemoveAccountDialog(
            userId = it.get<String>(USER_ID_KEY)?.takeIfNotBlank()?.let(::UserId),
            onRemoved = { navController.popBackStack() },
            onCancelled = { navController.popBackStack() }
        )
    }
}

internal fun NavGraphBuilder.addSettings(
    navController: NavHostController,
    showFeatureMissingSnackbar: () -> Unit
) {
    composable(route = Destination.Screen.Settings.route) {
        MainSettingsScreen(
            actions = MainSettingsScreen.Actions(
                onAccountClick = {
                    navController.navigate(Destination.Screen.AccountSettings.route)
                },
                onThemeClick = {
                    navController.navigate(Destination.Screen.ThemeSettings.route)
                },
                onPushNotificationsClick = {
                    showFeatureMissingSnackbar()
                },
                onAutoLockClick = {
                    showFeatureMissingSnackbar()
                },
                onAlternativeRoutingClick = {
                    navController.navigate(Destination.Screen.AlternativeRoutingSettings.route)
                },
                onAppLanguageClick = {
                    navController.navigate(Destination.Screen.LanguageSettings.route)
                },
                onCombinedContactsClick = {
                    navController.navigate(Destination.Screen.CombinedContactsSettings.route)
                },
                onSwipeActionsClick = {
                    navController.navigate(Destination.Screen.SwipeActionsSettings.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        )
    }
}

