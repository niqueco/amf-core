package amf.core.internal.convert

import java.util.concurrent.atomic.AtomicBoolean

trait UniqueInitializer {

  private final val initialized: AtomicBoolean = new AtomicBoolean(false)

  protected def shouldInitialize: Boolean = initialized.compareAndSet(false, true)

}
