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

package ch.protonmail.android.mailcontact.presentation.managemembers

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import arrow.core.right
import ch.protonmail.android.mailcommon.domain.usecase.ObservePrimaryUserId
import ch.protonmail.android.mailcontact.domain.usecase.ObserveContacts
import ch.protonmail.android.mailcontact.presentation.model.ManageMembersUiModel
import ch.protonmail.android.mailcontact.presentation.model.ManageMembersUiModelMapper
import ch.protonmail.android.test.utils.rule.MainDispatcherRule
import ch.protonmail.android.testdata.user.UserIdTestData
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.proton.core.contact.domain.entity.Contact
import me.proton.core.contact.domain.entity.ContactEmail
import me.proton.core.contact.domain.entity.ContactEmailId
import me.proton.core.contact.domain.entity.ContactId
import me.proton.core.test.kotlin.TestDispatcherProvider
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ManageMembersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(TestDispatcherProvider().Main)

    private val defaultTestContact = Contact(
        UserIdTestData.userId,
        ContactId("ContactId1"),
        "John Doe",
        listOf(
            ContactEmail(
                UserIdTestData.userId,
                ContactEmailId("ContactEmailId1"),
                "John Doe",
                "johndoe+alias@protonmail.com",
                0,
                0,
                ContactId("ContactId1"),
                "johndoe@protonmail.com",
                emptyList(),
                true
            ),
            ContactEmail(
                UserIdTestData.userId,
                ContactEmailId("ContactEmailId2"),
                "Jane Doe",
                "janedoe@protonmail.com",
                0,
                0,
                ContactId("ContactId1"),
                "janedoe@protonmail.com",
                emptyList(),
                true
            )
        )
    )
    private val defaultTestSelectedContactEmailIds = listOf(ContactEmailId("ContactEmailId2"))
    private val defaultTestManageMembersUiModel = listOf(
        ManageMembersUiModel(
            id = ContactEmailId("ContactEmailId1"),
            name = "John Doe",
            email = "johndoe+alias@protonmail.com",
            initials = "JD",
            isSelected = false
        ),
        ManageMembersUiModel(
            id = ContactEmailId("ContactEmailId2"),
            name = "Jane Doe",
            email = "janedoe@protonmail.com",
            initials = "JD",
            isSelected = true
        )
    )

    private val observePrimaryUserId = mockk<ObservePrimaryUserId> {
        every { this@mockk.invoke() } returns flowOf(UserIdTestData.userId)
    }

    private val manageMembersUiModelMapperMock = mockk<ManageMembersUiModelMapper>()
    private val observeContactsMock = mockk<ObserveContacts>()
    private val savedStateHandleMock = mockk<SavedStateHandle>()

    private val reducer = ManageMembersReducer()

    private val manageMembersViewModel by lazy {
        ManageMembersViewModel(
            observeContactsMock,
            reducer,
            manageMembersUiModelMapperMock,
            savedStateHandleMock,
            observePrimaryUserId
        )
    }

    @Test
    fun `given contact list, when init, then emits data state`() = runTest {
        // Given
        val contacts = listOf(defaultTestContact)
        expectContactsData(contacts)
        expectUiModelMapper(contacts, defaultTestSelectedContactEmailIds, defaultTestManageMembersUiModel)
        expectSavedState(defaultTestSelectedContactEmailIds)

        // When
        manageMembersViewModel.state.test {
            skipItems(1)

            // Then
            val actual = awaitItem()
            val expected = ManageMembersState.Data(
                members = defaultTestManageMembersUiModel
            )

            assertEquals(expected, actual)
        }
    }

    private fun expectContactsData(contacts: List<Contact>) {
        coEvery {
            observeContactsMock(userId = UserIdTestData.userId)
        } returns flowOf(contacts.right())
    }

    private fun expectUiModelMapper(
        contacts: List<Contact>,
        selectedContactEmailIds: List<ContactEmailId>,
        manageMembersUiModel: List<ManageMembersUiModel>
    ) {
        every {
            manageMembersUiModelMapperMock.toManageMembersUiModelList(
                contacts = contacts,
                selectedContactEmailIds = selectedContactEmailIds
            )
        } returns manageMembersUiModel
    }

    private fun expectSavedState(selectedContactEmailIds: List<ContactEmailId>) {
        every {
            savedStateHandleMock.get<List<String>>(ManageMembersScreen.ManageMembersSelectedContactEmailIdsKey)
        } returns selectedContactEmailIds.map { it.id }
    }
}
