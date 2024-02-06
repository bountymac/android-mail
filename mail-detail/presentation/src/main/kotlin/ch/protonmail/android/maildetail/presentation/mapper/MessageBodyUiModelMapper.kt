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

package ch.protonmail.android.maildetail.presentation.mapper

import ch.protonmail.android.maildetail.domain.usecase.DoesMessageBodyHaveEmbeddedImages
import ch.protonmail.android.maildetail.domain.usecase.DoesMessageBodyHaveRemoteContent
import ch.protonmail.android.maildetail.domain.usecase.ShouldShowEmbeddedImages
import ch.protonmail.android.maildetail.domain.usecase.ShouldShowRemoteContent
import ch.protonmail.android.maildetail.presentation.usecase.ExtractMessageBodyWithoutQuote
import ch.protonmail.android.mailmessage.domain.model.DecryptedMessageBody
import ch.protonmail.android.mailmessage.domain.model.GetDecryptedMessageBodyError
import ch.protonmail.android.mailmessage.domain.model.MimeType
import ch.protonmail.android.mailmessage.presentation.mapper.AttachmentUiModelMapper
import ch.protonmail.android.mailmessage.presentation.model.AttachmentGroupUiModel
import ch.protonmail.android.mailmessage.presentation.model.MessageBodyUiModel
import ch.protonmail.android.mailmessage.presentation.model.MimeTypeUiModel
import ch.protonmail.android.mailmessage.presentation.usecase.InjectCssIntoDecryptedMessageBody
import ch.protonmail.android.mailmessage.presentation.usecase.SanitizeHtmlOfDecryptedMessageBody
import me.proton.core.domain.entity.UserId
import javax.inject.Inject

class MessageBodyUiModelMapper @Inject constructor(
    private val attachmentUiModelMapper: AttachmentUiModelMapper,
    private val doesMessageBodyHaveEmbeddedImages: DoesMessageBodyHaveEmbeddedImages,
    private val doesMessageBodyHaveRemoteContent: DoesMessageBodyHaveRemoteContent,
    private val injectCssIntoDecryptedMessageBody: InjectCssIntoDecryptedMessageBody,
    private val sanitizeHtmlOfDecryptedMessageBody: SanitizeHtmlOfDecryptedMessageBody,
    private val extractMessageBodyWithoutQuote: ExtractMessageBodyWithoutQuote,
    private val shouldShowEmbeddedImages: ShouldShowEmbeddedImages,
    private val shouldShowRemoteContent: ShouldShowRemoteContent
) {

    suspend fun toUiModel(userId: UserId, decryptedMessageBody: DecryptedMessageBody): MessageBodyUiModel {
        val sanitizedMessageBody = sanitizeHtmlOfDecryptedMessageBody(
            decryptedMessageBody.value,
            decryptedMessageBody.mimeType.toMimeTypeUiModel()
        )
        val shouldShowEmbeddedImages = shouldShowEmbeddedImages(userId)
        val doesMessageBodyHaveEmbeddedImages = doesMessageBodyHaveEmbeddedImages(decryptedMessageBody)
        val shouldShowRemoteContent = shouldShowRemoteContent(userId)
        val doesMessageBodyHaveRemoteContent = doesMessageBodyHaveRemoteContent(decryptedMessageBody)

        val originalMessageBody = injectCssIntoDecryptedMessageBody(
            sanitizedMessageBody,
            decryptedMessageBody.mimeType.toMimeTypeUiModel()
        )

        val extractQuoteResult = extractMessageBodyWithoutQuote(originalMessageBody)
        val bodyWithoutQuote = if (extractQuoteResult.hasQuote) {
            extractQuoteResult.messageBodyHtmlWithoutQuote
        } else originalMessageBody

        return MessageBodyUiModel(
            messageId = decryptedMessageBody.messageId,
            messageBody = originalMessageBody,
            messageBodyWithoutQuote = bodyWithoutQuote,
            mimeType = decryptedMessageBody.mimeType.toMimeTypeUiModel(),
            shouldShowEmbeddedImages = shouldShowEmbeddedImages,
            shouldShowRemoteContent = shouldShowRemoteContent(userId),
            shouldShowEmbeddedImagesBanner = !shouldShowEmbeddedImages && doesMessageBodyHaveEmbeddedImages,
            shouldShowRemoteContentBanner = !shouldShowRemoteContent && doesMessageBodyHaveRemoteContent,
            shouldShowExpandCollapseButton = extractQuoteResult.hasQuote,
            shouldShowOpenInProtonCalendar = decryptedMessageBody.attachments.any { it.isCalendarAttachment() },
            attachments = if (decryptedMessageBody.attachments.isNotEmpty()) {
                AttachmentGroupUiModel(
                    attachments = decryptedMessageBody.attachments.map {
                        attachmentUiModelMapper.toUiModel(it)
                    }
                )
            } else null,
            userAddress = decryptedMessageBody.userAddress
        )
    }

    fun toUiModel(decryptionError: GetDecryptedMessageBodyError.Decryption) = MessageBodyUiModel(
        messageId = decryptionError.messageId,
        messageBody = decryptionError.encryptedMessageBody,
        messageBodyWithoutQuote = decryptionError.encryptedMessageBody,
        mimeType = MimeTypeUiModel.PlainText,
        shouldShowEmbeddedImages = false,
        shouldShowRemoteContent = false,
        shouldShowEmbeddedImagesBanner = false,
        shouldShowRemoteContentBanner = false,
        shouldShowExpandCollapseButton = false,
        shouldShowOpenInProtonCalendar = false,
        attachments = null,
        userAddress = null
    )

    private fun MimeType.toMimeTypeUiModel() = when (this) {
        MimeType.PlainText -> MimeTypeUiModel.PlainText
        MimeType.Html, MimeType.MultipartMixed -> MimeTypeUiModel.Html
    }
}
