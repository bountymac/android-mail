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

package ch.protonmail.android.mailsettings.data.usecase

import arrow.core.Either
import arrow.core.raise.either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.proton.core.crypto.common.keystore.KeyStoreCrypto
import javax.inject.Inject

class DecryptSerializableValue @Inject constructor(
    private val keyStoreCrypto: KeyStoreCrypto
) {

    val String.decrypted: String
        get() = keyStoreCrypto.decrypt(this)

    suspend inline operator fun <reified T> invoke(value: String): Either<DecodingError, T> = either {
        withContext(Dispatchers.IO) {
            runCatching<T> { Json.decodeFromString(value.decrypted) }.getOrElse {
                raise(DecodingError(it.message.toString()))
            }
        }
    }

    data class DecodingError(val message: String)
}
