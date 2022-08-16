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

package ch.protonmail.android.mailcommon.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import ch.protonmail.android.mailcommon.domain.model.DataError
import ch.protonmail.android.mailcommon.domain.model.NetworkError
import ch.protonmail.android.mailcommon.domain.model.ProtonError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import me.proton.core.domain.arch.DataResult
import me.proton.core.util.kotlin.takeIfNotEmpty

fun <T> Flow<DataResult<T>>.mapToEither(): Flow<Either<DataError, T>> = transform { dataResult ->
    when (dataResult) {
        is DataResult.Error.Local -> emit(toLocalError(dataResult).left())
        is DataResult.Error.Remote -> emit(toRemoteDataError(dataResult).left())
        is DataResult.Processing -> Unit
        is DataResult.Success -> emit(dataResult.value.right())
    }
}

private fun toLocalError(dataResult: DataResult.Error.Local): DataError.Local =
    DataError.Local.Other(messageFrom(dataResult))

private fun toRemoteDataError(dataResult: DataResult.Error.Remote): DataError.Remote {
    return when {
        dataResult.protonCode != PROTON_CODE_INITIAL_VALUE -> toProtonDataError(dataResult.protonCode)
        dataResult.httpCode != PROTON_CODE_INITIAL_VALUE -> toHttpDataError(dataResult.httpCode)
        else -> DataError.Remote.Other(messageFrom(dataResult))
    }
}

private fun toHttpDataError(httpCode: Int): DataError.Remote.Http {
    val networkError = when (httpCode) {
        401 -> NetworkError.Unauthorized
        403 -> NetworkError.Forbidden
        404 -> NetworkError.NotFound
        500 -> NetworkError.Internal
        else -> NetworkError.Other(httpCode)
    }
    return DataError.Remote.Http(networkError)
}

private fun toProtonDataError(protonCode: Int): DataError.Remote.Proton =
    DataError.Remote.Proton(ProtonError.Other(protonCode))

private fun messageFrom(dataResult: DataResult.Error): String =
    dataResult.message?.takeIfNotEmpty()
        ?: dataResult.cause?.message?.takeIfNotEmpty()
        ?: DATA_RESULT_NO_MESSAGE_PROVIDED

private const val PROTON_CODE_INITIAL_VALUE = 0
internal const val DATA_RESULT_NO_MESSAGE_PROVIDED = "DataResult didn't provide any message"
