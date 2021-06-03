import Commands.LSUSB
import java.lang.IllegalStateException
import kotlin.system.exitProcess

class Presenter(private val viewModel: ViewModel) {

    private var sudoPassword: String = ""

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

    fun onPasswordInput(password: String) {
        sudoPassword = password
    }

    fun onPasswordConfirm() {
        addDeviceToSystemRules(viewModel.devices!!.first())
    }

    private fun confirmDevice(device: Device) {
        if (sudoPassword.isEmpty()) {
            var state = viewModel.appState
            state = when (state) {
                is AppState.ChooseDevice -> state.withPassword()
                is AppState.ConfirmDevice -> state.withPassword()
                else -> throw IllegalStateException()
            }
            viewModel.toState(state)
        } else {
            addDeviceToSystemRules(device)
        }
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
            .toMutableList().apply {
                add(Device("1234", "Oneplus 7T"))
            }
    }

    private fun addDeviceToSystemRules(device: Device) {
        //etc/udev/rules.d/51-android.rules
        //SUBSYSTEM=="usb", ATTR{idVendor}=="2a70", MODE="0666", GROUP="plugdev", SYMLINK+="android%n"
        val result = Shell.exec("sudo ls")
        println("output ${result.output}")
        println("error ${result.error}")
    }
}