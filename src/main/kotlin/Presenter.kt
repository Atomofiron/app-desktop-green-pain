import Commands.LSUSB
import kotlin.system.exitProcess

class Presenter(private val viewModel: ViewModel) {
    companion object {
        private const val RULES_FILE_PATH = "/etc/udev/rules.d/51-android.rules"
    }

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
            is AppState.ErrorState -> viewModel.toDisconnectDeviceState()
            AppState.FinalState -> exitProcess(0)
        }
    }

    fun onDeviceClick(device: Device) = confirmDevice(device)

    fun onPasswordInput(password: String) {
        sudoPassword = password
        viewModel.withPassword(isError = false)
    }

    fun onPasswordConfirm() = when {
        sudoPassword.isEmpty() -> Unit
        Shell.sudo(sudoPassword) -> addDeviceToSystemRules(viewModel.devices!!.first())
        else -> {
            sudoPassword = ""
            viewModel.withPassword(isError = true)
        }
    }

    private fun confirmDevice(device: Device) = when {
        sudoPassword.isEmpty() -> viewModel.withPassword()
        else -> addDeviceToSystemRules(device)
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
        val rule = "SUBSYSTEM==\"usb\", ATTR{idVendor}==\"${device.idVendor}\", MODE=\"0666\", GROUP=\"plugdev\", SYMLINK+=\"android%n\""
        val result = Shell.exec("cat $RULES_FILE_PATH")
        when {
            !result.ok -> viewModel.toErrorState(result.error)
            result.output.split('\n').contains(rule) -> tryReloadRules()
            else -> tryAppendRule(rule)
        }
    }

    private fun tryAppendRule(rule: String) {
        val result = Shell.exec("echo '\n$rule' >> $RULES_FILE_PATH", sudoPassword)
        when {
            result.ok -> tryReloadRules()
            else -> viewModel.toErrorState(result.error)
        }
    }

    private fun tryReloadRules() {
        val result = Shell.exec("udevadm control --reload-rules && udevadm trigger", sudoPassword)
        if (!result.ok) {
            return viewModel.toErrorState(result.error)
        }
        viewModel.toFinalState()
    }
}