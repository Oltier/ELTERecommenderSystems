package hu.elte.inf.recommenderSystems.model

import hu.elte.inf.recommenderSystems.model.enum.Method.Method

package object knowledgebase {
  sealed trait KnowledgeBaseMessage

  case class KnowledgeBaseEmit(channel: String, configName: Option[String] = None) extends KnowledgeBaseMessage

  case class KnowledgeBaseEmitMethod(method: Method, task: KnowledgeBaseEmit) extends KnowledgeBaseMessage
}
