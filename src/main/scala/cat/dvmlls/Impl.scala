package cat.dvmlls

import java.util
import com.carrotsearch.hppc.{IntDoubleMap, IntDoubleScatterMap, IntDoubleHashMap}
import gnu.trove.map.hash.{TIntDoubleHashMap, THashMap}

import scala.collection._

trait Impl[T, K] {
  def map:T
  def put(n:K, d:Double):K
  def update(n:K, d:Double):K
  def remove(n:K):K
  def get(n:K):Double
  def merge(m:T):Int
  def size:Int
}

// http://fastutil.di.unimi.it/
// https://github.com/OpenHFT/Koloboke

trait HPPC[T <: IntDoubleMap ] extends Impl[T,Int] {
  override def update(n: Int, d: Double):Int = { map.put(n,d) ; n }
  override def get(n: Int): Double = map.get(n)
  override def put(n: Int, d: Double):Int = update(n,d)
  override def merge(m:T): Int = { map.putAll(m); map.size() }
  override def size: Int = map.size()
  override def remove(n:Int):Int = { map.remove(n) ; n }
}

class HPPC_IDHM(capacity:Int) extends HPPC [IntDoubleHashMap] { override def map = new IntDoubleHashMap(capacity) }
class HPPC_IDSM(capacity:Int) extends HPPC [IntDoubleScatterMap] { override def map = new IntDoubleScatterMap(capacity) }

class TR_HM[K](capacity:Int) extends Impl[THashMap[K,Double],K] {
  override def map = new THashMap[K,Double](capacity)
  override def update(n: K, d: Double): K = { map.put(n,d) ; n }
  override def get(n: K): Double = map.get(n)
  override def put(n: K, d: Double): K = update(n,d)
  override def merge(m:THashMap[K,Double]): Int = { map.putAll(m); map.size() }
  override def size: Int = map.size()
  override def remove(n: K): K = { map.remove(n) ; n }
}

class TR_IDHM(capacity:Int) extends Impl[TIntDoubleHashMap,Int] {
  override def map = new TIntDoubleHashMap(capacity)
  override def update(n: Int, d: Double):Int = { map.put(n,d) ; n }
  override def get(n: Int): Double = map.get(n)
  override def put(n: Int, d: Double):Int = update(n,d)
  override def merge(m:TIntDoubleHashMap): Int = { map.putAll(m); map.size() }
  override def size: Int = map.size()
  override def remove(n:Int):Int = { map.remove(n) ; n }
}

trait JU[T <: util.Map[Int,Double]] extends Impl[T, Int] {
  override def put(n: Int, d:Double): Int = { map.put(n,d) ; n }
  override def update(n: Int, d:Double): Int = put(n, d)
  override def remove(n: Int): Int = { map.remove(n) ; n }
  override def get(n:Int):Double = map.get(n)
  override def merge(m:T):Int = { map.putAll(m); map.size }
  override def size:Int = map.size()
}

class JU_HM (capacity:Int) extends JU [util.HashMap[Int,Double]] { override val map = new util.HashMap[Int,Double](capacity) }
class JU_TM () extends JU [util.TreeMap[Int,Double]] { override val map = new util.TreeMap[Int,Double]() }
class JU_C_HM (capacity:Int) extends JU [util.concurrent.ConcurrentHashMap[Int,Double]] { override val map = new util.concurrent.ConcurrentHashMap[Int,Double](capacity) }

trait SC_M[T <: mutable.Map[K,Double], K] extends Impl[T, K] {
  override def put(n:K, d:Double):K = { map.put(n,d) ; n }
  override def update(n:K, d:Double):K = { map.update(n,d) ; n }
  override def remove(n:K):K = { map.remove(n) ; n }
  override def get(n:K):Double = map.get(n).get
  override def merge(m:T):Int = { map ++= m; map.size }
  override def size:Int = map.size
}

class SC_M_HM () extends SC_M [mutable.HashMap[Int,Double], Int] { override val map = new mutable.HashMap[Int,Double]() }
class SC_M_OHM (capacity:Int) extends SC_M [mutable.OpenHashMap[Int,Double], Int] { override val map = new mutable.OpenHashMap[Int,Double](capacity) }
class SC_M_LHM () extends SC_M [mutable.LinkedHashMap[Int,Double], Int] { override val map = new mutable.LinkedHashMap[Int,Double]() }
class SC_M_LsM () extends SC_M [mutable.ListMap[Int,Double], Int] { override val map = new mutable.ListMap[Int,Double]() }
class SC_M_LoM (capacity:Int) extends SC_M [mutable.LongMap[Double], Long] { override val map = new mutable.LongMap[Double](capacity) }

trait SC_I[T <: immutable.Map[K,Double], K] extends Impl[T, K] {
  override def get(n:K):Double = map.get(n).get
  override def size:Int = map.size
}

class SC_I_TM () extends SC_I [immutable.TreeMap[Int,Double], Int] {
  var map = immutable.TreeMap[Int,Double]()
  override def put(n:Int, d:Double):Int = { map += n -> d ; n }
  override def remove(n:Int):Int = { map -= n ; n }
  override def merge(m:immutable.TreeMap[Int,Double]):Int = { map ++= m; map.size }
  override def update(n:Int, d:Double):Int = { map = map.updated(n, d); n}
}

class SC_I_HM () extends SC_I [immutable.HashMap[Int,Double], Int] {
  var map = immutable.HashMap[Int,Double]()
  override def put(n:Int, d:Double):Int = { map += n -> d ; n }
  override def remove(n:Int):Int = { map -= n ; n }
  override def merge(m:immutable.HashMap[Int,Double]):Int = { map ++= m; map.size }
  override def update(n:Int, d:Double):Int = { map = map.updated(n, d); n}
}

class SC_I_IM () extends SC_I [immutable.IntMap[Double], Int] {
  var map = immutable.IntMap[Double]()
  override def put(n:Int, d:Double):Int = { map += n -> d ; n }
  override def remove(n:Int):Int = { map -= n ; n }
  override def merge(m:immutable.IntMap[Double]):Int = { map ++= m; map.size }
  override def update(n:Int, d:Double):Int = { map = map.updated(n, d); n}
}

class SC_I_LoM () extends SC_I [immutable.LongMap[Double], Long] {
  var map = immutable.LongMap[Double]()
  override def put(n: Long, d:Double):Long = { map += n -> d ; n }
  override def remove(n:Long):Long = { map -= n ; n }
  override def merge(m:immutable.LongMap[Double]):Int = { map ++= m; map.size }
  override def update(n:Long, d:Double):Long = { map = map.updated(n, d); n}
}
