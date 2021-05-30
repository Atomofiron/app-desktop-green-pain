import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
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
        modifier = Modifier.fillMaxSize().padding(8.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            content()
        }
    }
}

@Composable
fun content() {
    Text(
        viewModel.topText,
        modifier = Modifier.fillMaxHeight(0.5f).padding(8.dp),
        style = TextStyle(textAlign = TextAlign.Center),
    )
    Button(
        modifier = Modifier.padding(8.dp),
        onClick = presenter::onButtonClick
    ) {
        Text(viewModel.btnText)
    }
}


