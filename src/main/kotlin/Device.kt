class Device(
    val idVendor: String,
    val name: String,
) {
    override fun equals(other: Any?): Boolean {
        return when {
            other !is Device -> false
            other.idVendor != idVendor -> false
            other.name != name -> false
            else -> true
        }
    }

    override fun hashCode(): Int = idVendor.hashCode() + name.hashCode()
}
