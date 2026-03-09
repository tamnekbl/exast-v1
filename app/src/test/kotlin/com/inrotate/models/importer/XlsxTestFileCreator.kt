package com.inrotate.models.importer

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

object XlsxTestFileCreator {
    fun createTestFile(filePath: String) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Events")

        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("id")
        header.createCell(1).setCellValue("title")
        header.createCell(2).setCellValue("date")
        header.createCell(3).setCellValue("level")
        header.createCell(4).setCellValue("types")
        header.createCell(5).setCellValue("location")
        header.createCell(6).setCellValue("format")
        header.createCell(7).setCellValue("organizations")
        header.createCell(8).setCellValue("participantsOther")
        header.createCell(9).setCellValue("participantsSpo")
        header.createCell(10).setCellValue("participantsVo")
        header.createCell(11).setCellValue("participantsForeign")
        header.createCell(12).setCellValue("participantsTotal")
        header.createCell(13).setCellValue("organizationRole")
        header.createCell(14).setCellValue("description")

        val dataRow = sheet.createRow(1)
        dataRow.createCell(0).setCellValue(1.0)
        dataRow.createCell(1).setCellValue("Test Event")
        dataRow.createCell(2).setCellValue("2024-01-01")
        dataRow.createCell(3).setCellValue("University")
        dataRow.createCell(4).setCellValue("Conference")
        dataRow.createCell(5).setCellValue("Online")
        dataRow.createCell(6).setCellValue("Remote")
        dataRow.createCell(7).setCellValue("иасид")
        dataRow.createCell(8).setCellValue(10.0)
        dataRow.createCell(9).setCellValue(20.0)
        dataRow.createCell(10).setCellValue(30.0)
        dataRow.createCell(11).setCellValue(5.0)
        dataRow.createCell(12).setCellValue(65.0)
        dataRow.createCell(13).setCellValue("Organizer")
        dataRow.createCell(14).setCellValue("Test Description")

        FileOutputStream(filePath).use { outputStream ->
            workbook.write(outputStream)
        }
        workbook.close()
    }
}