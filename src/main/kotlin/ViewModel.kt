import androidx.compose.runtime.*
import java.lang.IllegalStateException

class ViewModel {
    lateinit var appState: AppState
        private set
    var topText: String by mutableStateOf("")
        private set
    var btnText: String by mutableStateOf("")
        private set
    var devices: List<Device>? by mutableStateOf(null)
        private set
    var password: Boolean by mutableStateOf(false)
        private set
    var sudoPasswordError: Boolean by mutableStateOf(false)
        private set

    init {
        update(AppState.DisconnectDevice)
    }

    private fun update(state: AppState) {
        appState = state
        topText = state.topText
        btnText = state.btnText
        devices = state.devices
        password = state.sudoPassword
        sudoPasswordError = state.sudoPasswordError
    }

    fun toDisconnectDeviceState() = update(AppState.DisconnectDevice)

    fun toConnectDeviceState(extraDevices: List<Device>) = update(AppState.ConnectDevice(extraDevices))

    fun toDeviceNotFoundState() = update(AppState.DeviceNotFound)

    fun toChooseDeviceState(targetDevices: List<Device>) = update(AppState.ChooseDevice(targetDevices))

    fun toConfirmDeviceState(device: Device) = update(AppState.ConfirmDevice(device))

    fun toFinalState() = update(AppState.FinalState)

    fun toErrorState(message: String) = update(AppState.ErrorState(message))

    fun withPassword(isError: Boolean = false) {
        when (val state = appState) {
            is AppState.ChooseDevice -> state.withPassword(isError)
            is AppState.ConfirmDevice -> state.withPassword(isError)
            else -> throw IllegalStateException()
        }.let { update(it) }
    }
}


