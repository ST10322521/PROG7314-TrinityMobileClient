package Emeris.PROG7314.trinitymobileclient.ui

fun initialsOf(username: String?): String? {
    val name = username?.substringBefore('@')?.trim().orEmpty()
    if (name.isEmpty()) return null

    return name.split(' ', '.', '_')
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .ifEmpty { null }
}
