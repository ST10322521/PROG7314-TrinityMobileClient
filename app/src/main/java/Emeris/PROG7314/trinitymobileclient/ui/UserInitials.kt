package Emeris.PROG7314.trinitymobileclient.ui

// up to 2 initials, "ada lovelace" -> "AL", an email uses the part before the @
// used by the drawer, app bar and the Settings account card
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
