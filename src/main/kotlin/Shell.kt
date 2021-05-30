object Shell {

    fun exec(c: String): Result {
        val process = Runtime.getRuntime().exec(c)
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