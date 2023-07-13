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

package ch.protonmail.android.maildetail.domain.usecase

import me.proton.core.domain.entity.UserId
import me.proton.core.mailsettings.domain.entity.ShowImage
import me.proton.core.mailsettings.domain.repository.MailSettingsRepository
import me.proton.core.mailsettings.domain.repository.getMailSettingsOrNull
import javax.inject.Inject

class ShouldShowEmbeddedImages @Inject constructor(
    private val mailSettingsRepository: MailSettingsRepository
) {

    suspend operator fun invoke(userId: UserId): Boolean =
        mailSettingsRepository.getMailSettingsOrNull(userId, refresh = false)?.showImages?.let {
            it.enum == ShowImage.Embedded || it.enum == ShowImage.Both
        } ?: false
}
