package hu.elte.inf.recommenderSystems.model

import hu.elte.inf.recommenderSystems.model.enum.Method.Method

package object entityrelation {
  sealed trait EntityRelationMessage

  case class AddNodeType(title: String) extends EntityRelationMessage

  case class AddNodeTypeMethod(method: Method, task: AddNodeType) extends EntityRelationMessage

  case class AddRelationType(title: String, fromType: String, toType: String) extends EntityRelationMessage

  case class AddRelationTypeMethod(method: Method, task: AddRelationType) extends EntityRelationMessage

  case class DeleteNodeType(title: String) extends EntityRelationMessage

  case class DeleteNodeTypeMethod(method: Method, task: DeleteNodeType) extends EntityRelationMessage

  case class DeleteRelationType(title: String) extends EntityRelationMessage

  case class DeleteRelationTypeMethod(method: Method, task: DeleteRelationType) extends EntityRelationMessage
}
