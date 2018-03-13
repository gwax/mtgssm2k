package com.gwax.mtgssm.io

import com.gwax.scryfall.ScryfallData
import org.apache.poi.ss.usermodel.BuiltinFormats
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.RegionUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

fun fillWorkbook(wb: XSSFWorkbook) {
    val allSetsSheet = wb.createSheet("All Sets")
    val allCardsSheet = wb.createSheet("All Cards")
    val scryfallData = ScryfallData(File(System.getProperty("user.home")).resolve(".mtg_ssm"))
    val allSets = scryfallData.sets.sortedWith(compareBy({ it.releasedAt }, { it.code }))
    val allCards = scryfallData.cards.sortedWith(compareBy(
        { it.set }, { it.collectorNumber }, { it.name }, { it.id }))

    val cellStyle = wb.createCellStyle()
    cellStyle.dataFormat = wb.creationHelper.createDataFormat().getFormat("m/d/yyyy")

    allSets.forEachIndexed { idx, set ->
        val row = allSetsSheet.createRow(idx)
        row.createCell(0).setCellValue(set.code)
        row.createCell(1).setCellValue(set.name)
        val cell2 = row.createCell(2)
        cell2.setCellValue(set.releasedAt?.toDate())
        cell2.cellStyle = cellStyle
    }
    allSetsSheet.setDefaultColumnStyle(2, cellStyle)

    allCards.forEachIndexed { idx, card ->
        val row = allCardsSheet.createRow(idx)
        row.createCell(0).setCellValue(card.set)
        row.createCell(1).setCellValue(card.name)
        row.createCell(2).setCellValue(card.collectorNumber)
    }
}

fun makeWorkbook(target: File) {
    val wb = XSSFWorkbook()
    fillWorkbook(wb)
    FileOutputStream(target).use { f ->
        wb.write(f)
    }
}

fun main(args: Array<String>) {
    makeWorkbook(File("test.xlsx"))
}
