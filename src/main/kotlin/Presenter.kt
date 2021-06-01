import Commands.LSUSB
import kotlin.system.exitProcess

class Presenter(private val viewModel: ViewModel) {

    fun onButtonClick() {
        when (val state = viewModel.appState) {
            AppState.DisconnectDevice -> {
                val extraDevices = discoverDevices() ?: return
                viewModel.toConnectDeviceState(extraDevices)
            }
            is AppState.ConnectDevice -> {
                val allDevices = discoverDevices() ?: return
                val targetDevices = findTargetDevices(state.extraDevices, allDevices)
                when {
                    targetDevices.isEmpty() -> viewModel.toDeviceNotFoundState()
                    targetDevices.size == 1 -> viewModel.toConfirmDeviceState(targetDevices.first())
                    else -> viewModel.toChooseDeviceState(targetDevices)
                }
            }
            AppState.DeviceNotFound -> viewModel.toDisconnectDeviceState()
            is AppState.ChooseDevice -> viewModel.toDisconnectDeviceState()
            is AppState.ConfirmDevice -> confirmDevice(state.device)
            AppState.FinalState -> exitProcess(0)
        }
    }

    fun onDeviceClick(device: Device) = confirmDevice(device)

    private fun confirmDevice(device: Device) {
        addDeviceToSystemRules(device)
        viewModel.toFinalState()
    }

    private fun discoverDevices(): List<Device>? {
        val result = Shell.exec(LSUSB)
        return when {
            !result.ok -> {
                viewModel.toErrorState(result.error)
                null
            }
            result.output.isBlank() -> listOf()
            else -> result.output.lineSequence().mapNotNull {
                val parts = it.split(" ", limit = 7)
                when {
                    parts.size < 7 -> null
                    parts[4] != "ID" -> null
                    else -> {
                        val idVendor = parts[5].split(":")[0]
                        Device(idVendor, parts[6])
                    }
                }
            }.toList()
        }
    }

    private fun findTargetDevices(extraDevices: List<Device>, allDevices: List<Device>): List<Device> {
        return allDevices.filter { !extraDevices.contains(it) }
    }

    private fun addDeviceToSystemRules(device: Device) {
        //etc/udev/rules.d/51-android.rules
        //SUBSYSTEM=="usb", ATTR{idVendor}=="2a70", MODE="0666", GROUP="plugdev", SYMLINK+="android%n"
    }
}