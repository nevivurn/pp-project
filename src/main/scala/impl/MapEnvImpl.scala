package pp202302.project.impl

import pp202302.project.common.MapEnv
import pp202302.project.common._

import scala.annotation.tailrec

given mapEnvImpl[V]: EnvOps[MapEnv[V], V] with
  def emptyEnv() = MapEnv(Nil)

  extension (env: MapEnv[V])
    def pushEmptyFrame = MapEnv(Map.empty :: env.frames)

    def popFrame: MapEnv[V] = MapEnv(env.frames.tail)

    def setItem(name: String, item: V): MapEnv[V] =
      val hd = env.frames.head + (name -> item)
      val tl = env.frames.tail
      MapEnv(hd :: tl)

    def findItem(name: String): Option[V] =
      env.frames match {
        case hd :: tl =>
          hd.get(name) match {
            case Some(v) => Some(v)
            case None => popFrame.findItem(name)
          }
        case Nil => None
      }
