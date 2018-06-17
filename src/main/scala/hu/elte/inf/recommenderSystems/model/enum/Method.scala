package hu.elte.inf.recommenderSystems.model.enum

object Method extends CustomEnumeration with SerializableEnumeration {
  type Method = Value
  val REGISTER: Value = Value("register")
  val ADD_NODE_TYPE: Value = Value("addNodeType")
}
