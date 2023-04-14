package amf.core.internal.utils

import scala.annotation.tailrec

/** Topological sort
  */
object TSort {

  def tsort[A](edges: Iterable[(A, A)]): Either[Iterable[A], Iterable[A]] = {

    val preds = edges.foldLeft(Map[A, Set[A]]()) { case (acc, (from, to)) =>
      acc + (from -> acc.getOrElse(from, Set())) + (to -> (acc.getOrElse(to, Set()) + from))
    }

    tsort(preds, Seq())
  }

  @tailrec def tsort[A](preds: Map[A, Set[A]], done: Iterable[A]): Either[Iterable[A], Iterable[A]] = {
    val (noPreds, hasPreds) = preds.partition { case (_, ps) => ps.isEmpty }
    if (noPreds.isEmpty) {
      if (hasPreds.isEmpty) { Right(done) }
      else {
        // Recursion found!
        Left(done ++ hasPreds.keys)
      }
    } else {
      val found = noPreds.keys
      tsort(hasPreds.view.mapValues { _ -- found }.toMap, done ++ found)
    }
  }
}
