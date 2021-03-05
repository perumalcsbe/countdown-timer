/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Segment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.ui.components.AnimatedCircle
import com.example.androiddevchallenge.ui.theme.Green500
import com.example.androiddevchallenge.ui.theme.pauseButton
import com.example.androiddevchallenge.ui.theme.pauseText
import com.example.androiddevchallenge.ui.theme.playButton
import com.example.androiddevchallenge.ui.theme.playText
import com.example.androiddevchallenge.ui.theme.stopText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DELAY_MILLIS = 500L
private const val TIME_LIMIT = 362f
private const val WARNING_LIMIT = 12
private const val ALERT_LIMIT = 6
private const val TIMER_LOW_RESET = 2
enum class ButtonState {
    Play,
    Pause,
    Resume
}

var timerInFloat by mutableStateOf(TIME_LIMIT)
var isPlaying by mutableStateOf(false)
var threshold by mutableStateOf("")

suspend fun startTimer() {
    while (isPlaying) {
        timerInFloat -= 1
        // change threshold color
        if (timerInFloat <= WARNING_LIMIT) {
            threshold = if (timerInFloat <= ALERT_LIMIT) "alert" else "warning"
        }
        if (timerInFloat <= TIMER_LOW_RESET) {
            timerInFloat = TIME_LIMIT
            isPlaying = false
            threshold = ""
        }
        delay(DELAY_MILLIS)
    }
}

@ExperimentalAnimationApi
@Composable
fun CountDownTimerContent() {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                ConstraintLayout(
                    Modifier
                        .fillMaxWidth()
                        .size(220.dp)
                        .padding(top = 20.dp)
                ) {
                    val (box, circle, text, bg, label) = createRefs()
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .constrainAs(box) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )

                    AnimatedCircle(
                        color = Color(0xFFd3d3d3),
                        sweep = 362f,
                        modifier = Modifier
                            .height(190.dp)
                            .fillMaxWidth()
                            .constrainAs(bg) {
                                top.linkTo(box.top, margin = 5.dp)
                                start.linkTo(box.start)
                                end.linkTo(box.end)
                            }
                    )
                    AnimatedCircle(
                        color = Green500,
                        sweep = timerInFloat,
                        modifier = Modifier
                            .height(190.dp)
                            .fillMaxWidth()
                            .constrainAs(circle) {
                                top.linkTo(bg.top)
                                start.linkTo(bg.start)
                                end.linkTo(bg.end)
                            }
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .constrainAs(text) {
                                top.linkTo(circle.top, margin = 4.dp)
                                start.linkTo(circle.start, margin = 4.dp)
                                end.linkTo(circle.end, margin = 4.dp)
                                bottom.linkTo(circle.bottom, margin = 4.dp)
                            }
                    ) {
                        val seconds = (((timerInFloat - 2) % 120) / 2).toInt()
                        val mins = ((timerInFloat - 2) / 120).toInt()
                        val textColor by animateColorAsState(
                            when (threshold) {
                                "warning" -> pauseText
                                "alert" -> stopText
                                else -> MaterialTheme.colors.onBackground
                            }
                        )
                        Text(
                            text = "${if (mins < 10) "0$mins" else mins}",
                            style = MaterialTheme.typography.h5,
                            color = textColor,
                            modifier = Modifier
                                .border(width = 2.dp, color = Color(0xFFd3d3d3), RoundedCornerShape(10))
                                .padding(4.dp)
                        )
                        Text(
                            text = "${if (seconds < 10) "0$seconds" else seconds}",
                            style = MaterialTheme.typography.h5,
                            color = textColor,
                            modifier = Modifier
                                .border(width = 2.dp, color = Color(0xFFd3d3d3), RoundedCornerShape(10))
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
        item {

            CountDownTimerControls(
                onPause = {
                    isPlaying = false
                },
                onStop = {
                    isPlaying = false
                    timerInFloat = TIME_LIMIT
                    coroutineScope.launch {
                        startTimer()
                    }
                },
                onPlay = {
                    isPlaying = true
                    coroutineScope.launch {
                        startTimer()
                    }
                }
            )
        }
    }
}

@Composable
fun CountDownTimerAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Countdown Timer"
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Filled.Segment,
                contentDescription = "menu"
            )
        }
    )
}

@ExperimentalAnimationApi
@Composable
fun CountDownTimerControls(
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    var buttonState by mutableStateOf(if (isPlaying) ButtonState.Pause else ButtonState.Play)

    Row(
        modifier = Modifier
            .padding(
                start = 48.dp,
                end = 48.dp,
                bottom = 24.dp,
                top = 100.dp
            )
            .fillMaxWidth()
    ) {
        CancelButton(
            onClick = {
                buttonState = ButtonState.Play
                onStop()
            },
            modifier = modifier, // .size(100.dp),
            buttonState != ButtonState.Play
        )
        Spacer(modifier = Modifier.weight(1f))
        val text = when (buttonState) {
            ButtonState.Pause -> "Pause"
            ButtonState.Resume -> "Resume"
            else -> "Start"
        }
        val backgroundColor = when (buttonState) {
            ButtonState.Pause -> pauseButton
            else -> playButton
        }
        val contentColor = when (buttonState) {
            ButtonState.Pause -> pauseText
            else -> playText
        }
        val imageVector = when (buttonState) {
            ButtonState.Pause -> Icons.Filled.Pause
            else -> Icons.Filled.PlayArrow
        }
        ChangeableButton(
            onClick = {
                when (buttonState) {
                    ButtonState.Play -> {
                        onPlay()
                        buttonState = ButtonState.Pause
                    }

                    ButtonState.Pause -> {
                        buttonState = ButtonState.Resume
                        onPause()
                    }
                    ButtonState.Resume -> {
                        buttonState = ButtonState.Pause
                        onPlay()
                    }
                }
            },
            modifier = modifier,
            text = text,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            imageVector = imageVector
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean
) {
    AnimatedVisibility(visible = visible) {
        OutlinedButton(
            onClick = onClick,
            shape = CircleShape,
            modifier = modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFd3d3d3),
                contentColor = Color.Gray,
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Replay,
                contentDescription = "Reset"
            )
            Spacer(modifier = Modifier.padding(start = 4.dp))
            Text(text = "Reset")
        }
    }
}

@Composable
fun ChangeableButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = playButton,
    contentColor: Color = playText,
    text: String = "Start",
    imageVector: ImageVector = Icons.Filled.PlayArrow
) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = text
        )
        Spacer(modifier = Modifier.padding(start = 4.dp))
        Text(text)
    }
}
