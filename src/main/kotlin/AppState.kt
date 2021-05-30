sealed class AppState(
    val topText: String,
    val btnText: String,
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
    class ChooseDevice(val targetDevices: List<Device>) : AppState(
        "Выберете целевое Android-устройство",
        "Отмена",
    )
    class ConfirmDevice(val device: Device) : AppState(
        "Устройство определено верно?\n${device.name}",
        "Да",
    )
    object FinalState : AppState(
        "Теперь должно работать",
        "Выйти",
    )
    class ErrorState(message: String) : AppState(message, "Понятно")
}