package com.example.cryptotracker.crypto.presentation.coin_detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptotracker.crypto.domain.CoinPrice
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    dataPoints: List<DataPoint> = emptyList(),
    visibleDataPointsIndices: IntRange= IntRange(0, 0),
    style: ChartStyle = ChartStyle(),
    showHelperLines: Boolean = true
) {
    val visibleDataPoints = remember(dataPoints, visibleDataPointsIndices) {
        dataPoints.slice(visibleDataPointsIndices)
    }
    val measurer = rememberTextMeasurer()
    val textStyle = TextStyle.Default.copy(fontSize = style.labelFontSize)
    val state = remember { LineChartState() }

    Canvas(
        modifier = modifier
    ) {
        // Update the state, define the viewports, and draw
        updateState(
            drawScope = this,
            state = state,
            measurer = measurer,
            textStyle = textStyle,
            style = style,
            visibleDataPoints = visibleDataPoints)

        val viewport = Rect(
            left = state.viewportLeftX,
            top = state.viewportTopY,
            right = state.viewportRightX,
            bottom = state.viewportBottomY,
        )

        val viewportWithXPadding = viewport.copy(
            left = viewport.left + state.horizontalPaddingPx * 10,
            right = viewport.right + state.horizontalPaddingPx * 10
        )

        drawBackground(
            drawScope = this,
            viewport = viewportWithXPadding)
        drawXAxisLabels(
            drawScope = this,
            viewport = viewportWithXPadding,
            labelTextLayoutResults = state.xAxisLabelTextLayoutResults,
            xLabelWidthPx = state.xAxisLabelWidthPx,
            xAxisLabelBaseXPos = state.xAxisLabelBaseXPos,
            xAxisLabelBaseYPos = state.xAxisLabelBaseYPos)
        drawYAxisLabels(
            drawScope = this,
            viewport = viewport,
            textLayoutResults = state.yAxisLabelTextLayoutResult,
            yAxisLabelHeightPx = state.yAxisLabelHeightPx,
            yAxisLabelBaseXPos = state.yAxisLabelBaseXPos,
            yAxisLabelBaseYPos = state.yAxisLabelBaseYPos)
        drawPoints(
            drawScope = this,
            viewport = viewportWithXPadding,
            dataPoints = state.dataPoints,
            radius = state.dataPointRadius,
            color = style.selectedColor)
        drawPointLines(
            drawScope = this,
            viewport = viewportWithXPadding,
            dataPoints = state.dataPoints,
            lineWidth = state.dataPointLineWidth,
            color = style.chartLineColor)

        if (showHelperLines) {
            drawHelperLinesHorizontal(
                drawScope = this,
                viewport = viewportWithXPadding,
                textLayoutResults = state.xAxisLabelTextLayoutResults,
                xAxisLabelWidthPx = state.xAxisLabelWidthPx,
                xAxisLabelBaseXPos = state.xAxisLabelBaseXPos,
                lineWidth = style.helperLinesThicknessPx,
                color = style.unselectedColor
            )

            drawHelperLinesVertical(
                drawScope = this,
                viewport = viewport,
                textLayoutResults = state.yAxisLabelTextLayoutResult,
                yAxisLabelHeightPx = state.yAxisLabelHeightPx,
                yAxisLabelBaseYPos = state.yAxisLabelBaseYPos,
                lineWidth = style.helperLinesThicknessPx,
                color = style.unselectedColor
            )
        }
    }
}

