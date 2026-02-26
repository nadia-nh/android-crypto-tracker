package com.example.cryptotracker.crypto.presentation.coin_detail

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType.Companion.Sp

data class ChartStyle(
    val chartLineColor: Color = Color.Black,
    val unselectedColor: Color = Color.DarkGray,
    val selectedColor: Color = Color.Black,
    val helperLinesThicknessPx: Float = 1.0f,
    val axisLineThicknessPx: Float = 5.0f,
    val labelFontSize: TextUnit = TextUnit(14f, Sp),
    val minYLabelSpacing: Dp = Dp(25f),
    val verticalPadding: Dp = Dp(8f),
    val horizontalPadding: Dp = Dp(8f),
    val xAxisLabelSpacing: Dp = Dp(8f)
)
