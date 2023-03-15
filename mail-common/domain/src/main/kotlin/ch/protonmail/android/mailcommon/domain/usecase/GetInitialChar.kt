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

package ch.protonmail.android.mailcommon.domain.usecase

import javax.inject.Inject

class GetInitialChar @Inject constructor() {

    operator fun invoke(value: String): String? {
        if (value.isBlank()) {
            return null
        }
        val firstChar = value.getOrNull(0)?.uppercaseChar() ?: return null
        val stringBuilder = StringBuilder().append(firstChar)

        if (firstChar.isHighSurrogate()) {
            value.getOrNull(1)?.let { followingChar ->
                stringBuilder.append(followingChar)
            }
        }
        return stringBuilder.toString()
    }
}
