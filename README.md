### krational — Simple Kotlin Rational Numbers

`krational` is a tiny Kotlin library that provides an immutable `Rational` type backed by `java.math.BigInteger`. It focuses on:

- Exact arithmetic with arbitrary precision (no rounding errors)
- Idiomatic Kotlin operators (`+`, `-`, `*`, `/`, unary `-`)
- Convenient construction helpers, including an infix `over` (e.g., `1 over 2`)
- Interop with `Int`, `Long`, and `BigInteger` on both sides of operations

This library is suitable for applications that require exact fractions (math, finance rules, puzzles, parsers, etc.).

#### Coordinates

- Group: `io.timvanoijen.github`
- Artifact: `krational`
- Version: `1.0-SNAPSHOT`

Adjust the version as needed.

#### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("nl.timvanoijen.github:krational:1.0-SNAPSHOT")
}
```

#### Maven

```xml
<dependency>
  <groupId>nl.timvanoijen.github</groupId>
  <artifactId>krational</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

### Quick start

```kotlin
// Construct with the infix `over`
val a = 1 over 2          // 1/2
val b = 3 over 4          // 3/4

// Arithmetic (results are reduced and sign-normalized)
val sum = a + b           // 5/4
val diff = b - a          // 1/4
val prod = a * b          // 3/8
val quot = a / b          // 2/3

// Numeric interop on both sides
val x1 = a + 2            // 5/2
val x2 = 2 - a            // 3/2
val x3 = 3L / a           // 6/1

// Utilities
val neg = -a              // -1/2
val inv = a.inverse()     // 2/1
val d  = a.toDouble()     // 0.5
val s  = a.signum()       // 1 (positive)

println(sum)              // prints "5/4"
```

### Constructing rationals

- Via infix `over` (most convenient):

```kotlin
val r1 = 1 over 2           // Int over Int
val r2 = 1 over 2L          // Int over Long
val r3 = 1L over 2          // Long over Int
val r4 = 6L over 8L         // Long over Long -> reduced to 3/4
val r5 = BigInteger("5") over 10  // BigInteger over Int -> 1/2
```

- Via factory `Rational.of(p, q)` when you already have `BigInteger`s:

```kotlin
val p = java.math.BigInteger("42")
val q = java.math.BigInteger("56")
val r = Rational.of(p, q)   // reduced to 3/4
```

Denominator must be non-zero when constructing via `Rational.of` or `over`.

### Arithmetic and interop

All four operators are provided between:

- `Rational` and `Rational`
- `Rational` and `Int`/`Long`/`BigInteger`
- `Int`/`Long`/`BigInteger` and `Rational` (the numeric value on the left-hand side)

Examples:

```kotlin
val r = 2 over 4            // 1/2 after reduction

// With Int
val a = r + 1               // (1/2) + 1 = 3/2
val b = 2 * r               // 1/1
val c = r / 2               // 1/4
val d = 2 / r               // 4/1

// With Long
val e = r + 3L              // 7/2
val f = 3L - r              // 5/2
val g = r * 3L              // 3/2
val h = 3L / r              // 6/1

// With BigInteger
val bi = java.math.BigInteger("5")
val i = r + bi              // 11/2
val j = bi - r              // 9/2
val k = r * bi              // 5/2
val l = bi / r              // 10/1
```

### Normalization and reduction rules

- All results created through `Rational.of` and the arithmetic operators are reduced by the GCD of numerator and denominator.
- For non-zero rationals, the denominator is normalized to be positive; the sign is carried by the numerator.
- If the numerator is zero during construction (`Rational.of(0, q)` or `0 over q`), the resulting value keeps the given denominator as-is. For example, `0 over -5` yields `0/-5`. This matches the current implementation and is tested. Arithmetic that triggers a reduction may keep that sign as-is for zero numerators.
- `inverse()` simply swaps numerator and denominator. If you call `inverse()` on a zero rational (e.g., `0/5`), you will get a denominator of zero (`5/0`). Avoid using such values in division; constructing new rationals with a zero denominator via `of` is not allowed.

### API surface

- `data class Rational(val p: BigInteger, val q: BigInteger)`
- Construction
  - `Rational.of(numerator: BigInteger, denominator: BigInteger): Rational`
  - Infix constructors: `Int.over(Int|Long|BigInteger)`, `Long.over(Int|Long|BigInteger)`, `BigInteger.over(Int|Long|BigInteger)`
- Utilities
  - `fun Rational.signum(): Int` — returns `-1`, `0`, or `1`
  - `fun Rational.inverse(): Rational`
  - `fun Rational.toDouble(): Double`
  - `override fun Rational.toString(): String` — prints `"p/q"`
- Operators (selected):
  - `+`, `-`, `*`, `/` between `Rational` and `Rational`
  - Interop: the same operators between `Rational` and `Int`/`Long`/`BigInteger` on either side
  - Unary minus: `-r`

### Notes

- All arithmetic is exact; it grows the numerator/denominator as needed. Use `toDouble()` only when you explicitly want a floating-point approximation.
- Because `Rational` uses `BigInteger`, operations are unbounded but may allocate; consider performance implications in tight loops.

### Development

The project uses Kotlin/JVM with JUnit 5 for tests. To run tests:

```bash
./gradlew test
```
