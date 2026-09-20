package Emeris.PROG7314.trinitymobileclient.auth

class AuthException(
    val error: AuthError,
    cause: Throwable? = null
): Exception(error.name, cause)