package io.timvanoijen.github.krational

import java.math.BigInteger
import java.math.BigInteger.ZERO
import kotlin.div
import kotlin.times

@ConsistentCopyVisibility
data class Rational private constructor(
    val p: BigInteger,
    val q: BigInteger,
) {
    fun signum() = if (p.signum() == 0) 0 else if (p.signum() == q.signum()) 1 else -1

    fun inverse() = Rational(q, p)

    fun toDouble() = p.toDouble() / q.toDouble()

    override fun toString() = "$p/$q"

    companion object {
        fun of(numerator: BigInteger, denominator: BigInteger): Rational {
            require(denominator != ZERO) { "Denominator must not be zero" }

            if (numerator == ZERO) return Rational(ZERO, denominator)
            val g = gcf(numerator.abs(), denominator.abs())
            val signBI = if (numerator.signum() != denominator.signum()) (-1).toBigInt() else 1.toBigInt()
            return Rational((numerator.abs() / g) * signBI, denominator.abs() / g)
        }
    }
}


// Infix operator "over" for creating Rationals
infix fun Int.over(denominator: Int) = Rational.of(this.toBigInt(), denominator.toBigInt())
infix fun Int.over(denominator: Long) = Rational.of(this.toBigInt(), denominator.toBigInt())
infix fun Int.over(denominator: BigInteger) = Rational.of(this.toBigInt(), denominator)
infix fun Long.over(denominator: Int) = Rational.of(this.toBigInt(), denominator.toBigInt())
infix fun Long.over(denominator: Long) = Rational.of(this.toBigInt(), denominator.toBigInt())
infix fun Long.over(denominator: BigInteger) = Rational.of(this.toBigInt(), denominator)
infix fun BigInteger.over(denominator: Int) = Rational.of(this, denominator.toBigInt())
infix fun BigInteger.over(denominator: Long) = Rational.of(this, denominator.toBigInt())
infix fun BigInteger.over(denominator: BigInteger) = Rational.of(this, denominator)

operator fun Rational.unaryMinus() = Rational.of(-p, q)

// Operators: Rational -> Rational
operator fun Rational.plus(other: Rational) = Rational.of(p * other.q + other.p * q, q * other.q)
operator fun Rational.minus(other: Rational) = Rational.of(p * other.q - other.p * q, q * other.q)
operator fun Rational.times(other: Rational) = Rational.of(p * other.p, q * other.q)
operator fun Rational.div(other: Rational) = Rational.of(p * other.q, q * other.p)

// Operators: Rational -> Int
operator fun Rational.plus(other: Int) = Rational.of(p + other.toBigInt() * q, q)
operator fun Rational.minus(other: Int) = Rational.of(p - other.toBigInt() * q, q)
operator fun Rational.times(other: Int) = Rational.of(p * other.toBigInt(), q)
operator fun Rational.div(other: Int) = Rational.of(p, q * other.toBigInt())

// Operators: Int -> Rational
operator fun Int.plus(other: Rational) = other + this
operator fun Int.minus(other: Rational) = -other + this
operator fun Int.times(other: Rational) = (other * this)
operator fun Int.div(other: Rational) = Rational.of(toBigInt() * other.q, other.p)

// Operators: Rational -> Long
operator fun Rational.plus(other: Long) = Rational.of(p + other.toBigInt() * q, q)
operator fun Rational.minus(other: Long) = Rational.of(p - other.toBigInt() * q, q)
operator fun Rational.times(other: Long) = Rational.of(p * other.toBigInt(), q)
operator fun Rational.div(other: Long) = Rational.of(p, q * other.toBigInt())

// Operators: Long -> Rational
operator fun Long.plus(other: Rational) = other + this
operator fun Long.minus(other: Rational) = -other + this
operator fun Long.times(other: Rational) = (other * this)
operator fun Long.div(other: Rational) = Rational.of(toBigInt() * other.q, other.p)

// Operators: Rational -> BigInteger
operator fun Rational.plus(other: BigInteger) = Rational.of(p + other * q, q)
operator fun Rational.minus(other: BigInteger) = Rational.of(p - other * q, q)
operator fun Rational.times(other: BigInteger) = Rational.of(p * other, q)
operator fun Rational.div(other: BigInteger) = Rational.of(p, q * other)

// Operators: BigInteger -> Rational
operator fun BigInteger.plus(other: Rational) = other + this
operator fun BigInteger.minus(other: Rational) = -other + this
operator fun BigInteger.times(other: Rational) = (other * this)
operator fun BigInteger.div(other: Rational) = Rational.of(this * other.q, other.p)

private fun Int.toBigInt() = BigInteger.valueOf(this.toLong())
private fun Long.toBigInt() = BigInteger.valueOf(this)

private tailrec fun gcf(a: BigInteger, b: BigInteger): BigInteger {
    if (b == ZERO) return a
    return gcf(b, a % b)
}