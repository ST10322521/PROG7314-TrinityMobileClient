package Emeris.PROG7314.trinitymobileclient.data.command.dto

/**
 * DTO for a shell command.
 *
 * Console format:
 *   shell <commandlet>
 *
 * Examples:
 *   shell whoami
 *   shell ipconfig /all
 *   shell Get-Process | Where-Object { $_.CPU -gt 10 }
 *
 * Everything after the "shell" keyword is treated as the commandlet,
 * preserving spaces so multi-word commands work correctly.
 */
data class ShellCommandDto(
    val commandlet: String
)
