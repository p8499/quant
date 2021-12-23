package org.p8499.quant.analysis

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtils
import org.jfree.data.category.DefaultCategoryDataset
import java.io.File
import java.util.*

fun jpg(title: String, dateList: List<Date>, vararg pairList: Pair<String, List<Double?>>) {
    val dataset = DefaultCategoryDataset()
    for (pair in pairList)
        pair.second.forEachIndexed { i, value -> dataset.addValue(value, pair.first, dateList[i]) }
    val chart = ChartFactory.createLineChart(title, "Date", "Value", dataset)
    ChartUtils.saveChartAsJPEG(File("$title.jpg"), chart, 1280, 1024)
}