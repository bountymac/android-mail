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

package ch.protonmail.android.mailconversation.data.remote

import ch.protonmail.android.mailcommon.domain.model.ConversationId
import ch.protonmail.android.mailconversation.data.remote.worker.AddLabelConversationWorker
import ch.protonmail.android.mailconversation.data.remote.worker.RemoveLabelConversationWorker
import ch.protonmail.android.mailconversation.domain.entity.ConversationWithContext
import ch.protonmail.android.mailconversation.domain.entity.ConversationWithMessages
import ch.protonmail.android.mailconversation.domain.repository.ConversationRemoteDataSource
import ch.protonmail.android.mailmessage.domain.entity.MessageId
import ch.protonmail.android.mailpagination.domain.model.OrderBy
import ch.protonmail.android.mailpagination.domain.model.OrderDirection
import ch.protonmail.android.mailpagination.domain.model.PageKey
import ch.protonmail.android.mailpagination.domain.model.ReadStatus
import me.proton.core.domain.entity.UserId
import me.proton.core.label.domain.entity.LabelId
import me.proton.core.network.data.ApiProvider
import me.proton.core.util.kotlin.takeIfNotBlank
import javax.inject.Inject

class ConversationRemoteDataSourceImpl @Inject constructor(
    private val apiProvider: ApiProvider,
    private val addLabelConversationWorker: AddLabelConversationWorker.Enqueuer,
    private val removeLabelConversationWorker: RemoveLabelConversationWorker.Enqueuer
) : ConversationRemoteDataSource {

    override suspend fun getConversations(
        userId: UserId,
        pageKey: PageKey
    ): List<ConversationWithContext> = apiProvider.get<ConversationApi>(userId).invoke {
        require(pageKey.size <= ConversationApi.maxPageSize)
        getConversations(
            labelIds = listOf(pageKey.filter.labelId).map { it.id },
            beginTime = pageKey.filter.minTime.takeIf { it != Long.MIN_VALUE },
            endTime = pageKey.filter.maxTime.takeIf { it != Long.MAX_VALUE },
            beginId = pageKey.filter.minId,
            endId = pageKey.filter.maxId,
            keyword = pageKey.filter.keyword.takeIfNotBlank(),
            unread = when (pageKey.filter.read) {
                ReadStatus.All -> null
                ReadStatus.Read -> 0
                ReadStatus.Unread -> 1
            },
            sort = when (pageKey.orderBy) {
                OrderBy.Time -> "Time"
            },
            desc = when (pageKey.orderDirection) {
                OrderDirection.Ascending -> 0
                OrderDirection.Descending -> 1
            },
            pageSize = pageKey.size
        ).conversations.map { it.toConversationWithContext(userId, pageKey.filter.labelId) }
    }.valueOrThrow

    override suspend fun getConversationWithMessages(
        userId: UserId,
        conversationId: ConversationId
    ): ConversationWithMessages = apiProvider.get<ConversationApi>(userId).invoke {
        val response = getConversation(conversationId = conversationId.id)
        ConversationWithMessages(
            response.conversation.toConversation(userId),
            messages = response.messages.map { it.toMessage(userId) }
        )
    }.valueOrThrow

    override fun addLabel(
        userId: UserId,
        conversationId: ConversationId,
        labelId: LabelId,
        messageIds: List<MessageId>
    ) {
        addLabelConversationWorker.enqueue(userId, conversationId, labelId, messageIds)
    }

    override fun removeLabel(
        userId: UserId,
        conversationId: ConversationId,
        labelId: LabelId,
        messageIds: List<MessageId>
    ) {
        removeLabelConversationWorker.enqueue(userId, conversationId, labelId, messageIds)
    }
}
