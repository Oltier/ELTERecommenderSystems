package hu.elte.inf.recommenderSystems.model.enum

object Method extends CustomEnumeration with SerializableEnumeration {
  type Method = Value
  val REGISTER: Value = Value("register")
  val ADD_NODE_TYPE: Value = Value("addNodeType")
  val ADD_RELATION_TYPE: Value = Value("addRelationType")
  val DELETE_NODE_TYPE: Value = Value("deleteNodeType")
  val DELETE_RELATION_TYPE: Value = Value("deleteRelationType")
  val ADD_NODE: Value = Value("addNode")
  val ADD_RELATION: Value = Value("addRelation")
  val EMIT: Value = Value("emit")
  val GET_RECOMMENDATIONS: Value = Value("getRecommendations")
}
