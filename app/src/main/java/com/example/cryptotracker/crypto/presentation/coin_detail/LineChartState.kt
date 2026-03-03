package com.example.cryptotracker.crypto.presentation.coin_detail

import androidx.compose.ui.text.TextLayoutResult

class LineChartState {
    // Padding values
    var horizontalPaddingPx: Float = 0f
    var verticalPaddingPx: Float = 0f

    // Viewport values
    var viewportLeftX: Float = 0f
    var viewportTopY: Float = 0f
    var viewportRightX: Float = 0f
    var viewportBottomY: Float = 0f

    var viewportHeightPx: Float = 0f

    // X-axis label values
    var xAxisLabelTextLayoutResults: List<TextLayoutResult> = emptyList()

    var xAxisMaxLineCount: Int = 0

    var xAxisLabelSpacingPx: Float = 0f
    var xAxisLabelWidthPx: Float = 0f
    var xAxisLabelHeightPx: Float = 0f
    var xAxisLabelMaxWidthPx: Int = 0
    var xAxisLabelMaxHeightPx: Int = 0
    var xAxisLabelBaseXPos: Float = 0f
    var xAxisLabelBaseYPos: Float = 0f

    // Y-axis label values
    var yAxisValueLabels: List<ValueLabel> = emptyList()
    var yAxisLabelTextLayoutResult: List<TextLayoutResult> = emptyList()

    var yAxisItemCount: Int = 0
    var yAxisMinValue: Float = 0f
    var yAxisMaxValue: Float = 0f
    var yAxisValueRange: Float = 0f
    var yAxisValueIncrement: Float = 0f

    var yAxisLabelMinSpacingPx: Float = 0f

    var yAxisLabelMaxHeightPx: Float = 0f
    var yAxisLabelHeightPx: Float = 0f
    var yAxisLabelBaseXPos: Float = 0f
    var yAxisLabelBaseYPos: Float = 0f

    // Point values
    var dataPointRadius: Float = 1f
    var dataPoints: List<DataPoint> = emptyList()
}
