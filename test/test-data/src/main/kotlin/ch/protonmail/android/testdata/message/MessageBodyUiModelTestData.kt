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

package ch.protonmail.android.testdata.message

import ch.protonmail.android.maildetail.presentation.model.MessageBodyUiModel
import ch.protonmail.android.maildetail.presentation.model.MimeTypeUiModel
import ch.protonmail.android.mailmessage.presentation.model.AttachmentGroupUiModel
import ch.protonmail.android.mailmessage.presentation.sample.AttachmentUiModelSample
import ch.protonmail.android.mailmessage.domain.model.MessageId
import ch.protonmail.android.mailmessage.domain.sample.MessageIdSample

object MessageBodyUiModelTestData {

    val plainTextMessageBodyUiModel = buildMessageBodyUiModel()

    val messageBodyWithAttachmentsUiModel = buildMessageBodyUiModel(
        attachments = AttachmentGroupUiModel(
            limit = 3,
            attachments = listOf(
                AttachmentUiModelSample.invoice,
                AttachmentUiModelSample.document,
                AttachmentUiModelSample.documentWithMultipleDots,
                AttachmentUiModelSample.image
            )
        )
    )

    val htmlMessageBodyUiModel = buildMessageBodyUiModel(
        messageBody = """
            <div>
                <p>Dear Test,</p>
                <p>This is an HTML message body.</p>
                <p>Kind regards,<br>
                Developer</p>
            </div>
        """.trimIndent(),
        mimeType = MimeTypeUiModel.Html
    )

    fun buildMessageBodyUiModel(
        messageId: MessageId = MessageIdSample.build(),
        messageBody: String = MessageBodyTestData.messageBody.body,
        mimeType: MimeTypeUiModel = MimeTypeUiModel.PlainText,
        shouldShowEmbeddedImages: Boolean = false,
        shouldShowRemoteContent: Boolean = false,
        shouldShowEmbeddedImagesBanner: Boolean = false,
        shouldShowRemoteContentBanner: Boolean = false,
        attachments: AttachmentGroupUiModel? = null
    ): MessageBodyUiModel {
        return MessageBodyUiModel(
            messageBody = messageBody,
            messageId = messageId,
            mimeType = mimeType,
            shouldShowEmbeddedImages = shouldShowEmbeddedImages,
            shouldShowRemoteContent = shouldShowRemoteContent,
            shouldShowEmbeddedImagesBanner = shouldShowEmbeddedImagesBanner,
            shouldShowRemoteContentBanner = shouldShowRemoteContentBanner,
            attachments = attachments
        )
    }
}
