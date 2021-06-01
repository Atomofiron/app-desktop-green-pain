sealed class AppState(
    val topText: String,
    val btnText: String,
    val devices: List<Device>? = null,
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
    class ChooseDevice(targetDevices: List<Device>) : AppState(
        "Выберете целевое Android-устройство",
        "Отмена",
        devices = targetDevices,
    )
    class ConfirmDevice(val device: Device) : AppState(
        "Устройство определено верно?",
        "Да",
        devices = listOf(device),
    )
    object FinalState : AppState(
        "Теперь должно работать",
        "Выйти",
    )
    class ErrorState(message: String) : AppState(message, "Понятно")
}