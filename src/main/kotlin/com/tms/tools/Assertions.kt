package com.tms.tools

import io.testignite.steps.allureStep


fun String?.shouldBe(expected: String, subject: String = "value"): Unit =
    allureStep("Check $subject is '$expected'") {
        check(this@shouldBe == expected) { "Expected $subject '$expected' but was '${this@shouldBe}'" }
    }
