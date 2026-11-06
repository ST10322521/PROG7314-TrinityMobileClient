package Emeris.PROG7314.trinitymobileclient.ui

// up to 2 initals. "ada lovelace" -> "AL". emails use the part before the @
// used by the drawer, app bar and the Settings acount card
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
