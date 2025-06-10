package org.example.pekomon.cryptoapp.core.util

import cryptoapp.composeapp.generated.resources.Res
import cryptoapp.composeapp.generated.resources.*
import org.example.pekomon.cryptoapp.core.domain.DataError
import org.jetbrains.compose.resources.StringResource

fun DataError.toUiText(): StringResource {
    return when(this) {
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.SERVER -> Res.string.error_unknown
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization
        DataError.Remote.UNKNOWN -> Res.string.error_unknown
        DataError.Local.INSUFFICIENT_FUNDS -> Res.string.error_insufficient_balance
        DataError.Remote.TO_MANY_REQUESTS -> Res.string.error_too_many_requests
    }
}