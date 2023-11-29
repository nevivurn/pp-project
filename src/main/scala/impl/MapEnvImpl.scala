package pp202302.project.impl

import pp202302.project.common.MapEnv
import pp202302.project.common._

import scala.annotation.tailrec

given mapEnvImpl[V]: EnvOps[MapEnv[V], V] with
  def emptyEnv() = ???

  extension (env: MapEnv[V])
    def pushEmptyFrame = ???

    def popFrame: MapEnv[V] = ???

    def setItem(name: String, item: V): MapEnv[V] = ???

    def findItem(name: String): Option[V] = ???
