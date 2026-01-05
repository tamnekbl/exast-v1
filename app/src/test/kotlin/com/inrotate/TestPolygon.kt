package com.inrotate

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Instant


class TestPolygon {

    @Test
    fun testRoot() = runTest {
        val time = Instant.now().toString()
        println(time)
    }
}