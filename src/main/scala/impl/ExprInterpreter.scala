package pp202302.project.impl

import pp202302.project.common.{_, given}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

import Arg._
import Bind._
import Expr._
import IOAction._
import Val._

given exprInterpreter[Env, V](using
    envOps: EnvOps[Env, V],
    lazyOps: LazyOps[Val, V]
): Interpreter[Expr, V] with

  private def eval(env: Env, e: Expr): V =
    e match {
      // Simple
      case EInt(n) => VInt(n).toLazy
      case EFloat(f) => VFloat(f).toLazy
      case EString(s) => VString(s).toLazy
      case EName(x) => env.findItem(x).getOrElse(throw new Exception(s"undefined ${x}"))
      case ENil => VNil.toLazy

      // Cons
      case ECons(hd, tl) =>
        val hdv = eval(env, hd)
        val tlv = eval(env, tl)
        VCons(hdv.evaluate, tlv.evaluate).toLazy
      case EFst(e) =>
        val v = eval(env, e)
        v.evaluate match {
          case VCons(hd, _) => hd.toLazy
          case _ => throw new Exception("type mismatch")
        }
      case ESnd(e) =>
        val v = eval(env, e)
        v.evaluate match {
          case VCons(_, tl) => tl.toLazy
          case _ => throw new Exception("type mismatch")
        }

      // Preds
      case ENilP(e) =>
        val v = eval(env, e)
        v.evaluate match {
          case VNil => VInt(1).toLazy
          case _ => VInt(0).toLazy
        }
      case EIntP(e) =>
        val v = eval(env, e)
        v.evaluate match {
          case VInt(_) => VInt(1).toLazy
          case _ => VInt(0).toLazy
        }
      case EFloatP(e) =>
        val v = eval(env, e)
        v.evaluate match {
          case VFloat(_) => VInt(1).toLazy
          case _ => VInt(0).toLazy
        }
      case EStringP(e) =>
        val v = eval(env, e)
        v.evaluate match {
          case VString(_) => VInt(1).toLazy
          case _ => VInt(0).toLazy
        }
      case EPairP(e) =>
        val v = eval(env, e)
        v.evaluate match {
          case VCons(_, _) => VInt(1).toLazy
          case VNil => VInt(1).toLazy
          case _ => VInt(0).toLazy
        }

      case ESubstr(e, start, end) =>
        val v = eval(env, e)
        val sv = eval(env, start)
        val ev = eval(env, end)
        (v.evaluate, sv.evaluate, ev.evaluate) match {
          case (VString(s), VInt(sn), VInt(en)) =>
            VString(s.substring(sn, en)).toLazy
          case _ => throw new Exception("type mismatch")
        }

      case ELen(e) =>
        val v = eval(env, e)
        v.evaluate match {
          case VString(s) => VInt(s.length).toLazy
          case VCons(hd, tl) =>
            def len(ls: Val): Int =
              ls match {
                case VCons(_, tl) => 1+len(tl)
                case VNil => 0
                case _ => 1
              }
            VInt(len(VCons(hd, tl))).toLazy
          case VNil => VInt(0).toLazy
          case _ => throw new Exception("type mismatch")
        }

      case EIf(cond, ift, iff) =>
        val cv = eval(env, cond)
        cv.evaluate match {
          case VInt(0) => eval(env, iff)
          case VFloat(0.0) => eval(env, iff)
          case _ => eval(env, ift)
        }

      case ELet(bs, e) =>
        val innerEnv = bs.foldLeft(env.pushEmptyFrame)((penv, b) =>
            b match {
              case BDef(f, params, body) =>
                penv.setItem(f, VFunc(f, params, body, penv).toLazy)
              case BVal(x, e) =>
                penv.setItem(x, eval(penv, e))
              case BLVal(x, e) =>
                penv.setItem(x, lazyOps.pend(() => eval(penv, e).evaluate))
              case BDefIO(f, params, actions, returns) =>
                penv.setItem(f, VIOAction(f, params, actions, returns, penv).toLazy)
            })
        eval(innerEnv, e)

      // Actual expressions
      case EApp(f, args) =>
        val fv = eval(env, f)
        fv.evaluate match {
          case VFunc[Env](fs, params, body, fenv) =>
            if (params.length != args.length)
              throw new Exception(s"parameter arg mismatch ${fs} ${params.length} ${args.length}")
            // Add fs -> fv for recursion
            val recurEnv = fenv.pushEmptyFrame.setItem(fs, fv)
            val callEnv = params.zip(args).foldLeft(recurEnv)((penv, v) =>
                val (p, a) = v
                p match {
                  case AVName(x) => penv.setItem(x, eval(env, a))
                  case ANName(x) => penv.setItem(x, lazyOps.pend(() => eval(env, a).evaluate))
                })
            eval(callEnv, body)
          case act: VIOAction[Env] =>
            if (act.params.length != args.length)
              throw new Exception("parameter arg mismatch")
            // TODO: wtf
            VIOThunk(act, args.map(eval(env, _).evaluate)).toLazy
          case _ => throw new Exception("type mismatch")
        }
      case EAdd(l, r) =>
        val lv = eval(env, l).evaluate
        val rv = eval(env, r).evaluate
        (lv, rv) match {
          case (VFloat(f1), VFloat(f2)) => VFloat(f1 + f2).toLazy
          case (VFloat(f1), VInt(i2)) => VFloat(f1 + i2).toLazy
          case (VInt(i1), VFloat(f2)) => VFloat(i1 + f2).toLazy
          case (VInt(i1), VInt(i2)) => VInt(i1 + i2).toLazy
          case (VString(s1), VString(s2)) => VString(s1 + s2).toLazy
          case _ => throw new Exception("type mismatch")
        }
      case ESub(l, r) =>
        val lv = eval(env, l).evaluate
        val rv = eval(env, r).evaluate
        (lv, rv) match {
          case (VFloat(f1), VFloat(f2)) => VFloat(f1 - f2).toLazy
          case (VFloat(f1), VInt(i2)) => VFloat(f1 - i2).toLazy
          case (VInt(i1), VFloat(f2)) => VFloat(i1 - f2).toLazy
          case (VInt(i1), VInt(i2)) => VInt(i1 - i2).toLazy
          case _ => throw new Exception("type mismatch")
        }
      case EMul(l, r) =>
        val lv = eval(env, l).evaluate
        val rv = eval(env, r).evaluate
        (lv, rv) match {
          case (VFloat(f1), VFloat(f2)) => VFloat(f1 * f2).toLazy
          case (VFloat(f1), VInt(i2)) => VFloat(f1 * i2).toLazy
          case (VInt(i1), VFloat(f2)) => VFloat(i1 * f2).toLazy
          case (VInt(i1), VInt(i2)) => VInt(i1 * i2).toLazy
          case _ => throw new Exception("type mismatch")
        }
      case EDiv(l, r) =>
        val lv = eval(env, l).evaluate
        val rv = eval(env, r).evaluate
        (lv, rv) match {
          case (VFloat(f1), VFloat(f2)) => VFloat(f1 / f2).toLazy
          case (VFloat(f1), VInt(i2)) => VFloat(f1 / i2).toLazy
          case (VInt(i1), VFloat(f2)) => VFloat(i1 / f2).toLazy
          case (VInt(i1), VInt(i2)) => VInt(i1 / i2).toLazy
          case _ => throw new Exception("type mismatch")
        }
      case EMod(l, r) =>
        val lv = eval(env, l).evaluate
        val rv = eval(env, r).evaluate
        (lv, rv) match {
          /*
          case (VFloat(f1), VFloat(f2)) => VFloat(f1 % f2).toLazy
          case (VFloat(f1), VInt(i2)) => VFloat(f1 % i2).toLazy
          case (VInt(i1), VFloat(f2)) => VFloat(i1 % f2).toLazy
          */
          case (VInt(i1), VInt(i2)) => VInt(i1 % i2).toLazy
          case _ => throw new Exception("type mismatch")
        }
      case EEq(l, r) =>
        val lv = eval(env, l).evaluate
        val rv = eval(env, r).evaluate
        (lv, rv) match {
          case (VFloat(f1), VFloat(f2)) if f1 == f2 => VInt(1).toLazy
          case (VFloat(f1), VInt(i2)) if (f1 == i2) => VInt(1).toLazy
          case (VInt(i1), VFloat(f2)) if i1 == f2 => VInt(1).toLazy
          case (VInt(i1), VInt(i2)) if i1 == i2 => VInt(1).toLazy
          case (VString(s1), VString(s2)) if s1 == s2 => VInt(1).toLazy
          case _ => VInt(0).toLazy
        }
      case ELt(l, r) =>
        val lv = eval(env, l).evaluate
        val rv = eval(env, r).evaluate
        (lv, rv) match {
          case (VFloat(f1), VFloat(f2)) =>
            if f1 < f2 then VInt(1).toLazy else VInt(0).toLazy
          case (VFloat(f1), VInt(i2)) =>
            if f1 < i2 then VInt(1).toLazy else VInt(0).toLazy
          case (VInt(i1), VFloat(f2)) =>
            if i1 < f2 then VInt(1).toLazy else VInt(0).toLazy
          case (VInt(i1), VInt(i2)) =>
            if i1 < i2 then VInt(1).toLazy else VInt(0).toLazy
          case _ => throw new Exception("type mismatch")
        }
      case EGt(l, r) =>
        val lv = eval(env, l).evaluate
        val rv = eval(env, r).evaluate
        (lv, rv) match {
          case (VFloat(f1), VFloat(f2)) =>
            if f1 > f2 then VInt(1).toLazy else VInt(0).toLazy
          case (VFloat(f1), VInt(i2)) =>
            if f1 > i2 then VInt(1).toLazy else VInt(0).toLazy
          case (VInt(i1), VFloat(f2)) =>
            if i1 > f2 then VInt(1).toLazy else VInt(0).toLazy
          case (VInt(i1), VInt(i2)) =>
            if i1 > i2 then VInt(1).toLazy else VInt(0).toLazy
          case _ => throw new Exception("type mismatch")
        }
    }

  extension (e: Expr)
    def evaluate(): Try[V] = {
      try {
        Success(eval(envOps.emptyEnv(), e))
      } catch {
        case e: Exception => Failure(e)
      }
    }

    def interpret[R, W](reader: R, writer: W)(using
        showOps: Show[V],
        readOps: Reader[R],
        writeOps: Writer[W]
    ): Try[V] = {
      try {
        val result = eval(envOps.emptyEnv(), e).evaluate
        result match {
          case thunk: VIOThunk[Env] =>
            def runIO(thunk: VIOThunk[Env]): V =
              val action = thunk.action
              val args = thunk.args

              // For recursion
              val recurEnv = action.env.pushEmptyFrame.setItem(action.actionName, action.toLazy)
              // For args
              val callEnv = action.params.zip(args).foldLeft(recurEnv)((penv, v) =>
                  val (p, a) = v
                  penv.setItem(p.x, a.toLazy))

              // Run
              val finEnv = action.actions.foldLeft(callEnv)((penv, act) =>
                  act match {
                    case IORun(x, e) =>
                      val v = eval(penv, e)
                      val vv = v.evaluate match {
                        case ith: VIOThunk[Env] => runIO(ith)
                        case _ => v
                      }
                      penv.setItem(x, vv)
                    case IOReadLine(x) =>
                      def readline: String =
                        reader.readChar() match {
                          case Some(c) =>
                            if c == '\r' || c == '\n'
                            then ""
                            else c + readline
                          case None => ""
                        }
                      val s = readline
                      penv.setItem(x, VString(s).toLazy)
                    case IOPrint(e) =>
                      val s = eval(penv, e).evaluate.show
                      s.foreach(writer.writeChar)
                      penv
                  })
              eval(finEnv, action.returns)
            Success(runIO(thunk))

          case r => Success(r.toLazy)
        }
      } catch {
        case e: Exception => Failure(e)
      }
    }
