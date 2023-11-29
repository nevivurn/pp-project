package pp202302.project.common

trait EnvOps[Env, V]:
  def emptyEnv(): Env

  extension (env: Env)
    def pushEmptyFrame: Env
    def popFrame: Env
    def setItem(name: String, item: V): Env
    def findItem(name: String): Option[V]
