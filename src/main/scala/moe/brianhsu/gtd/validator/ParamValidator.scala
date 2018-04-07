package moe.brianhsu.gtd.validator

object ParamValidator {

  type Validation[T] = T => Option[ErrorDescription]
  type ValidationRequest = () => List[FieldError]

  def checkParams(validationRequest: ValidationRequest*): Option[ValidationError] = {
    val errors: List[FieldError] = validationRequest.flatMap(f => f()).toList
    errors match {
      case Nil => None
      case _   => Some(new ValidationError(ParamError(errors)))
    }
  }

  def forField[T](name: String, value: T, validations: Validation[T]*): ValidationRequest = { () =>
    val fieldErrors = for {
      validation <- validations
      error <- validation(value)
    } yield FieldError(name, error)

    fieldErrors.headOption.toList
  }

  object EmailValidator extends Validation[String] {
  
    private val emailRegex = (
      """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+""" +
      """@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?""" +
      """(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""
    ).r
  
    def apply(email: String): Option[ErrorDescription] =
      Option(email)
        .filter(e => emailRegex.findFirstMatchIn(e).isEmpty)
        .map(_ => IsMalformed)
  }

  object NonEmptyString extends Validation[String] {
    def apply(value: String): Option[ErrorDescription] = {
      value match {
        case null => Some(IsRequired)
        case s if s.trim.isEmpty => Some(IsRequired)
        case _ => None
      }
    }
  }
}

