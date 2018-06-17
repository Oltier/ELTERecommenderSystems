package hu.elte.inf.recommenderSystems.actor

import com.spingo.op_rabbit.{Binding, RecoveryStrategy}

trait LimitedDeliveryStrategy {
  implicit val recoveryStrategy: AnyRef with RecoveryStrategy {
    def genRetryBinding(queueName: String): Binding
  } = RecoveryStrategy.limitedRedeliver()
}
