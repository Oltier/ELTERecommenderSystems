package hu.elte.inf.recommenderSystems.model.registration

import hu.elte.inf.recommenderSystems.config.Config.Developer

case class RegistrationTask(methodName: String, developerEmail: String, developerName: String, developerNeptun: String)

object RegistrationTask {
  def apply(methodName: String, developer: Developer): RegistrationTask = {
    new RegistrationTask(
      methodName,
      developer.developerEmail,
      developer.developerName,
      developer.developerNeptun)
  }
}
