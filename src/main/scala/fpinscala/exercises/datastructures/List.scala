package fpinscala.exercises.datastructures

/** `List` data type, parameterized on a type, `A`. */
enum List[+A]:
  /** A `List` data constructor representing the empty list. */
  case Nil
  /** Another data constructor, representing nonempty lists. Note that `tail` is another `List[A]`,
    which may be `Nil` or another `Cons`.
   */
  case Cons(head: A, tail: List[A])

object List: // `List` companion object. Contains functions for creating and working with lists.
  def sum(ints: List[Int]): Int = ints match // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x,xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.

  def product(doubles: List[Double]): Double = doubles match
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)

  def apply[A](as: A*): List[A] = // Variadic function syntax
    if as.isEmpty then Nil
    else Cons(as.head, apply(as.tail*))

  @annotation.nowarn // Scala gives a hint here via a warning, so let's disable that
  val result = List(1,2,3,4,5) match
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + sum(t)
    case _ => 101

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))

  def foldRight[A,B](as: List[A], acc: B, f: (A, B) => B): B = // Utility functions
    as match
      case Nil => acc
      case Cons(x, xs) => f(x, foldRight(xs, acc, f))

  def sumViaFoldRight(ns: List[Int]): Int =
    foldRight(ns, 0, (x,y) => x + y)

  def productViaFoldRight(ns: List[Double]): Double =
    foldRight(ns, 1.0, _ * _) // `_ * _` is more concise notation for `(x,y) => x * y`; see sidebar

  def tail[A](l: List[A]): List[A] = 
    l match
      case Nil => Nil
      case Cons(_, t) => t
    

  def setHead[A](l: List[A], h: A): List[A] = 
    l match
      case Nil => Cons(h, Nil)
      case Cons(_, t) => Cons(h, t)
  

  def drop[A](l: List[A], n: Int): List[A] = 
    @annotation.tailrec
    def go[A](n: Int, l: List[A]): List[A] =
      if n <= 0 then l
      else l match
        case Nil => List()
        case Cons(_, t) => go(n-1, t)
    
    go(n, l)
      


  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = 
    @annotation.tailrec
    def go[A](l: List[A], f: A => Boolean): List[A] =
      l match
        case Cons(h, t) if f(h) => go (t, f) 
        case _ => l
    
    go(l, f)
      

  def init[A](l: List[A]): List[A] = 
    l match
      case Nil => Nil
      case Cons(h, Nil) => Nil
      case Cons(h, t) => Cons(h, init(t))

    
  def length[A](l: List[A]): Int = 
    foldRight(l, 0, (_, acc) => acc + 1)

  @annotation.tailrec
  def foldLeft[A,B](l: List[A], acc: B, f: (B, A) => B): B =
    l match
      case Nil => acc
      case Cons(h, t) => foldLeft(t, f(acc, h), f)


  def sumViaFoldLeft(ns: List[Int]): Int = 
    foldLeft(ns, 0, _ + _)

  def productViaFoldLeft(ns: List[Double]): Double = 
    foldLeft(ns, 1.0, _ * _)

  def lengthViaFoldLeft[A](l: List[A]): Int = 
    foldLeft(l, 0, (acc, _) => acc + 1)

  def reverse[A](l: List[A]): List[A] = 
    foldLeft(l, Nil: List[A], (acc: List[A], a: A) => Cons(a, acc))

  def appendViaFoldRight[A](l: List[A], r: List[A]): List[A] = 
    foldRight(l, r, (h, t)=>Cons(h, t))

  def appendViaFoldLeft[A](l: List[A], r: List[A]): List[A] =
    foldLeft(reverse(l), r, (t, h) => Cons(h, t))
    
  def concat[A](l: List[List[A]]): List[A] = 
    foldRight(l, Nil: List[A], append)

  def incrementEach(l: List[Int]): List[Int] = 
    foldRight(l, Nil: List[Int], (h, acc)=>Cons(h+1, acc))

  def doubleToString(l: List[Double]): List[String] = 
    foldRight(l, Nil: List[String], (h, acc)=>Cons(h.toString(), acc))

  def map[A,B](l: List[A], f: A => B): List[B] =
    foldLeft(reverse(l), Nil:List[B], (acc, a) => Cons(f(a), acc))

  def filter[A](as: List[A], f: A => Boolean): List[A] = 
    @annotation.tailrec
    def go(l: List[A], g: A => Boolean): List[A] =
      l match
        case Nil => Nil
        case Cons(h, t) if g(h) => Cons(h, filter(t, g)) 
        case Cons(_, t) => go(t, g)

    go(as, f)
      
  def filterViaFoldLeft[A](as: List[A], f: A => Boolean): List[A] = 
    foldLeft(reverse(as), Nil: List[A], (t, h) => if f(h) then Cons(h, t) else t)

  def flatMap[A,B](as: List[A], f: A => List[B]): List[B] = 
    concat(map(as, f))

  def filterViaFlatMap[A](as: List[A], f: A => Boolean): List[A] = 
    flatMap(as, a => if f(a) then List(a) else Nil)

  def addPairwise(a: List[Int], b: List[Int]): List[Int] = 
    a match
      case Nil => Nil
      case Cons(ah, at) => b match
        case Nil => Nil
        case Cons(bh, bt) => Cons(ah+bh, addPairwise(at, bt))
  

  // def zipWith - TODO determine signature
  def zipWith[A, B, C](a: List[A], b: List[B], f: (A, B) => C): List[C] = 
    (a, b) match
      case (Nil, _) => Nil
      case (_, Nil) => Nil
      case (Cons(ah, at), Cons(bh, bt)) => Cons(f(ah, bh), zipWith(at, bt, f))
    

  @annotation.tailrec
  def hasSameStart[A](l: List[A], s: List[A]): Boolean = (l, s) match
    case (_, Nil) => true
    case (Cons(lh, lt), Cons(sh, st)) if lh == sh => hasSameStart(lt, st) 
    case _ => false

  @annotation.tailrec
  def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean = sup match
    case Nil => sub == Nil
    case _ if hasSameStart(sup, sub) => true
    case Cons(_, t) => hasSubsequence(t, sub)
  


    

    