package io.timvanoijen.github.krational

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigInteger

class RationalTest {
    private fun bi(s: String) = BigInteger(s)
    private fun r(p: String, q: String) = Rational.of(bi(p), bi(q))
    private fun assertRQ(actual: Rational, p: String, q: String) {
        assertEquals(bi(p), actual.p, "Unexpected numerator")
        assertEquals(bi(q), actual.q, "Unexpected denominator")
    }

    @Test
    fun signum_basic() {
        assertEquals(1, r("1", "2").signum())
        assertEquals(-1, r("-1", "2").signum())
        assertEquals(-1, r("1", "-2").signum())
        assertEquals(1, r("-1", "-2").signum())
        assertEquals(0, r("0", "5").signum())
        assertEquals(0, r("0", "-5").signum())
    }

    @Test
    fun inverse_swapsNumeratorAndDenominator() {
        assertRQ(r("3", "7").inverse(), "7", "3")
        assertRQ(r("0", "5").inverse(), "5", "0")
    }

    @Test
    fun toDouble_and_toString() {
        assertEquals(0.5, r("1", "2").toDouble(), 1e-12)
        assertEquals(-0.75, r("-3", "4").toDouble(), 1e-12)
        assertEquals("1/2", r("1", "2").toString())
        assertEquals("-3/4", r("-3", "4").toString())
    }

    @Test
    fun unaryMinus_flipsSignOfNumerator() {
        val a = r("2", "5")
        val b = -a
        assertRQ(b, "-2", "5")
    }

    @Test
    fun rational_rational_operations_simplifyAndNormalize() {
        // plus
        val sum = r("1", "2") + r("1", "3")
        assertRQ(sum, "5", "6")

        // minus
        val diff = r("3", "4") - r("1", "6")
        assertRQ(diff, "7", "12")

        // times
        val prod = r("2", "3") * r("9", "4")
        assertRQ(prod, "3", "2")

        // division
        val quot = r("2", "3") / r("-4", "5")
        assertRQ(quot, "-5", "6")
    }

    @Test
    fun int_interactions_rightHandSide() {
        val base = r("2", "4")
        assertRQ(base + 1, "3", "2") // 2 + 1*4 = 6 over 4 (unsimplified)
        assertRQ(base - 1, "-1", "2")
        assertRQ(base * 2, "1", "1") // (2*2)/4 -> 4/4 -> 1/1
        assertRQ(base / 2, "1", "4") // 2/(4*2) -> 2/8 -> 1/4
    }

    @Test
    fun int_interactions_leftHandSide() {
        val base = r("2", "4")
        assertRQ(1 + base, "3", "2") // unsimplified
        assertRQ(1 - base, "1", "2") // -2/4 + 1 -> (−2) + 1*4 = 2 over 4
        assertRQ(2 * base, "1", "1")
        assertRQ(2 / base, "4", "1") // (2*4)/2 -> 8/2 -> 4/1
    }

    @Test
    fun long_interactions() {
        val base = r("1", "2")
        assertRQ(base + 3L, "7", "2")
        assertRQ(3L + base, "7", "2")
        assertRQ(base - 3L, "-5", "2")
        assertRQ(3L - base, "5", "2")
        assertRQ(base * 3L, "3", "2") // (2*3)/4 -> 6/4 -> 3/2
        assertRQ(3L * base, "3", "2")
        assertRQ(base / 3L, "1", "6") // 2/(4*3) -> 2/12 -> 1/6
        assertRQ(3L / base, "6", "1") // (3*4)/2 -> 12/2 -> 6/1
    }

    @Test
    fun bigInteger_interactions() {
        val base = r("2", "4")
        val five = bi("5")
        assertRQ(base + five, "11", "2")
        assertRQ(five + base, "11", "2")
        assertRQ(base - five, "-9", "2")
        assertRQ(five - base, "9", "2")
        assertRQ(base * five, "5", "2") // (2*5)/4 -> 10/4 -> 5/2
        assertRQ(five * base, "5", "2")
        assertRQ(base / five, "1", "10") // 2/(4*5) -> 2/20 -> 1/10
        assertRQ(five / base, "10", "1") // (5*4)/2 -> 20/2 -> 10/1
    }

    @Test
    fun simplification_and_sign_normalization() {
        // simplify reduces gcd and results in positive denominator for non-zero values
        val normalized = r("-2", "-4") * r("1", "1") // triggers simplify
        assertRQ(normalized, "1", "2")

        val normalized2 = r("2", "-4") * r("1", "1")
        assertRQ(normalized2, "-1", "2")

        // zero numerator remains as-is (including negative denominator) after simplify-triggering ops
        val zero = r("0", "-5") * r("1", "1")
        assertRQ(zero, "0", "-5")

        // gcd reduction on typical fraction via operation that simplifies
        val reduced = r("8", "12") * r("1", "1")
        assertRQ(reduced, "2", "3")
    }
    
    @Test
    fun infix_over_constructor() {
        // Int over Int
        val a = 1 over 2
        assertRQ(a, "1", "2")

        // Reduction and sign normalization for non-zero
        val b = 2 over 4
        assertRQ(b, "1", "2")
        val c = -2 over 4
        assertRQ(c, "-1", "2")
        val d = 2 over -4
        assertRQ(d, "-1", "2")
        val e = -2 over -4
        assertRQ(e, "1", "2")

        // Zero numerator preserves denominator sign (no simplify path)
        val z1 = 0 over 5
        assertRQ(z1, "0", "5")
        val z2 = 0 over -5
        assertRQ(z2, "0", "-5")

        // Cross type overloads should behave same as Rational.of
        val f = 1 over 2L
        assertRQ(f, "1", "2")
        val g = 1L over 2
        assertRQ(g, "1", "2")
        val h = 6L over 8L
        assertRQ(h, "3", "4")

        val biFive = bi("5")
        val i = biFive over 10
        assertRQ(i, "1", "2")
        val j = 5 over bi("10")
        assertRQ(j, "1", "2")
        val k = bi("-6") over bi("-9")
        assertRQ(k, "2", "3")
    }
}