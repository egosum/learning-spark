package rdd

import org.apache.spark.{SparkConf, SparkContext}

object Factorial extends App {
 val num:BigInt = 200000
  println("Boosting Factorial Calculation")

  def time[R](block: => R): R = {
    val t0 = System.currentTimeMillis()
    val result = block    // call-by-name
    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) + " millisecs")
    result
  }

  def factorial(num: BigInt): BigInt = {
    def factImp(num: BigInt, fact: BigInt): BigInt = {
      if (num == 0) fact
      else
      factImp(num - 1, num * fact)
    }
    factImp(num, 1)
  }

  var result = time(factorial(num))
  //println(s"Factorial Without Using Spark $result")
  
  def factorialPar(num: BigInt): BigInt = {
    if (num == 0) BigInt(1)
    else {
	  val list = (BigInt(1) to num).toList
	  list.par.reduce(_ * _)
    }
  }

  var resultPar = time(factorialPar(num))
  //println(s"Factorial Using Par $resultPar")

  val conf = new SparkConf().setMaster("local[*]").setAppName("Factorial")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")
  def factorialUsingSpark(num: BigInt): BigInt = {
    if (num == 0) BigInt(1)
    else {
      val list = (BigInt(1) to num).toList
      sc.parallelize(list).reduce(_ * _)
    }
  }

  var resultUsingSpark = time(factorialUsingSpark(num))
  //println(s"Factorial Using Spark $resultUsingSpark")
  println("Are \"Scala vanilla\" and Spark equal? " + (result == resultUsingSpark))
  
  // is the Par version computing the right result?
  println("Are Par and Spark equal? " + (resultPar == resultUsingSpark))

}
