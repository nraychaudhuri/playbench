package scala.concurrent 

package object duration {
  type Duration = scala.concurrent.util.Duration
  def Duration(timeout: Long, unit: java.util.concurrent.TimeUnit) = scala.concurrent.util.Duration(timeout, unit)
}
