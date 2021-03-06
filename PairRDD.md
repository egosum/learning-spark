Spark provides special operations on RDDs containing key/value pairs, called as Pair RDDs.

Pair RDDs are a useful building block in many programs, as
they expose operations that allow you to act on each key in parallel or regroup data
across the network.

**Creating PairRDD**
We can do this by running a map() function that returns key/value pairs.
```
val lines = sc.textFile("README.md")
val pairs = lines.map(x => (x.split(" ")(0), x))
```
**Transformations on Pair RDDs**
```
scala> val rdd = sc.parallelize(List((1, 2), (3, 4), (3, 6)))
rdd: org.apache.spark.rdd.RDD[(Int, Int)] = ParallelCollectionRDD[15] at parallelize at <console>:24
scala> rdd.collect
res20: Array[(Int, Int)] = Array((1,2), (3,4), (3,6))
```
**reduceByKey(function)**: Combine values with the same key.
```
scala> rdd.reduceByKey(_ + _).collect
res21: Array[(Int, Int)] = Array((1,2), (3,10))
```
**groupByKey**: Group values with the same key.
```
scala> rdd.groupByKey.collect
res22: Array[(Int, Iterable[Int])] = Array((1,CompactBuffer(2)), (3,CompactBuffer(4, 6)))
```
**mapValues(function)**: Apply a function to each value of a pair RDD without changing the key.
```
scala> rdd.mapValues(value => value * 2).collect
res24: Array[(Int, Int)] = Array((1,4), (3,8), (3,12))
```
**flatMapValues(function)**: Apply a function that returns an iterator to each value of a pair RDD, and for each element returned, produce a key/value entry with the oldkey. Often used for tokenization.
```
scala> rdd.flatMapValues(value => (value to 5)).collect
res30: Array[(Int, Int)] = Array((1,2), (1,3), (1,4), (1,5), (3,4), (3,5))
```
What is happening here? We have an RDD , val rdd = sc.parallelize(List((1, 2), (3, 4), (3, 6)))
Now flatMapValues method is a combination of flatMap and mapValues. What mapValues does is that it maps the values while keeping the keys. 
example,
```
scala> rdd.mapValues(x  => x to 5).collect
res33: Array[(Int, scala.collection.immutable.Range.Inclusive)] = Array((1,Range(2, 3, 4, 5)), (3,Range(4, 5)), (3,Range()))
```
Notice here that for the key-value pair (3, 6), it produces (3,Range()) since 6 to 5 does not produce a non-empty collection of values.
what flatMap does is it "breaks down" collections into the elements of the collection.
if we do rdd.mapValues(x  => x to 5).flatMap(x => x)
We will get Array((1,2), (1,3), (1,4), (1,5), (3,4), (3,5))


**keys()** : Return an RDD of just the keys.
```
scala> rdd.keys.collect
res34: Array[Int] = Array(1, 3, 3)
```
**values**: Return an RDD of just the values.
```
scala> rdd.values.collect
res35: Array[Int] = Array(2, 4, 6)
```

**sortByKey()**: Return an RDD sorted by the key.
```
scala> rdd.sortByKey().collect
res38: Array[(Int, Int)] = Array((1,2), (3,4), (3,6))
```

**Transformations on two pair RDDs**
```
val rdd = sc.parallelize(List((1, 2), (3, 4), (3, 6)))
val other = sc.parallelize(List((3, 9)))
```
**subtractByKey**: Remove elements with a key present in the other RDD.
```
scala> rdd.subtractByKey(other).collect
res40: Array[(Int, Int)] = Array((1,2))
```
**join**: Perform an inner join between two RDDs.
```
scala> rdd.join(other).collect
res42: Array[(Int, (Int, Int))] = Array((3,(4,9)), (3,(6,9)))
```
**rightOuterJoin**: Perform a join between two RDDs where the key must be present in the first RDD.
```
scala> rdd.rightOuterJoin(other).collect
res43: Array[(Int, (Option[Int], Int))] = Array((3,(Some(4),9)), (3,(Some(6),9)))
```
**leftOuterJoin**: Perform a join between two rdd. RDDs where the key must be present in the other RDD.
```
scala> rdd.leftOuterJoin(other).collect
res45: Array[(Int, (Int, Option[Int]))] = Array((1,(2,None)), (3,(4,Some(9))), (3,(6,Some(9))))
```
**cogroup**: Group data from both RDDs sharing the same key.
```
scala> rdd.cogroup(other).collect
res47: Array[(Int, (Iterable[Int], Iterable[Int]))] = Array((1,(CompactBuffer(2),CompactBuffer())), (3,(CompactBuffer(4, 6),CompactBuffer(9))))
```
