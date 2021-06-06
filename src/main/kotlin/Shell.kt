object Shell {
    private const val ROOT = "root\n"

    fun sudo(sudoPassword: String): Boolean {
        val result = exec("whoami", sudoPassword)
        return result.ok && result.output == ROOT
    }

    fun exec(command: String, sudoPassword: String? = null): Result {
        val start = when (sudoPassword) {
            null -> "sh"
            else -> "sudo -S sh"
        }
        val process = Runtime.getRuntime().exec(start)
        val stream = process.outputStream
        val writer = stream.writer()
        if (sudoPassword != null) {
            writer.write(sudoPassword)
            writer.write("\n")
            writer.flush()
        }
        writer.write(command)
        writer.write("\n")
        writer.flush()
        stream.close()
        val ok = process.waitFor() == 0
        val output = process.inputStream.reader().readText()
        val error = process.errorStream.reader().readText()
        return Result(ok, output, error)
    }

    class Result(
        val ok: Boolean,
        val output: String,
        val error: String,
    )
}