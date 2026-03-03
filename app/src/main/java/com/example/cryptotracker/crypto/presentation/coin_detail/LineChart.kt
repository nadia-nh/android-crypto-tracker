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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    dataPoints: List<DataPoint> = emptyList(),
    visibleDataPointsIndices: IntRange= IntRange(0, 0),
    style: ChartStyle = ChartStyle(),
) {
    val visibleDataPoints = remember(dataPoints, visibleDataPointsIndices) {
        dataPoints.slice(visibleDataPointsIndices)
    }
    val measurer = rememberTextMeasurer()

    val textStyle = TextStyle.Default.copy(fontSize = style.labelFontSize)

    Canvas(
        modifier = modifier
    ) {
        val xLabelTextLayoutResults = visibleDataPoints.map {
            measurer.measure(
                text = it.xLabel,
                style = textStyle.copy(textAlign = TextAlign.Center)
            )
        }

        // X-coordinate vals
        val horizontalPaddingPx = style.horizontalPadding.toPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()

        val viewportRightX = size.width
        val viewportLeftX = 2 * horizontalPaddingPx

        val maxXLabelWidthPx = xLabelTextLayoutResults
            .maxOfOrNull { it.size.width } ?: 0

        // Y-coordinate vals
        val minLabelSpacingPx = style.minYLabelSpacing.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()

        val maxXLabelLineCount = xLabelTextLayoutResults
            .maxOfOrNull { it.lineCount } ?: 0

        val maxXLabelHeightPx = xLabelTextLayoutResults
            .maxOfOrNull { it.size.height } ?: 0
        val xLabelLineHeightPx = if (maxXLabelLineCount > 0) {
            maxXLabelHeightPx / maxXLabelLineCount.toFloat()
        } else {
            0f
        }

        val viewportHeightPx = size.height - (
                maxXLabelHeightPx + 2 * verticalPaddingPx + xLabelLineHeightPx + xAxisLabelSpacingPx)
        val viewportTopY = verticalPaddingPx + xLabelLineHeightPx + 10f
        val viewportBottomY = viewportTopY + viewportHeightPx

        val minYValue = visibleDataPoints
            .minOfOrNull { it.y } ?: 0f
        val maxYValue = visibleDataPoints
            .maxOfOrNull { it.y } ?: 0f

        // ----- Define the viewport and start drawing -----
        val viewport = Rect(
            top = viewportTopY,
            left = viewportLeftX,
            bottom = viewportBottomY,
            right = viewportRightX
        )

        val viewportWithXPadding = viewport.copy(
            left = viewport.left + horizontalPaddingPx * 10,
            right = viewport.right + horizontalPaddingPx * 10
        )

        drawBackground(
            drawScope = this,
            viewport = viewportWithXPadding)
        drawXAxisLabels(
            drawScope = this,
            labelTextLayoutResults = xLabelTextLayoutResults,
            viewport = viewportWithXPadding,
            maxXLabelWidthPx = maxXLabelWidthPx,
            xAxisLabelSpacingPx = xAxisLabelSpacingPx)
        drawYAxisLabels(
            drawScope = this,
            viewport = viewport,
            measurer = measurer,
            minLabelSpacingPx = minLabelSpacingPx,
            xLabelLineHeight = xLabelLineHeightPx,
            minYValue = minYValue,
            maxYValue = maxYValue
        )
    }
}

fun drawBackground(
    drawScope: DrawScope,
    viewport: Rect,
) {
    drawScope.drawRect(
        color = Color.Green,
        topLeft = viewport.topLeft,
        size = viewport.size,
    )
}
fun drawXAxisLabels(
    drawScope: DrawScope,
    labelTextLayoutResults: List<TextLayoutResult>,
    viewport: Rect,
    maxXLabelWidthPx: Int,
    xAxisLabelSpacingPx: Float
) {
    val xLabelWidthPx = maxXLabelWidthPx + xAxisLabelSpacingPx
    val xAxisLabelXPos = viewport.left + xAxisLabelSpacingPx / 2f
    val xAxisLabelYPos = viewport.bottom + xAxisLabelSpacingPx

    labelTextLayoutResults.forEachIndexed { index, result ->
        drawScope.drawText(
            textLayoutResult = result,
            topLeft = Offset(
                x = xAxisLabelXPos + xLabelWidthPx * index,
                y = xAxisLabelYPos
            )
        )
    }
}

fun drawYAxisLabels(
    drawScope: DrawScope,
    viewport: Rect,
    measurer: TextMeasurer,
    minLabelSpacingPx: Float,
    xLabelLineHeight: Float,
    minYValue: Float,
    maxYValue: Float
) {
    val yLabelMaxHeightPx = viewport.height + xLabelLineHeight
    val labelCount = (yLabelMaxHeightPx / (xLabelLineHeight + minLabelSpacingPx)).toInt()
    val valueIncrement = if (labelCount > 0) {
        (maxYValue - minYValue) / labelCount
    } else {
        return
    }

    val yLabels = (0..labelCount).map {
        ValueLabel(
            value = minYValue + valueIncrement * it,
            unit = "$"
        )
    }

    val yLabelTextLayoutResult = yLabels.map {
        measurer.measure(
            text = it.formatted(),
            style = TextStyle.Default.copy(textAlign = TextAlign.Center)
        )
    }

    val yLabelHeightPx = xLabelLineHeight + minLabelSpacingPx
    val yLabelXPos = viewport.left
    val yLabelYPos = viewport.bottom - xLabelLineHeight
    yLabelTextLayoutResult.forEachIndexed { index, result ->
        drawScope.drawText(
            textLayoutResult = result,
            topLeft = Offset(
                x = yLabelXPos,
                y = yLabelYPos - (yLabelHeightPx * index)
            )
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
    LineChart(
        modifier = Modifier
            .width(700.dp)
            .height(300.dp)
            .background(Color.White),
        dataPoints = dataPoints,
        visibleDataPointsIndices = IntRange(0, 15),
    )
}
