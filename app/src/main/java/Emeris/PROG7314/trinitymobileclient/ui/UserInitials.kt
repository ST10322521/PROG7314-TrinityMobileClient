package Emeris.PROG7314.trinitymobileclient.ui

//Up to two uppercase initials from a name or email.
//Shared by the drawer footer, app bar and the Settings account card.
//"ada lovelace" -> "AL", "ada@trinity.io" -> "A", blank/null -> null.
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
