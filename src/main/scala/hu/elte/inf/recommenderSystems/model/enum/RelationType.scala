package hu.elte.inf.recommenderSystems.model.enum

import hu.elte.inf.recommenderSystems.model.enum

object RelationType extends CustomEnumeration with SerializableEnumeration {

  type RelationType = Value

  val PERSON_GENDER: enum.RelationType.Value = Value("PersonGender")
  val PERSON_OCCUPATION: enum.RelationType.Value = Value("PersonOccupation")
  val PERSON_AGE_CATEGORY: enum.RelationType.Value = Value("PersonAgeCategory")
  val PERSON_ZIP_CODE_REGION: enum.RelationType.Value = Value("PersonZipCodeRegion")
  val ITEM_GENRE: enum.RelationType.Value = Value("ItemGenre")
  val ITEM_YEAR_OF_PUBLISHING: enum.RelationType.Value = Value("ItemYearOfPublishing")
  val ITEM_RATING: enum.RelationType.Value = Value("ItemRating")

}
