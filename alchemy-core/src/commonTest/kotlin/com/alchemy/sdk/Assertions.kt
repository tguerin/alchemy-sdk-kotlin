package com.alchemy.sdk

import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

infix fun <T> T.shouldBeEqualTo(expected: T) = assertEquals(expected, this)
infix fun IntArray.shouldBeEqualTo(expected: IntArray) = forEachIndexed { index, t ->
    assertEquals(expected[index], t)
}

infix fun <T> T.shouldNotBeEqualTo(expected: T) = assertNotEquals(expected, this)
infix fun <T> T.shouldNotBe(expected: T) = assertNotSame(expected, this)
infix fun <T>  Comparable<T>.shouldBeGreaterThan(expected: T) = assertTrue(this > expected)
infix fun <T>  Comparable<T>.shouldBeGreaterOrEqualTo(expected: T) = assertTrue(this >= expected)
infix fun <T> List<T>.shouldHaveSize(expected: Int) = assertEquals(this.size, expected)
infix fun <T> List<T>.shouldContainAll(expected: List<T>) = expected.forEach { assertContains(this, it) }