fun updateState(
    drawScope: DrawScope,
    state: LineChartState,
    measurer: TextMeasurer,
    textStyle: TextStyle,
    style: ChartStyle = ChartStyle(),
    visibleDataPoints: List<DataPoint> = emptyList()
) {
    // Convert styles values from Dp to Px
    with(drawScope) {
        state.horizontalPaddingPx = style.horizontalPadding.toPx()
        state.verticalPaddingPx = style.verticalPadding.toPx()
        state.xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()
        state.yAxisLabelMinSpacingPx = style.minYLabelSpacing.toPx()
    }

    state.xAxisLabelTextLayoutResults = visibleDataPoints.map {
        measurer.measure(
            text = it.xLabel,
            style = textStyle.copy(textAlign = TextAlign.Center)
        )
    }

    // Values needed for the viewport
    state.xAxisMaxLineCount = state.xAxisLabelTextLayoutResults
        .maxOfOrNull { it.lineCount } ?: 0
    state.xAxisLabelMaxHeightPx = state.xAxisLabelTextLayoutResults
        .maxOfOrNull { it.size.height } ?: 0
    state.xAxisLabelHeightPx = if (state.xAxisMaxLineCount > 0) {
        state.xAxisLabelMaxHeightPx / state.xAxisMaxLineCount.toFloat()
    } else {
        0f
    }
    state.viewportHeightPx = drawScope.size.height - (
            state.xAxisLabelMaxHeightPx +
                    2 * state.verticalPaddingPx +
                    state.xAxisLabelMaxHeightPx +
                    state.xAxisLabelSpacingPx)

    // Viewport values
    state.viewportLeftX = 2 * state.horizontalPaddingPx
    state.viewportTopY = state.verticalPaddingPx + state.xAxisLabelMaxHeightPx + 10f
    state.viewportRightX = drawScope.size.width
    state.viewportBottomY = state.viewportTopY + state.viewportHeightPx


    // X-axis label values
    state.xAxisLabelMaxWidthPx = state.xAxisLabelTextLayoutResults
        .maxOfOrNull { it.size.width } ?: 0

    state.xAxisLabelWidthPx = state.xAxisLabelMaxWidthPx + state.xAxisLabelSpacingPx
    state.xAxisLabelBaseXPos = state.xAxisLabelSpacingPx / 2f
    state.xAxisLabelBaseYPos = state.xAxisLabelSpacingPx

    // Y-axis label values
    state.yAxisMinValue = visibleDataPoints.minOfOrNull { it.y } ?: 0f
    state.yAxisMaxValue = visibleDataPoints.maxOfOrNull { it.y } ?: 0f

    state.yAxisLabelMaxHeightPx = state.viewportHeightPx + state.xAxisLabelHeightPx
    state.yAxisItemCount = (state.yAxisLabelMaxHeightPx /
            (state.xAxisLabelHeightPx + state.yAxisLabelMinSpacingPx)).toInt()
    state.yAxisValueRange = state.yAxisMaxValue - state.yAxisMinValue
    state.yAxisValueIncrement = if (state.yAxisItemCount > 0) {
        state.yAxisValueRange / state.yAxisItemCount
    } else {
        0f
    }

    state.yAxisValueLabels = (0..state.yAxisItemCount).map {
        ValueLabel(
            value = state.yAxisMinValue + state.yAxisValueIncrement * it,
            unit = "$"
        )
    }

    state.yAxisLabelTextLayoutResult = state.yAxisValueLabels.map {
        measurer.measure(
            text = it.formatted(),
            style = textStyle.copy(textAlign = TextAlign.Center)
        )
    }

    state.yAxisLabelHeightPx = state.xAxisLabelHeightPx + state.yAxisLabelMinSpacingPx
    state.yAxisLabelBaseXPos = 0f
    state.yAxisLabelBaseYPos = -state.yAxisLabelHeightPx / 2

    // Point and line values
    state.dataPointRadius = 5f
    state.dataPoints = visibleDataPoints.mapIndexed { index, point ->
        // Map it to the [0, 1] range
        val yInAxisValueRange = (point.y - state.yAxisMinValue) / state.yAxisValueRange

        DataPoint(
            x = index * state.xAxisLabelWidthPx + state.xAxisLabelWidthPx / 2f,
            y = -yInAxisValueRange * state.viewportHeightPx - state.dataPointRadius / 2,
            xLabel = point.xLabel
        )
    }

    state.dataPointLineWidth = 1.5f
}

fun drawBackground(
    drawScope: DrawScope,
    viewport: Rect,
) {
    drawScope.drawRect(
        color = Color.Green.copy(alpha = 0.5f),
        topLeft = viewport.topLeft,
        size = viewport.size,
    )
}

fun drawXAxisLabels(
    drawScope: DrawScope,
    viewport: Rect,
    labelTextLayoutResults: List<TextLayoutResult>,
    xLabelWidthPx: Float,
    xAxisLabelBaseXPos: Float,
    xAxisLabelBaseYPos: Float
) {
    labelTextLayoutResults.forEachIndexed { index, result ->
        drawScope.drawText(
            textLayoutResult = result,
            topLeft = Offset(
                x = viewport.left + xAxisLabelBaseXPos + xLabelWidthPx * index,
                y = viewport.bottom + xAxisLabelBaseYPos
            )
        )
    }
}

