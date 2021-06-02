sealed class AppState(
    val topText: String,
    val btnText: String,
    val devices: List<Device>? = null,
    val sudoPassword: String? = null,
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
    class ChooseDevice(targetDevices: List<Device>, sudoPassword: String? = null) : AppState(
        "Выберете целевое Android-устройство",
        "Отмена",
        devices = targetDevices,
        sudoPassword = sudoPassword,
    )
    class ConfirmDevice(val device: Device, sudoPassword: String? = null) : AppState(
        "Устройство определено верно?",
        "Да",
        devices = listOf(device),
        sudoPassword = sudoPassword,
    )
    object FinalState : AppState(
        "Теперь должно работать",
        "Выйти",
    )
    class ErrorState(message: String) : AppState(message, "Понятно")
}