package hu.elte.inf.recommenderSystems.model.enum

object Method extends CustomEnumeration with SerializableEnumeration {
  type Method = Value
  val REGISTER: Value = Value("register")
  val ADD_NODE_TYPE: Value = Value("addNodeType")
  val ADD_RELATION_TYPE: Value = Value("addRelationType")
  val DELETE_NODE_TYPE: Value = Value("deleteNodeType")
  val DELETE_RELATION_TYPE: Value = Value("deleteRelationType")
}
