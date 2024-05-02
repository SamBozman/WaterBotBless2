package com.example.waterbotbless2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TargetControl() {
    Column(
        modifier = Modifier
            .background(Color.Green)
            .padding(5.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Target Control")
        WaterBotSlider()
        WaterBotButtons()
    }
}

@Preview
@Composable
fun WaterBotSlider() {
    var sliderPosition by remember { mutableFloatStateOf(100f) }
    Column(
        horizontalAlignment = Alignment.End
    ) {
        Text(text = (sliderPosition.toInt() - 100).toString())
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 200,
            valueRange = 0f..200f
        )
    }
}



@Preview
@Composable
fun WaterBotButtons(){
    val buttonSpacing = 8.dp
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(buttonSpacing)

    ) {
        WaterBotButton(
            symbol = "HORZ",
            color = Red,
            modifier = Modifier
                .aspectRatio(2f)
                .weight(2f)
        ) {
            //TODO
        }
        WaterBotButton(
            symbol = "VERT",
            color = Magenta,
            modifier = Modifier
                .aspectRatio(2f)
                .weight(2f)
        ) {
            //TODO
        }
        WaterBotButton(
            symbol = "SPRAY",
            color = DarkGray,
            modifier = Modifier
                .aspectRatio(2f)
                .weight(2f)
        ) {
            //TODO
        }
        WaterBotButton(
            symbol = "WATER",
            color = Blue,
            modifier = Modifier
                .aspectRatio(2f)
                .weight(2f)
        ) {
            //TODO
        }

    }


}
