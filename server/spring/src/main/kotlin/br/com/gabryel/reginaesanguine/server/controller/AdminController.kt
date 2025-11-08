package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.server.service.PackSeederService
import br.com.gabryel.reginaesanguine.server.service.SeedResult
import kotlinx.coroutines.runBlocking
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("admin", produces = [APPLICATION_JSON_VALUE])
class AdminController(private val packSeederService: PackSeederService) {
    @PostMapping("/seed-packs")
    fun seedPacks(): SeedResult = runBlocking {
        packSeederService.seedPacks()
    }
}
