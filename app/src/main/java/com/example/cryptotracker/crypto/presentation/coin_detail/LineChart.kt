package com.example.cryptotracker.crypto.presentation.coin_detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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

        // X-coordinate
        val horizontalPaddingPx = style.horizontalPadding.roundToPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.roundToPx()

        val maxXLabelWidth = xLabelTextLayoutResults
            .maxOfOrNull { it.size.width } ?: 0
        val viewportRightX = size.width
        val viewportLeftX = 2 * horizontalPaddingPx.toFloat()

        // Y-coordinate
        val verticalPaddingPx = style.verticalPadding.roundToPx()

        val maxXLabelHeight = xLabelTextLayoutResults
            .maxOfOrNull { it.size.height } ?: 0
        val maxXLabelLineCount = xLabelTextLayoutResults
            .maxOfOrNull { it.lineCount } ?: 0

        val xLabelLineHeight = if (maxXLabelLineCount > 0) {
            maxXLabelHeight / maxXLabelLineCount
        } else {
            0
        }

        val viewportHeightPx = size.height - (
                maxXLabelHeight + 2 * verticalPaddingPx + xLabelLineHeight + xAxisLabelSpacingPx)
        val viewportTopY = verticalPaddingPx + xLabelLineHeight + 10f
        val viewportBottomY = viewportTopY + viewportHeightPx

        val viewport = Rect(
            top = viewportTopY,
            left = viewportLeftX,
            bottom = viewportBottomY,
            right = viewportRightX
        )

        drawRect(
            color = Color.Green,
            topLeft = viewport.topLeft,
            size = viewport.size,
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
                    .ofPattern("ha M/d")
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
        visibleDataPointsIndices = IntRange(0, 15)
    )
}
