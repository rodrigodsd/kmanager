package com.finance.kmanager.portfolio

import com.finance.kmanager.portfolio.domain.Portfolio
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/v1/portfolio")
class PortfolioController(val portfolioService: PortfolioService) {

    val logger: Logger = LoggerFactory.getLogger(PortfolioController::class.java)

    @GetMapping("{id}")
    fun getPortfolio(@PathVariable("id") id: Int) :Portfolio {
        return Portfolio(0,0,"","")
    }

    @PostMapping("/upload")
    fun importPortfolio(
        @RequestPart("file") file: MultipartFile,
        @RequestParam("name") portfolioName: String
    ): ResponseEntity<Any> {

        //val authentication: Authentication = SecurityContextHolder.getContext().getAuthentication()
        // val investorId: Long = (authentication.getPrincipal() as Jwt).getClaim("investorId")
        val investorId = 1

        if (file.isEmpty) {
            logger.error("Uploaded file is empty")
            return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
        }

        // Log file details
        val fileName = file.originalFilename
        val fileType = file.contentType
        val fileSize = file.size

        logger.info("Received file: Name={}, Type={}, Size={}", fileName, fileType, fileSize);

        // Validate file type by extension
//        checkNotNull(fileName)

        fileName?.let {
            if (it.lastIndexOf(".") != -1 && it.lastIndexOf(".") != 0) {
                val ext = it.substring(it.lastIndexOf(".") + 1)
                if (ext != "xlsx" && ext != "xls") {
                    logger.error("Unsupported file type: {}", ext)
                    return ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                }
            }
        }

        return ResponseEntity.ok(portfolioService.importPortfolio(file, portfolioName, investorId));
    }
}