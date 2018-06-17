package hu.elte.inf.recommenderSystems.model.enum

import hu.elte.inf.recommenderSystems.model.enum

object NodeType extends CustomEnumeration with SerializableEnumeration {
  type NodeType = Value

  val PERSON: Value = Value("Person")
  val GENDER: enum.NodeType.Value = Value("Gender")
  val OCCUPATION: enum.NodeType.Value = Value("Occupation")
  val AGE_CATEGORY: enum.NodeType.Value = Value("AgeCategory")
  val ZIP_CODE_REGION: enum.NodeType.Value = Value("ZipCodeRegion")
  val ITEM: enum.NodeType.Value = Value("Item")
  val GENRE: enum.NodeType.Value = Value("Genre")

}