fun drawYAxisLabels(
    drawScope: DrawScope,
    viewport: Rect,
    textLayoutResults: List<TextLayoutResult>,
    yAxisLabelHeightPx: Float,
    yAxisLabelBaseXPos: Float,
    yAxisLabelBaseYPos: Float,
) {
    textLayoutResults.forEachIndexed { index, result ->
        drawScope.drawText(
            textLayoutResult = result,
            topLeft = Offset(
                x = viewport.left + yAxisLabelBaseXPos,
                y = viewport.bottom + yAxisLabelBaseYPos - (yAxisLabelHeightPx * index)
            )
        )
    }
}

fun drawPoints(
    drawScope: DrawScope,
    viewport: Rect,
    dataPoints: List<DataPoint>,
    radius: Float,
    color: Color = Color.Red,
) {
    dataPoints.forEach { dataPoint ->
        drawScope.drawCircle(
            color = color,
            radius = radius,
            center = Offset(
                x = viewport.left + dataPoint.x,
                y = viewport.bottom + dataPoint.y
            )
        )
    }
}

fun drawPointLines(
    drawScope: DrawScope,
    viewport: Rect,
    dataPoints: List<DataPoint>,
    lineWidth: Float,
    color: Color = Color.Red,
) {
    dataPoints.zipWithNext().forEach { (current, next) ->
        drawScope.drawLine(
            color = color,
            start = Offset(
                x = viewport.left + current.x,
                y = viewport.bottom + current.y),
            end = Offset(
                x = viewport.left + next.x,
                y = viewport.bottom + next.y
            ),
            strokeWidth = lineWidth
        )
    }
}


fun drawHelperLinesHorizontal(
    drawScope: DrawScope,
    viewport: Rect,
    textLayoutResults: List<TextLayoutResult>,
    xAxisLabelWidthPx: Float,
    xAxisLabelBaseXPos: Float,
    lineWidth: Float,
    color: Color = Color.Red,
) {
    textLayoutResults.forEachIndexed { index, result ->
        val halfWidth = result.size.width / 2f
        val xValue = viewport.left + xAxisLabelBaseXPos + halfWidth
        drawScope.drawLine(
            color = color,
            start = Offset(
                x = xValue + xAxisLabelWidthPx * index,
                y = viewport.bottom
            ),
            end = Offset(
                x = xValue + xAxisLabelWidthPx * index,
                y = viewport.top
            ),
            strokeWidth = lineWidth
        )
    }
}

fun drawHelperLinesVertical(
    drawScope: DrawScope,
    viewport: Rect,
    textLayoutResults: List<TextLayoutResult>,
    yAxisLabelHeightPx: Float,
    yAxisLabelBaseYPos: Float,
    lineWidth: Float,
    color: Color = Color.Red,
) {
    textLayoutResults.forEachIndexed { index, result ->
        val halfHeight = result.size.height.toFloat() / 2f
        val yValue = viewport.bottom + yAxisLabelBaseYPos + halfHeight
        drawScope.drawLine(
            color = color,
            start = Offset(
                x = viewport.left,
                y = yValue - (yAxisLabelHeightPx * index)
            ),
            end = Offset(
                x = viewport.right,
                y = yValue - (yAxisLabelHeightPx * index)
            ),
            strokeWidth = lineWidth
        )
    }
}

@Preview
@Composable
fun LineChartPreview() {
    val coinHistoryRandomized = remember {
        (1..20).map {
            CoinPrice(
                priceUsd = Random.nextFloat() * 1000.0,
                dateTime = ZonedDateTime.now().plusHours(it.toLong())
            )
        }
    }
    val dataPoints = remember {
        coinHistoryRandomized.map {
            DataPoint(
                x = it.dateTime.hour.toFloat(),
                y = it.priceUsd.toFloat(),
                xLabel = DateTimeFormatter
                    .ofPattern("ha\nM/d")
                    .format(it.dateTime)
            )
        }
    }

    CryptoTrackerTheme() {
        LineChart(
            modifier = Modifier
                .width(800.dp)
                .height(300.dp)
                .background(Color.White),
            dataPoints = dataPoints,
            visibleDataPointsIndices = IntRange(0, 15),
            style = ChartStyle(
                chartLineColor = Color.LightGray,
                unselectedColor = Color.White,
                selectedColor = Color.DarkGray
            )
        )
    }
}
