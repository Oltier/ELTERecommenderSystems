package hu.elte.inf.recommenderSystems.model.registration

import hu.elte.inf.recommenderSystems.model.enum.Method.Method

case class RegistrationMessage(method: Method, task: RegistrationTask)