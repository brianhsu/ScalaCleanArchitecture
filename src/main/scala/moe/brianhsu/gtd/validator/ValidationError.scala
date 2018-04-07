package moe.brianhsu.gtd.validator

class ValidationError(val error: ErrorDescription) extends Exception
trait ErrorDescription

case object IsRequired extends ErrorDescription
case object IsDuplicated extends ErrorDescription
case object IsMalformed extends ErrorDescription

case class FieldError(name: String, message: ErrorDescription) extends ErrorDescription
case class ParamError(errors: List[FieldError]) extends ErrorDescription
