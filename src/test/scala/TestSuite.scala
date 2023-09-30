// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
import pp202302.assign1.Assignment1.*

class TestSuite extends munit.FunSuite {
  test("problem 1") {
    def mul3(n: Long) = n % 3 == 0
    def isSq(n: Long) = Math.sqrt(n).toLong * Math.sqrt(n).toLong == n

    assertEquals(sumProp(mul3, 1), 0L)
    assertEquals(sumProp(mul3, 10), 18L)
    assertEquals(sumProp(mul3, 18), 63L)
    assertEquals(sumProp(isSq, 1), 1L)
    assertEquals(sumProp(isSq, 10), 25L)
    assertEquals(sumProp(isSq, 16), 64L)
    assertEquals(sumProp(isSq, 100000), 2500000000L)
  }

  test("problem 2-1") {
    assert(isDivisible(1, 2, 1, 0))
    assert(isDivisible(1, 2, 1, 2))
    assert(isDivisible(2, 2, 3, 3) == false)
    assert(isDivisible(3, 1, 1, 1))
    assert(isDivisible(3, 1, 2, -1))
    assert(isDivisible(5, 2, 2, 2) == false)
    assert(isDivisible(0, 0, 100, 100))
  }

  test("problem 2-2") {
    assertEquals(nthPrime(1, 2), (1L, 1L))
    assertEquals(nthPrime(2, 5), (1L, 1L))
    assertEquals(nthPrime(10, 6), (3L, 2L))
    assertEquals(nthPrime(20, 20), (3L, 0L))
    assertEquals(nthPrime(1000, 2000), (5L, 294L))
    assertEquals(nthPrime(100000, 10000), (127L, 108L))
    // assertEquals(nthPrime(1000000, 100000), (163L, 46290L))
  }
  
  test("problem 3") {
    assertEquals(baf(_ + _, 5, 6), 45L)
    assertEquals(baf(_ + _, 8, 2), 27L)
    assertEquals(baf(_ - _, 10, 10), -6L)
    assertEquals(baf(_ - _, 7, 8), -1L)
    assertEquals(baf(3 * _ + _, 12, 7), 5651809L)
    assertEquals(baf(_ + 3 * _, 20, 20), 47567331140L)
    assertEquals(baf(_ - _, 1000000, 1000000), -1237506L)
  }
}
