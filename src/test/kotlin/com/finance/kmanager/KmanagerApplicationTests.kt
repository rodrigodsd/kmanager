package com.finance.kmanager

import org.apache.poi.sl.usermodel.ObjectMetaData.Application
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.modulith.core.ApplicationModule
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter
import java.util.function.Consumer


@SpringBootTest
class KmanagerApplicationTests {

    @Test
    fun `verifies modular structure`() {
        val modules = ApplicationModules.of(KmanagerApplication::class.java)
        modules.verify()
    }

    @Test
    fun `print module structure`() {
        val modules = ApplicationModules.of(KmanagerApplication::class.java)
        modules.forEach(Consumer { module: ApplicationModule? -> println(module) })
    }

    @Test
    fun `create module documentation`() {
        val modules = ApplicationModules.of(KmanagerApplication::class.java)
        Documenter(modules)
            .writeDocumentation()
            .writeIndividualModulesAsPlantUml()
    }

}
