import androidx.compose.desktop.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


val Int.dpSize: Int get() = this.dp.value.toInt()
val viewModel: ViewModel = ViewModel()
val presenter: Presenter = Presenter(viewModel)

fun main() = Window(
    title = "Зелёная Боль",
    size = IntSize(320.dpSize, 320.dpSize),
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        content()
        controls()
    }
}

@Composable
fun content() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        title()
        list()
    }
}

@Composable
fun title() = Card(
    elevation = 5.dp,
    // padding(bottom = 4.dp) is a bad idea, but this is the best solution now
    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
) {
    Text(
        viewModel.topText,
        modifier = Modifier.padding(16.dp),
        style = TextStyle(textAlign = TextAlign.Center),
    )
}

@Composable
fun list() = viewModel.devices?.let { devices ->
    LazyColumn(
        contentPadding = PaddingValues(bottom = 72.dp),
    ) {
        items(devices) { device ->
            listItem(device)
        }
    }
}

@Composable
fun listItem(device: Device) = Card(
    elevation = 4.dp,
    modifier = Modifier.fillMaxWidth()
        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
        .clickable(onClick = {
            presenter.onDeviceClick(device)
        }),
) {
    Text(
        device.name,
        modifier = Modifier.padding(8.dp),
    )
}

@Composable
fun controls() = when {
    viewModel.password -> passwordField()
    else -> button()
}

@Composable
fun button() = Button(
    modifier = Modifier.padding(16.dp),
    onClick = presenter::onButtonClick,
) {
    Text(viewModel.btnText)
}

@Composable
fun passwordField() {
    var value by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    TextField(
        value,
        label = { Text(
            when {
                viewModel.sudoPasswordError -> "sudo: incorrect password"
                else -> "sudo password"
            }
        )},
        isError = viewModel.sudoPasswordError,
        textStyle = TextStyle.Default.copy(),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        singleLine = true,
        onValueChange = {
            value = it
            presenter.onPasswordInput(it)
        },
        keyboardActions = KeyboardActions(
            onDone = {
                presenter.onPasswordConfirm()
            },
        ),
        modifier = Modifier.padding(16.dp).focusRequester(focusRequester).onKeyEvent {
            val isEnter = it.key == Key.Enter
            if (isEnter && it.type == KeyEventType.KeyDown) {
                presenter.onPasswordConfirm()
            }
            isEnter
        },
    )
    post {
        focusRequester.requestFocus()
    }
}

@Composable
private fun post(action: () -> Unit) {
    DisposableEffect(Unit) {
        action()
        onDispose { }
    }
}


