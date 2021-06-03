sealed class AppState(
    val topText: String,
    val btnText: String,
    val devices: List<Device>? = null,
    val sudoPassword: Boolean = false,
) {
    object DisconnectDevice : AppState(
        "Отключите Android-устройство от компьютера",
        "Готово",
    )
    class ConnectDevice(val extraDevices: List<Device>) : AppState(
        "Подключите Android-устройство к компьютеру",
        "Готово",
    )
    object DeviceNotFound : AppState(
        "Новых устройств не было найдено",
        "Ок",
    )
    class ChooseDevice(targetDevices: List<Device>, sudoPassword: Boolean = false) : AppState(
        "Выберете целевое Android-устройство",
        "Отмена",
        devices = targetDevices,
        sudoPassword = sudoPassword,
    ) {
        fun withPassword() = ChooseDevice(devices!!, true)
    }
    class ConfirmDevice(val device: Device, sudoPassword: Boolean = false) : AppState(
        "Устройство определено верно?",
        "Да",
        devices = listOf(device),
        sudoPassword = sudoPassword,
    ) {
        fun withPassword() = ConfirmDevice(device, true)
    }
    object FinalState : AppState(
        "Теперь должно работать",
        "Выйти",
    )
    class ErrorState(message: String) : AppState(message, "Понятно")
}