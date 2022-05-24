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

package ch.protonmail.android.mailsettings.domain

import app.cash.turbine.test
import ch.protonmail.android.testdata.mailsettings.MailSettingsTestData.buildMailSettings
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import me.proton.core.domain.arch.DataResult
import me.proton.core.domain.arch.ResponseSource
import me.proton.core.domain.entity.UserId
import me.proton.core.mailsettings.domain.entity.MailSettings
import me.proton.core.mailsettings.domain.repository.MailSettingsRepository
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ObserveFolderColorSettingsTest {

    private val userId = UserId("1")

    private val mutableMailSettings = MutableSharedFlow<DataResult<MailSettings>>(replay = 1)
    private val mailSettingsRepository = mockk<MailSettingsRepository> {
        coEvery { getMailSettingsFlow(any()) } returns mutableMailSettings
    }

    private lateinit var usecase: ObserveFolderColorSettings

    @Before
    fun setUp() {
        usecase = ObserveFolderColorSettings(mailSettingsRepository)
    }

    @Test
    fun `return correct value on success`() = runTest {
        // Given
        mutableMailSettings.emit(
            DataResult.Success(
                source = ResponseSource.Local,
                value = buildMailSettings(
                    enableFolderColor = true,
                    inheritParentFolderColor = true
                )
            )
        )

        // When
        usecase.invoke(userId).test {
            // Then
            val item = awaitItem()
            assertEquals(true, item.useFolderColor)
            assertEquals(true, item.inheritParentFolderColor)
        }
    }

    @Test
    fun `return default value on error`() = runTest {
        // Given
        mutableMailSettings.emit(DataResult.Error.Local(message = "Error", cause = null))

        // When
        usecase.invoke(userId).test {
            // Then
            val item = awaitItem()
            assertEquals(true, item.useFolderColor)
            assertEquals(false, item.inheritParentFolderColor)
        }
    }
}
